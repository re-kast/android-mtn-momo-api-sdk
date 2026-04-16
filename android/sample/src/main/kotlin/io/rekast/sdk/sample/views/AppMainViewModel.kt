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

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.ProductType
import io.rekast.sdk.utils.Settings
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for managing authentication and API interactions.
 *
 * This ViewModel handles setting authentication credentials and making API calls.
 *
 * @property defaultRepository The repository for handling API calls related to MTN MOMO.
 * @property context The application context for accessing resources and utilities.
 * @property settings The settings utility for managing application settings.
 */
@HiltViewModel
open class AppMainViewModel @Inject constructor(
    private val defaultRepository: DefaultRepository,
    @param:ApplicationContext private val context: Context,
    private val settings: Settings,
    private val dispatchers: DispatcherProvider,
    private val sampleConfig: SampleConfig
) : ViewModel() {

    /**
     * Sets the Basic Authentication credentials.
     *
     * @param apiUserId The API user ID.
     * @param apiKey The API key.
     */
    fun setBasicAuth(apiUserId: String, apiKey: String) {
        val basicAuthCredentials = BasicAuthCredentials(apiUserId, apiKey)
        viewModelScope.launch {
            defaultRepository.setUpBasicAuth(basicAuthCredentials)
        }
    }

    /**
     * Checks the user status and creates an API user if necessary.
     *
     * This method checks if the API user exists and creates a new one if it does not.
     */
    fun checkUser() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.COLLECTION, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            defaultRepository.checkApiUser(sampleConfig.apiVersionV1, productType).collect { apiUser ->
                when (apiUser) {
                    is NetworkResult.Success -> {
                        createApiKey()
                    }

                    is NetworkResult.Error -> {
                        Timber.e(apiUser.message)
                        val providerCallBackHost = ProviderCallBackHost(providerCallbackHost = sampleConfig.providerCallbackHost)
                        defaultRepository.createApiUser(providerCallBackHost, sampleConfig.apiVersionV1, sampleConfig.apiUserId, productType).collect { newApiUser ->
                            when (newApiUser) {
                                is NetworkResult.Success -> {
                                    checkUser()
                                }

                                is NetworkResult.Error -> {
                                    Timber.e("New Api user was not created %s", newApiUser.message)
                                }

                                else -> {
                                    Timber.e("An error occurred")
                                }
                            }
                        }
                    }

                    else -> {
                        Timber.e("An error occurred")
                    }
                }
            }
        }
    }

    /**
     * Creates an API key for the user.
     *
     * This method retrieves the API key for the user and sets up basic authentication.
     */
    private fun createApiKey() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            val apiUserKey = Utils.getApiKey(context)
            if (apiUserKey.isNotBlank()) {
                setBasicAuth(apiUserId = sampleConfig.apiUserId, apiKey = apiUserKey)
                getAccessToken()
            } else {
                defaultRepository.createApiKey(apiVersion = sampleConfig.apiVersionV1, productSubscriptionKey = productType).collect { apiKey ->
                    when (apiKey) {
                        is NetworkResult.Success -> {
                            try {
                                val newApiKey = apiKey.response?.apiKey.orEmpty()
                                Utils.saveApiKey(context = context, apiKey = newApiKey)
                                setBasicAuth(apiUserId = sampleConfig.apiUserId, apiKey = newApiKey)
                                Timber.d("Api Key fetched and saved successfully")
                                getAccessToken()
                            } catch (exception: Exception) {
                                Timber.e("An Error occurred %s", exception.message)
                            }
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Api Key creation failed %s", apiKey.message)
                        }

                        else -> {
                            Timber.e("Api Key creation failed")
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the access token for the user.
     *
     * This method checks if the access token is available and retrieves it if not.
     */
    private fun getAccessToken() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            val apiUserKey = context.let { Utils.getApiKey(it) }
            val accessToken = context.let { Utils.getAccessToken(it) }

            if (apiUserKey.isNotBlank() && accessToken.isBlank()) {
                defaultRepository.getAccessToken(productSubscriptionKey = productType, productType = ProductType.REMITTANCE.productType).collect { accessToken ->
                    when (accessToken) {
                        is NetworkResult.Success -> {
                            try {
                                Utils.saveAccessToken(context, accessToken.response)
                                this@AppMainViewModel.setBasicAuth("", "")
                                Timber.d("Access token created and saved successfully")
                                getOauthAccessToken()
                            } catch (exception: Exception) {
                                Timber.e("An Error occurred %s", exception.message)
                            }
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Access token creation failed %s", accessToken.message)
                        }

                        else -> {
                            Timber.e("Access token creation failed")
                        }
                    }
                }
            } else {
                this@AppMainViewModel.setBasicAuth("", "")
                Timber.d("A valid access token was found")
                getOauthAccessToken()
            }
        }
    }

    /**
     * Retrieves the Oauth access token for the user.
     *
     * This method checks if the access token is available and retrieves it if not.
     */
    private fun getOauthAccessToken() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.COLLECTION, sampleConfig)
        viewModelScope.launch(dispatchers.io()) {
            val userAccessToken = Utils.getAccessToken(context)
            val userOauthAccessToken = Utils.getOauthAccessToken(context)

            if (userAccessToken.isNotBlank() && userOauthAccessToken.isBlank()) {
                defaultRepository.getOauthAccessToken(productType = ProductType.COLLECTION.productType, productSubscriptionKey = productType, environment = sampleConfig.environment).collect { oauthAccessToken ->
                    when (oauthAccessToken) {
                        is NetworkResult.Success -> {
                            try {
                                Utils.saveOauth2AccessToken(context, oauthAccessToken.response)
                                Timber.d("Oauth2 Access token created and saved successfully")
                            } catch (exception: Exception) {
                                Timber.e("An Error occurred %s", exception.message)
                            }
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Oauth2 Access token creation failed %s", oauthAccessToken.message)
                        }

                        else -> {
                            Timber.e("Oauth2 Access token creation failed")
                        }
                    }
                }
            } else {
                Timber.d("A valid Oauth2 access token was found")
            }
        }
    }

    /*
    private fun getUserInfoWithConsent() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getUserInfoWithConsent(
                Settings().getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is MomoResponse.Success -> {
                        val getUserInfoWithoutConsent = momoAPIResult.value
                        Timber.d(getUserInfoWithoutConsent.toString())
                    }
                    is MomoResponse.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    /**
     * Requests a refund for a transaction.
     *
     * @param requestToPayUuid The UUID of the request to pay.
     */
    fun refund(requestToPayUuid: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        val transactionUuid = Settings().generateUUID()
        if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(requestToPayUuid)) {
            val creditTransaction = createRefundTransaction(requestToPayUuid)
            accessToken?.let {
                defaultRepository.refund(
                    it,
                    creditTransaction,
                    BuildConfig.MOMO_API_VERSION_V2,
                    Settings().getProductSubscriptionKeys(ProductType.DISBURSEMENTS),
                    transactionUuid
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is MomoResponse.Success -> {
                            getRefundStatus(transactionUuid)
                        }
                        is MomoResponse.Failure -> {
                            val momoAPIException = momoAPIResult.momoException
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the status of a refund transaction.
     *
     * @param referenceId The reference ID of the transaction.
     */
    private fun getRefundStatus(referenceId: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                defaultRepository.getRefundStatus(
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    Settings().getProductSubscriptionKeys(ProductType.DISBURSEMENTS),
                    it
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is MomoResponse.Success -> {
                            val completeTransfer =
                                Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), MomoTransaction::class.java)
                            Timber.d(completeTransfer.toString())
                        }
                        is MomoResponse.Failure -> {
                            val momoAPIException = momoAPIResult.momoException
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a refund transaction.
     *
     * @param requestToPayUuid The UUID of the request to pay.
     * @return A MomoTransaction object representing the refund transaction.
     */
    private fun createRefundTransaction(requestToPayUuid: String): MomoTransaction {
        return MomoTransaction(
            amount = "30",
            currency = "EUR",
            financialTransactionId = null,
            externalId = Settings().generateUUID(),
            payee = null,
            payer = null,
            payerMessage = "Testing",
            payeeNote = "The Good Company",
            status = null,
            reason = null,
            referenceIdToRefund = requestToPayUuid
        )
    }*/
}
