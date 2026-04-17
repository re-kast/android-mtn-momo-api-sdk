/*
 * Copyright 2023-2024, Benjamin Mwalimu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rekast.sdk.sample.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.ProductType
import io.rekast.sdk.utils.Settings
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel that bootstraps authentication on first launch.
 *
 * Responsible for the one-time credential provisioning sequence:
 * 1. **Check/create API user** — verifies the sandbox user exists; creates it if not.
 * 2. **Create API key** — skipped if one is already stored in [CredentialStorage].
 * 3. **Fetch access token** — skipped if a non-expired token is already stored.
 * 4. **Fetch OAuth2 token** — skipped if a non-expired token is already stored.
 *
 * All credentials are written to [CredentialStorage] (EncryptedSharedPreferences) and then read
 * back on every SDK request via [io.rekast.sdk.network.interfaces.CredentialProvider]. Expired
 * tokens are refreshed automatically by [io.rekast.sdk.app.di.TokenAuthenticator] on 401 — no
 * manual re-bootstrap is needed after first launch.
 *
 * Each step uses a cold [kotlinx.coroutines.flow.Flow] from [io.rekast.sdk.repository.DefaultRepository].
 * Flows emit [io.rekast.sdk.repository.data.NetworkResult.Loading] first, then a terminal
 * [io.rekast.sdk.repository.data.NetworkResult.Success] or [io.rekast.sdk.repository.data.NetworkResult.Error].
 */
@HiltViewModel
open class MainViewModel @Inject constructor(
    private val defaultRepository: DefaultRepository,
    private val credentialStorage: CredentialStorage,
    private val settings: Settings,
    private val dispatchers: DispatcherProvider,
    private val sampleConfig: SampleConfig
) : ViewModel() {

    /**
     * Checks whether the API user exists and, if not, creates it — then advances to [createApiKey].
     *
     * Uses [kotlinx.coroutines.flow.flatMapLatest] to flatten the check → create sequence into a
     * single flow without nesting `.collect` calls:
     * - `checkApiUser` Success → passes through; the outer `.collect` calls [createApiKey].
     * - `checkApiUser` Error → switches the active inner flow to `createApiUser`; the outer
     *   `.collect` calls [createApiKey] once that succeeds.
     * - `Loading` from either call → passed through to the outer collect and ignored (no UI state
     *   is managed by this ViewModel).
     *
     * The bootstrap sequence does not repeat: if `createApiUser` also fails, the error is logged
     * and the chain stops. The user will remain uncredentialled until [checkUser] is called again.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun checkUser() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.COLLECTION, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            defaultRepository.checkApiUser(sampleConfig.apiVersionV1, productType)
                .flatMapLatest { result ->
                    when (result) {
                        is NetworkResult.Success -> flowOf(result)

                        is NetworkResult.Error -> {
                            Timber.e(result.message)
                            val callbackHost = ProviderCallBackHost(providerCallbackHost = sampleConfig.providerCallbackHost)
                            defaultRepository.createApiUser(callbackHost, sampleConfig.apiVersionV1, sampleConfig.apiUserId, productType)
                        }

                        is NetworkResult.Loading -> flowOf(result)
                    }
                }
                .collect { result ->
                    when (result) {
                        is NetworkResult.Success -> createApiKey()
                        is NetworkResult.Error -> Timber.e("API user creation failed: %s", result.message)
                        is NetworkResult.Loading -> {}
                    }
                }
        }
    }

    /**
     * Fetches and stores the API key if one is not already saved.
     */
    private fun createApiKey() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            val existingKey = credentialStorage.getApiKey()
            if (existingKey.isNotBlank()) {
                getAccessToken()
                return@launch
            }

            defaultRepository.createApiKey(apiVersion = sampleConfig.apiVersionV1, productSubscriptionKey = productType).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        try {
                            val newKey = result.response?.apiKey.orEmpty()
                            credentialStorage.saveApiKey(newKey)
                            Timber.d("API key saved")
                            getAccessToken()
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to save API key")
                        }
                    }

                    is NetworkResult.Error -> {
                        Timber.e("API key creation failed: %s", result.message)
                    }

                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    /**
     * Fetches and stores the access token if one is not already valid.
     * The [io.rekast.sdk.app.di.TokenAuthenticator] handles subsequent refreshes automatically.
     */
    private fun getAccessToken() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            val apiKey = credentialStorage.getApiKey()
            val accessToken = credentialStorage.getAccessToken()

            if (apiKey.isNotBlank() && accessToken.isBlank()) {
                defaultRepository.getAccessToken(productSubscriptionKey = productType, productType = ProductType.REMITTANCE.productType).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            try {
                                credentialStorage.saveAccessToken(result.response)
                                Timber.d("Access token saved")
                                getOauthAccessToken()
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to save access token")
                            }
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Access token fetch failed: %s", result.message)
                        }

                        is NetworkResult.Loading -> {}
                    }
                }
            } else {
                Timber.d("Valid access token already stored")
                getOauthAccessToken()
            }
        }
    }

    /**
     * Fetches and stores the OAuth2 access token if one is not already valid.
     */
    private fun getOauthAccessToken() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            val accessToken = credentialStorage.getAccessToken()
            val oauthToken = credentialStorage.getOauthAccessToken()

            if (accessToken.isNotBlank() && oauthToken.isBlank()) {
                defaultRepository.getOauthAccessToken(
                    productType = ProductType.REMITTANCE.productType,
                    productSubscriptionKey = productType,
                    environment = sampleConfig.environment
                ).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            try {
                                credentialStorage.saveOauthAccessToken(result.response)
                                Timber.d("OAuth2 token saved")
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to save OAuth2 token")
                            }
                        }

                        is NetworkResult.Error -> {
                            Timber.e("OAuth2 token fetch failed: %s", result.message)
                        }

                        is NetworkResult.Loading -> {}
                    }
                }
            } else {
                Timber.d("Valid OAuth2 token already stored")
            }
        }
    }
}
