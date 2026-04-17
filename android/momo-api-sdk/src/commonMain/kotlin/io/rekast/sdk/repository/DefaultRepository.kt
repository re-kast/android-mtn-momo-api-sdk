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
package io.rekast.sdk.repository

import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.MomoNotification
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.ApiKey
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.DisbursementsService
import io.rekast.sdk.repository.data.DataResponse
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.utils.ApiConfig
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * The DefaultRepository class is responsible for making network calls to the MTN MOMO APIs.
 *
 * This class holds all the common API methods for various MTN MOMO products, including
 * user management, transaction processing, and status checks.
 *
 * @property defaultSource The source for handling API calls related to user management and authentication.
 * @property disbursementsService The service for disbursement-related API calls.
 * @property collection The service for collection-related API calls.
 */
@Singleton
class DefaultRepository @Inject constructor(private val defaultSource: DefaultSource, private val disbursementsService: DisbursementsService, private val collection: CollectionService, private val config: ApiConfig) :
    DataResponse() {

    /**
     * Wraps a Retrofit suspend call in a cold [Flow] that always emits two items:
     * 1. [NetworkResult.Loading] — emitted immediately so that collectors can show a loading indicator.
     * 2. [NetworkResult.Success] or [NetworkResult.Error] — the terminal result from [safeApiCall].
     *
     * The flow runs entirely on [Dispatchers.IO]; collectors need not specify their own dispatcher.
     *
     * @param apiCall The suspend lambda that performs the Retrofit call and returns a [Response].
     * @return A cold [Flow] that emits exactly two [NetworkResult] values then completes.
     */
    private fun <T> executeApiCall(apiCall: suspend () -> Response<T>): Flow<NetworkResult<T>> = flow {
        emit(NetworkResult.Loading())
        emit(safeApiCall { apiCall() })
    }.flowOn(Dispatchers.IO)

    /**
     * Creates a new API user.
     *
     * @param providerCallBackHost The callback host for the provider.
     * @param apiVersion The version of the API to use.
     * @param uuid A unique identifier for the request.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Flow] emitting a [NetworkResult] containing the created [ApiUser].
     */
    fun createApiUser(providerCallBackHost: ProviderCallBackHost, apiVersion: String, uuid: String, productSubscriptionKey: String): Flow<NetworkResult<ApiUser>> =
        executeApiCall { defaultSource.createApiUser(providerCallBackHost = providerCallBackHost, apiVersion = apiVersion, uuid = uuid, productSubscriptionKey = productSubscriptionKey) }

    /**
     * Checks whether the supplied API user exists.
     *
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Flow] emitting a [NetworkResult] containing the [ApiUser] if found.
     */
    fun checkApiUser(apiVersion: String, productSubscriptionKey: String): Flow<NetworkResult<ApiUser>> =
        executeApiCall { defaultSource.getApiUser(apiVersion, userId = config.apiUserId, productSubscriptionKey = productSubscriptionKey) }

    /**
     * Gets the API Key based on the ApiUser Id and OCP Subscription Id.
     *
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Flow] emitting a [NetworkResult] containing the [ApiKey].
     */
    fun createApiKey(apiVersion: String, productSubscriptionKey: String): Flow<NetworkResult<ApiKey>> =
        executeApiCall { defaultSource.createApiKey(apiVersion = apiVersion, userId = config.apiUserId, productSubscriptionKey = productSubscriptionKey) }

    /**
     * Gets the Access Token based on the ApiUser ID, OCP Subscription Id, and the API Key.
     *
     * @param productSubscriptionKey The subscription key for the product.
     * @param productType The type of product for which to obtain the access token.
     * @return A [Response] containing the obtained [AccessToken].
     */
    fun getAccessToken(productSubscriptionKey: String, productType: String): Flow<NetworkResult<AccessToken>> =
        executeApiCall { defaultSource.getAccessToken(productType = productType, productSubscriptionKey = productSubscriptionKey) }

    /**
     * Obtains an OAuth2 access token for use with consent-based API endpoints.
     *
     * @param productType The type of product for which to obtain the OAuth2 access token.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A [Flow] emitting a [NetworkResult] containing the obtained [Oauth2AccessToken].
     */
    fun getOauthAccessToken(productType: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Oauth2AccessToken>> =
        executeApiCall { defaultSource.getOauth2AccessToken(productType = productType, productSubscriptionKey = productSubscriptionKey, environment = environment) }

    /**
     * Retrieves the basic user information for a specified MTN MOMO user.
     *
     * @param productType The type of product for which to retrieve the user information.
     * @param apiVersion The version of the API to use.
     * @param accountHolder The MSISDN or other identifier for the account holder.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A [Flow] emitting a [NetworkResult] containing the [BasicUserInfo] of the specified user.
     */
    fun getBasicUserInfo(productType: String, apiVersion: String, accountHolder: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<BasicUserInfo>> = executeApiCall {
        defaultSource.getBasicUserInfo(productType = productType, apiVersion = apiVersion, accountHolder = accountHolder, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Retrieves extended user information for a MTN MOMO user who has granted consent.
     *
     * @param productType The type of product for which to retrieve the user information.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A [Flow] emitting a [NetworkResult] containing the [UserInfoWithConsent] of the user.
     */
    fun getUserInfoWithConsent(productType: String, apiVersion: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<UserInfoWithConsent>> = executeApiCall {
        defaultSource.getUserInfoWithConsent(productType = productType, apiVersion = apiVersion, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Validates whether the specified account holder is active in the MTN MOMO system.
     *
     * @param productType The type of product for which to validate the account holder.
     * @param apiVersion The version of the API to use.
     * @param accountHolder The account holder details (ID and type) to validate.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A [Flow] emitting a [NetworkResult] with the raw validation result as a [ResponseBody].
     */
    fun validateAccountHolderStatus(productType: String, apiVersion: String, accountHolder: AccountHolder, productSubscriptionKey: String, environment: String): Flow<NetworkResult<ResponseBody>> = executeApiCall {
        defaultSource.validateAccountHolderStatus(productType, apiVersion = apiVersion, accountHolder = accountHolder, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Retrieves the account balance, optionally filtered by currency.
     *
     * Currently only works reliably with [io.rekast.sdk.utils.ProductType.COLLECTION].
     * Use EUR as the currency value when testing on the sandbox environment.
     *
     * @param productType The type of product for which to get the account balance.
     * @param apiVersion The version of the API to use.
     * @param currency An optional ISO currency code; when provided, the balance is returned for that currency only.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A [Flow] emitting a [NetworkResult] containing the [AccountBalance].
     */
    fun getAccountBalance(productType: String, apiVersion: String, currency: String?, productSubscriptionKey: String, environment: String): Flow<NetworkResult<AccountBalance>> = executeApiCall {
        if (!currency.isNullOrBlank()) {
            defaultSource.getAccountBalanceInSpecificCurrency(
                productType = productType,
                apiVersion = apiVersion,
                currency = currency,
                productSubscriptionKey = productSubscriptionKey,
                environment = environment
            )
        } else {
            defaultSource.getAccountBalance(productType = productType, apiVersion = apiVersion, productSubscriptionKey = productSubscriptionKey, environment = environment)
        }
    }

    /**
     * Initiates a fund transfer to a specified account.
     *
     * @param productType The type of product for the transfer.
     * @param apiVersion The version of the API to use.
     * @param momoTransaction The transaction details including amount, currency, and party information.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A [Flow] emitting a [NetworkResult] with an empty [Unit] body on success.
     */
    fun transfer(productType: String, apiVersion: String, momoTransaction: MomoTransaction, uuid: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Unit>> = executeApiCall {
        defaultSource.transfer(productType = productType, apiVersion = apiVersion, momoTransaction = momoTransaction, uuid = uuid, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Retrieves the status of a previously initiated fund transfer.
     *
     * @param productType The type of product for the transfer.
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID used when calling [transfer].
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A [Flow] emitting a [NetworkResult] with the transfer status as a [ResponseBody].
     */
    fun getTransferStatus(productType: String, apiVersion: String, referenceId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<ResponseBody>> = executeApiCall {
        defaultSource.getTransferStatus(productType = productType, apiVersion = apiVersion, referenceId = referenceId, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Sends a delivery notification to the payer for an existing request-to-pay transaction.
     *
     * @param productType The type of product for the notification.
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID of the original request-to-pay transaction.
     * @param momoNotification The notification payload containing the message to deliver.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A [Flow] emitting a [NetworkResult] with the raw result as a [ResponseBody].
     */
    fun requestToPayDeliveryNotification(
        productType: String,
        apiVersion: String,
        referenceId: String,
        momoNotification: MomoNotification,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<ResponseBody>> = executeApiCall {
        defaultSource.requestToPayDeliveryNotification(
            productType = productType,
            apiVersion = apiVersion,
            referenceId = referenceId,
            momoNotification = momoNotification,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Initiates a request-to-pay directly via the Collection service.
     *
     * @param accessToken The bearer access token used to authenticate the request.
     * @param momoTransaction The transaction payload containing amount, currency, and party details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A [retrofit2.Response] with an empty body; HTTP 202 indicates the request was accepted.
     */
    suspend fun requestToPay(accessToken: String, momoTransaction: MomoTransaction, apiVersion: String, productSubscriptionKey: String, uuid: String): Response<Unit> =
        collection.requestToPay(momoTransaction, apiVersion, productSubscriptionKey, config.environment, uuid)

    /**
     * Retrieves the status of a previously initiated request-to-pay transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToPay].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param accessToken The bearer access token used to authenticate the request.
     * @return A [retrofit2.Response] whose body contains the transaction status as a [ResponseBody].
     */
    suspend fun requestToPayTransactionStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String, accessToken: String): Response<ResponseBody> =
        collection.requestToPayTransactionStatus(referenceId, apiVersion, productSubscriptionKey, config.environment)

    /**
     * Initiates a request-to-withdraw directly via the Collection service.
     *
     * @param accessToken The bearer access token used to authenticate the request.
     * @param momoTransaction The transaction payload containing amount, currency, and party details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A [retrofit2.Response] with an empty body; HTTP 202 indicates the request was accepted.
     */
    suspend fun requestToWithdraw(accessToken: String, momoTransaction: MomoTransaction, apiVersion: String, productSubscriptionKey: String, uuid: String): Response<Unit> =
        collection.requestToWithdraw(momoTransaction, apiVersion, productSubscriptionKey, config.environment, uuid)

    /**
     * Retrieves the status of a previously initiated request-to-withdraw transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToWithdraw].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param accessToken The bearer access token used to authenticate the request.
     * @return A [retrofit2.Response] whose body contains the withdrawal status as a [ResponseBody].
     */
    suspend fun requestToWithdrawTransactionStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String, accessToken: String): Response<ResponseBody> =
        collection.requestToWithdrawTransactionStatus(referenceId, apiVersion, productSubscriptionKey, config.environment)

    /**
     * Initiates a deposit directly via the Disbursements service.
     *
     * @param accessToken The bearer access token used to authenticate the request.
     * @param momoTransaction The transaction payload containing amount, currency, and payee details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Disbursements product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A [retrofit2.Response] with an empty body; HTTP 202 indicates the request was accepted.
     */
    suspend fun deposit(accessToken: String, momoTransaction: MomoTransaction, apiVersion: String, productSubscriptionKey: String, uuid: String): Response<Unit> =
        disbursementsService.deposit(momoTransaction, apiVersion, productSubscriptionKey, config.environment, uuid)

    /**
     * Retrieves the status of a previously initiated deposit transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [deposit].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Disbursements product.
     * @param accessToken The bearer access token used to authenticate the request.
     * @return A [retrofit2.Response] whose body contains the deposit status as a [ResponseBody].
     */
    suspend fun getDepositStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String, accessToken: String): Response<ResponseBody> =
        disbursementsService.getDepositStatus(referenceId, apiVersion, productSubscriptionKey, config.environment)

    /**
     * Initiates a refund directly via the Disbursements service.
     *
     * @param accessToken The bearer access token used to authenticate the request.
     * @param momoTransaction The transaction payload; set [MomoTransaction.referenceIdToRefund] to the original transaction ID.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Disbursements product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A [retrofit2.Response] with an empty body; HTTP 202 indicates the request was accepted.
     */
    suspend fun refund(accessToken: String, momoTransaction: MomoTransaction, apiVersion: String, productSubscriptionKey: String, uuid: String): Response<Unit> =
        disbursementsService.refund(momoTransaction, apiVersion, productSubscriptionKey, config.environment, uuid)

    /**
     * Retrieves the status of a previously initiated refund transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [refund].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Disbursements product.
     * @param accessToken The bearer access token used to authenticate the request.
     * @return A [retrofit2.Response] whose body contains the refund status as a [ResponseBody].
     */
    suspend fun getRefundStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String, accessToken: String): Response<ResponseBody> =
        disbursementsService.getRefundStatus(referenceId, apiVersion, productSubscriptionKey, config.environment)
}
