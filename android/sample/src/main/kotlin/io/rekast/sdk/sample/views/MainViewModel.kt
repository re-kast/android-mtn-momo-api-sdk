/*
 * Copyright 2023-2026, Benjamin Mwalimu
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
import io.rekast.sdk.model.BcAuthorizeRequest
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.Constants
import io.rekast.sdk.utils.ProductType
import io.rekast.sdk.utils.Settings
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
 * tokens are refreshed automatically by `io.rekast.sdk.app.network.TokenAuthenticator` on 401 — no
 * manual re-bootstrap is needed after first launch.
 *
 * Each step uses a cold [kotlinx.coroutines.flow.Flow] from [io.rekast.sdk.repository.DefaultRepository].
 * Flows emit [io.rekast.sdk.repository.data.NetworkResult.Loading] first, then a terminal
 * [io.rekast.sdk.repository.data.NetworkResult.Success] or [io.rekast.sdk.repository.data.NetworkResult.Error].
 *
 * [isBootstrapComplete] is a [StateFlow] that becomes `true` when the bootstrap chain terminates —
 * either because all steps succeeded or because a step failed with no further retry. Observers
 * (such as [io.rekast.sdk.sample.views.home.HomeScreenFragment]) should wait for the first `true`
 * emission before making API calls, ensuring they never race against credential provisioning.
 */
@HiltViewModel
open class MainViewModel @Inject constructor(
    private val defaultRepository: DefaultRepository,
    private val credentialStorage: CredentialStorage,
    private val settings: Settings,
    private val dispatchers: DispatcherProvider,
    private val sampleConfig: SampleConfig
) : ViewModel() {

    private val _isBootstrapComplete = MutableStateFlow(false)

    /**
     * Becomes `true` when the bootstrap chain initiated by [checkUser] reaches any terminal state —
     * success or failure. Reset to `false` at the start of each [checkUser] call.
     *
     * Observers should use `isBootstrapComplete.first { it }` (suspending) or collect with a
     * `filter { it }` predicate so they fire as soon as bootstrap completes without busy-waiting.
     */
    val isBootstrapComplete: StateFlow<Boolean> = _isBootstrapComplete.asStateFlow()

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
     * and the chain stops — [isBootstrapComplete] is set to `true` to unblock any waiting observers.
     * The user will remain uncredentialled until [checkUser] is called again.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun checkUser() {
        _isBootstrapComplete.value = false
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

                        is NetworkResult.Error -> {
                            Timber.e("API user creation failed: %s", result.message)
                            _isBootstrapComplete.value = true
                        }

                        is NetworkResult.Loading -> {}
                    }
                }
        }
    }

    /**
     * Creates and stores the API key for the current API user — then advances to [getAccessToken].
     *
     * Short-circuits immediately if an API key is already present in [CredentialStorage]:
     * an existing key is still valid and re-creating it would invalidate any previously issued tokens.
     *
     * On success, persists the raw key string via [CredentialStorage.saveApiKey]. Failure is
     * terminal for this bootstrap run — the key creation error is logged, [isBootstrapComplete] is
     * set to `true` to unblock any waiting observers, and [getAccessToken] is not called. The user
     * will remain without credentials until [checkUser] is invoked again.
     *
     * Uses the [io.rekast.sdk.utils.ProductType.REMITTANCE] subscription key, matching the token
     * that will be obtained in [getAccessToken].
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
                            _isBootstrapComplete.value = true
                        }
                    }

                    is NetworkResult.Error -> {
                        Timber.e("API key creation failed: %s", result.message)
                        _isBootstrapComplete.value = true
                    }

                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    /**
     * Fetches and stores the Bearer access token — then advances to [getOauthAccessToken].
     *
     * Short-circuits in two cases:
     * - No API key is in [CredentialStorage] (cannot form a Basic Auth request without it).
     * - A non-expired access token is already stored — the skip is logged and [getOauthAccessToken]
     *   is called immediately so the bootstrap chain can continue to the OAuth2 step.
     *
     * On success, the token is persisted via [CredentialStorage.saveAccessToken], which stores both
     * the raw token string and its absolute expiry timestamp. Subsequent requests attach the token
     * via [io.rekast.sdk.network.interceptor.auth.AccessTokenInterceptor]; expired tokens are
     * refreshed automatically by [io.rekast.sdk.app.network.TokenAuthenticator] on 401 — no manual
     * re-invocation is needed after the first successful bootstrap.
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
                                _isBootstrapComplete.value = true
                            }
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Access token fetch failed: %s", result.message)
                            _isBootstrapComplete.value = true
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
     * Initiates a backchannel (CIBA) authorization request and stores the resulting `auth_req_id`
     * and `loginHint` for use by [getOauthAccessToken].
     *
     * This is the CIBA leg of the OAuth2 bootstrap: the MTN MoMo API sends an authorization
     * prompt to the subscriber's phone. Once the user approves on-device, the `auth_req_id`
     * can be exchanged for an OAuth2 access token via [getOauthAccessToken].
     *
     * On success, both [CredentialStorage.saveBackChannelAuthorizationRequestId] (with its expiry)
     * and [CredentialStorage.saveLoginHint] are written, then [getOauthAccessToken] is called
     * immediately to attempt the token exchange. If the MTN MoMo endpoint returns a non-2xx
     * response the error is logged and [isBootstrapComplete] is set to `true` — the caller should
     * retry [bcAuthorize] after correcting the login hint.
     *
     * `io.rekast.sdk.app.network.TokenAuthenticator` also calls the bc-authorize endpoint directly
     * (bypassing this method) when it detects an expired `auth_req_id` on a 401 response, using
     * the login hint stored by this method.
     *
     * @param request The authorization request parameters including the user's MSISDN login hint.
     */
    fun bcAuthorize(request: BcAuthorizeRequest) {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            defaultRepository.bcAuthorize(
                productType = ProductType.REMITTANCE.productType,
                apiVersion = sampleConfig.apiVersionV1,
                bcAuthorizeRequest = request,
                productSubscriptionKey = productType,
                environment = sampleConfig.environment
            ).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        result.response?.let { response ->
                            credentialStorage.saveBackChannelAuthorizationRequestId(response.authReqId, response.expiresIn)
                            credentialStorage.saveLoginHint(request.loginHint)
                            Timber.d("BC authorize request ID saved")
                            getOauthAccessToken()
                        } ?: run { _isBootstrapComplete.value = true }
                    }

                    is NetworkResult.Error -> {
                        Timber.e("BC authorize failed: %s", result.message)
                        _isBootstrapComplete.value = true
                    }

                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    /**
     * Exchanges the stored `auth_req_id` for an OAuth2 access token and persists the result.
     *
     * The method navigates three states in order:
     * 1. **OAuth2 token valid** — [CredentialStorage.getOauthAccessToken] is non-blank; logs and
     *    sets [isBootstrapComplete] to `true` immediately. The token was either just written by a
     *    previous call or has not yet expired.
     * 2. **`auth_req_id` missing** — [CredentialStorage.getBackChannelAuthorizationRequestId] is
     *    blank; attempts to re-initiate CIBA using the stored login hint by calling [bcAuthorize].
     *    If no login hint is stored either, logs a warning and sets [isBootstrapComplete] to `true`
     *    — the caller must invoke [bcAuthorize] explicitly with a valid login hint first.
     * 3. **`auth_req_id` present** — calls [DefaultRepository.getOauthAccessToken] to exchange
     *    the request ID for a token. On success, persists the token via
     *    [CredentialStorage.saveOauthAccessToken]. In all terminal cases (success or error),
     *    sets [isBootstrapComplete] to `true`.
     *
     * Requires a non-expired Bearer access token to be present in [CredentialStorage] — the
     * OAuth2 token endpoint uses Bearer auth, not Basic Auth.
     */
    private fun getOauthAccessToken() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            val accessToken = credentialStorage.getAccessToken()
            val oauthToken = credentialStorage.getOauthAccessToken()
            val backChannelAuthorizationRequestId = credentialStorage.getBackChannelAuthorizationRequestId()

            when {
                oauthToken.isNotBlank() -> {
                    Timber.d("Valid OAuth2 token already stored")
                    _isBootstrapComplete.value = true
                }

                backChannelAuthorizationRequestId.isBlank() -> {
                    val loginHint = credentialStorage.getLoginHint()
                    if (loginHint.isNotBlank()) {
                        // bcAuthorize (or the inner getOauthAccessToken it calls) will set
                        // isBootstrapComplete = true when it reaches its own terminal state.
                        bcAuthorize(BcAuthorizeRequest(loginHint = loginHint, scope = Constants.FormFields.CIBA_SCOPE, accessType = Constants.FormFields.CIBA_ACCESS_TYPE))
                    } else {
                        Timber.w("OAuth2 token fetch skipped: call bcAuthorize() with a login hint first")
                        _isBootstrapComplete.value = true
                    }
                }

                accessToken.isNotBlank() -> {
                    defaultRepository.getOauthAccessToken(
                        productType = ProductType.REMITTANCE.productType,
                        productSubscriptionKey = productType,
                        environment = sampleConfig.environment,
                        backChannelAuthorizationRequestId = backChannelAuthorizationRequestId
                    ).collect { result ->
                        when (result) {
                            is NetworkResult.Success -> {
                                try {
                                    credentialStorage.saveOauthAccessToken(result.response)
                                    Timber.d("OAuth2 token saved")
                                } catch (e: Exception) {
                                    Timber.e(e, "Failed to save OAuth2 token")
                                }
                                _isBootstrapComplete.value = true
                            }

                            is NetworkResult.Error -> {
                                Timber.e("OAuth2 token fetch failed: %s", result.message)
                                _isBootstrapComplete.value = true
                            }

                            is NetworkResult.Loading -> {}
                        }
                    }
                }

                else -> _isBootstrapComplete.value = true
            }
        }
    }
}
