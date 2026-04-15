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

import io.rekast.sdk.BuildConfig
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
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.DisbursementsService
import io.rekast.sdk.repository.data.DataResponse
import io.rekast.sdk.repository.data.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import org.apache.commons.lang3.StringUtils
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
 * @property basicAuthCredentialsT The credentials for basic authentication.
 * @property accessTokenCredentialsT The credentials for access token authentication.
 */
@Singleton
class DefaultRepository @Inject constructor(
    private val defaultSource: DefaultSource,
    private val disbursementsService: DisbursementsService,
    private val collection: CollectionService,
    private val basicAuthCredentialsT: BasicAuthCredentials,
    private val accessTokenCredentialsT: AccessTokenCredentials
) : DataResponse() {

    /**
     * Sets up basic authentication credentials.
     *
     * @param basicAuthCredentials The basic authentication credentials to set.
     */
    fun setUpBasicAuth(basicAuthCredentials: BasicAuthCredentials) {
        basicAuthCredentialsT.apiUserId = basicAuthCredentials.apiUserId
        basicAuthCredentialsT.apiKey = basicAuthCredentials.apiKey
    }

    /**
     * Sets up access token authentication credentials.
     *
     * @param accessTokenCredentials The access token credentials to set.
     */
    fun setUpAccessTokenAuth(accessTokenCredentials: AccessTokenCredentials) {
        accessTokenCredentialsT.accessToken = accessTokenCredentials.accessToken
    }

    /**
     * Retrieves the current basic authentication credentials.
     *
     * @return The current [BasicAuthCredentials].
     */
    fun getBasicAuth(): BasicAuthCredentials {
        return basicAuthCredentialsT
    }

    /**
     * Retrieves the current access token authentication credentials.
     *
     * @return The current [AccessTokenCredentials].
     */
    fun getAccessTokenAuth(): AccessTokenCredentials {
        return accessTokenCredentialsT
    }

    private fun <T> executeApiCall(apiCall: suspend () -> Response<T>): Flow<NetworkResult<T>> {
        return flow<NetworkResult<T>> {
            emit(safeApiCall { apiCall() })
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Creates a new API user.
     *
     * @param providerCallBackHost The callback host for the provider.
     * @param apiVersion The version of the API to use.
     * @param uuid A unique identifier for the request.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Flow] emitting a [NetworkResult] containing the created [ApiUser].
     */
    fun createApiUser(
        providerCallBackHost: ProviderCallBackHost,
        apiVersion: String,
        uuid: String,
        productSubscriptionKey: String
    ): Flow<NetworkResult<ApiUser>> {
        return executeApiCall { defaultSource.createApiUser(providerCallBackHost = providerCallBackHost, apiVersion = apiVersion, uuid = uuid, productSubscriptionKey = productSubscriptionKey) }
    }

    /**
     * Checks whether the supplied API user exists.
     *
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Flow] emitting a [NetworkResult] containing the [ApiUser] if found.
     */
    fun checkApiUser(
        apiVersion: String,
        productSubscriptionKey: String
    ): Flow<NetworkResult<ApiUser>> {
        return executeApiCall { defaultSource.getApiUser(apiVersion, userId = BuildConfig.MOMO_API_USER_ID, productSubscriptionKey = productSubscriptionKey) }
    }

    /**
     * Gets the API Key based on the ApiUser Id and OCP Subscription Id.
     *
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Flow] emitting a [NetworkResult] containing the [ApiKey].
     */
    fun createApiKey(
        apiVersion: String,
        productSubscriptionKey: String
    ): Flow<NetworkResult<ApiKey>> {
        return executeApiCall { defaultSource.createApiKey(apiVersion = apiVersion, userId = BuildConfig.MOMO_API_USER_ID, productSubscriptionKey = productSubscriptionKey) }
    }

    /**
     * Gets the Access Token based on the ApiUser ID, OCP Subscription Id, and the API Key.
     *
     * @param productSubscriptionKey The subscription key for the product.
     * @param productType The type of product for which to obtain the access token.
     * @return A [Response] containing the obtained [AccessToken].
     */
    fun getAccessToken(
        productSubscriptionKey: String,
        productType: String
    ): Flow<NetworkResult<AccessToken>> {
        return executeApiCall { defaultSource.getAccessToken(productType = productType, productSubscriptionKey = productSubscriptionKey) }
    }

    /**
     * Gets the Access Token based on the ApiUser ID, OCP Subscription Id, and the API Key.
     *
     * @param productSubscriptionKey The subscription key for the product.
     * @param productType The type of product for which to obtain the access token.
     * @return A [Response] containing the obtained [AccessToken].
     */
    fun getOauthAccessToken(
        productType: String,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<Oauth2AccessToken>> {
        return executeApiCall { defaultSource.getOauth2AccessToken(productType = productType, productSubscriptionKey = productSubscriptionKey, environment = environment) }
    }

    /**
     * Retrieves the basic user information for a specified MTN MOMO user.
     *
     * @param accountHolder The identifier for the account holder.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @param apiVersion The version of the API to use.
     * @param productType The type of product for which to retrieve the user information.
     * @return A [Response] containing the [BasicUserInfo] of the specified user.
     */
    fun getBasicUserInfo(
        productType: String,
        apiVersion: String,
        accountHolder: String,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<BasicUserInfo>> {
        return executeApiCall { defaultSource.getBasicUserInfo(productType = productType, apiVersion = apiVersion, accountHolder = accountHolder, productSubscriptionKey = productSubscriptionKey, environment = environment) }
    }

    /**
     * Retrieves the user information for a specified MTN MOMO user with consent.
     *
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @param apiVersion The version of the API to use.
     * @param productType The type of product for which to retrieve the user information.
     * @return A [Response] containing the [UserInfoWithConsent] of the specified user.
     */
    fun getUserInfoWithConsent(
        productType: String,
        apiVersion: String,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<UserInfoWithConsent>> {
        return executeApiCall { defaultSource.getUserInfoWithConsent(productType = productType, apiVersion = apiVersion, productSubscriptionKey = productSubscriptionKey, environment = environment) }
    }

    /**
     * Validates the status of an account holder.
     *
     * @param accountHolder The account holder details to validate.
     * @param apiVersion The version of the API to use.
     * @param productType The type of product for the validation.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @return A [Response] indicating the result of the account holder status validation.
     */
    fun validateAccountHolderStatus(
        productType: String,
        apiVersion: String,
        accountHolder: AccountHolder,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<ResponseBody>> {
        return executeApiCall { defaultSource.validateAccountHolderStatus(productType, apiVersion = apiVersion, accountHolder = accountHolder, productSubscriptionKey = productSubscriptionKey, environment = environment) }
    }

    /**
     * Gets the account balance of the entity/user initiating the transaction. This only works with the [ProductType.COLLECTION]. It seems to break with the other API products.
     * User EUR as the currency on sandbox
     *
     * @param currency The currency for which to get the account balance.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @param apiVersion The version of the API to use.
     * @param productType The type of product for which to get the account balance.
     * @return A [Response] containing the [AccountBalance].
     */
    fun getAccountBalance(
        productType: String,
        apiVersion: String,
        currency: String?,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<AccountBalance>> {
        return executeApiCall {
            if (StringUtils.isNotBlank(currency)) {
                defaultSource.getAccountBalanceInSpecificCurrency(
                    productType = productType,
                    apiVersion = apiVersion,
                    currency = currency.toString(),
                    productSubscriptionKey = productSubscriptionKey,
                    environment = environment
                )
            } else {
                defaultSource.getAccountBalance(productType = productType, apiVersion = apiVersion, productSubscriptionKey = productSubscriptionKey, environment = environment)
            }
        }
    }

    /**
     * Sends a request to transfer funds to a specified account.
     *
     * @param accessToken The access token for authentication.
     * @param momoTransaction The transaction details for the transfer.
     * @param apiVersion The version of the API to use.
     * @param productType The type of product for the transfer.
     * @param productSubscriptionKey The subscription key for the product.
     * @param uuid A unique identifier for the request.
     * @return A [Response] indicating the result of the transfer request.
     */
    fun transfer(
        productType: String,
        apiVersion: String,
        momoTransaction: MomoTransaction,
        uuid: String,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<Unit>> {
        return executeApiCall {
            defaultSource.transfer(productType = productType, apiVersion = apiVersion, momoTransaction = momoTransaction, uuid = uuid, productSubscriptionKey = productSubscriptionKey, environment = environment)
        }
    }

    /**
     * Retrieves the status of a transfer based on the provided transfer ID.
     *
     * @param referenceId The reference ID of the transfer.
     * @param apiVersion The version of the API to use.
     * @param productType The type of product for the transfer.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @return A [Response] containing the status of the transfer.
     */
    fun getTransferStatus(
        productType: String,
        apiVersion: String,
        referenceId: String,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<ResponseBody>> {
        return executeApiCall {
            defaultSource.getTransferStatus(productType = productType, apiVersion = apiVersion, referenceId = referenceId, productSubscriptionKey = productSubscriptionKey, environment = environment)
        }
    }

    /**
     * Sends a request to pay a user, identified by the provided reference ID.
     *
     * @param momoNotification The notification details for the payment.
     * @param referenceId The reference ID of the user to pay.
     * @param apiVersion The version of the API to use.
     * @param productType The type of product for the payment.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @return A [Response] indicating the result of the payment request.
     */
    fun requestToPayDeliveryNotification(
        productType: String,
        apiVersion: String,
        referenceId: String,
        momoNotification: MomoNotification,
        productSubscriptionKey: String,
        environment: String
    ): Flow<NetworkResult<ResponseBody>> {
        return executeApiCall {
            defaultSource.requestToPayDeliveryNotification(
                productType = productType,
                apiVersion = apiVersion,
                referenceId = referenceId,
                momoNotification = momoNotification,
                productSubscriptionKey = productSubscriptionKey,
                environment = environment
            )
        }
    }

    /**
     * Requests a payment to be processed.
     *
     * @param accessToken The access token for authentication.
     * @param momoTransaction The transaction details.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param uuid A unique identifier for the request.
     * @return A [Response] indicating the result of the request.
     */
    suspend fun requestToPay(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String
    ): Response<Unit> {
        return collection.requestToPay(momoTransaction, apiVersion, productSubscriptionKey, BuildConfig.MOMO_ENVIRONMENT, uuid)
    }

    /**
     * Requests the status of a payment transaction.
     *
     * @param referenceId The reference ID of the transaction.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @return A [Response] containing the status of the transaction.
     */
    suspend fun requestToPayTransactionStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Response<ResponseBody> {
        return collection.requestToPayTransactionStatus(referenceId, apiVersion, productSubscriptionKey, BuildConfig.MOMO_ENVIRONMENT)
    }

    /**
     * Requests a withdrawal to be processed.
     *
     * @param accessToken The access token for authentication.
     * @param momoTransaction The transaction details.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param uuid A unique identifier for the request.
     * @return A [Response] indicating the result of the request.
     */
    suspend fun requestToWithdraw(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String
    ): Response<Unit> {
        return collection.requestToWithdraw(momoTransaction, apiVersion, productSubscriptionKey, BuildConfig.MOMO_ENVIRONMENT, uuid)
    }

    /**
     * Requests the status of a withdrawal transaction.
     *
     * @param referenceId The reference ID of the transaction.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @return A [Response] containing the status of the transaction.
     */
    suspend fun requestToWithdrawTransactionStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Response<ResponseBody> {
        return collection.requestToWithdrawTransactionStatus(referenceId, apiVersion, productSubscriptionKey, BuildConfig.MOMO_ENVIRONMENT)
    }

    /**
     * Requests a deposit to be processed.
     *
     * @param accessToken The access token for authentication.
     * @param momoTransaction The transaction details.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param uuid A unique identifier for the request.
     * @return A [Response] indicating the result of the request.
     */
    suspend fun deposit(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String
    ): Response<Unit> {
        return disbursementsService.deposit(momoTransaction, apiVersion, productSubscriptionKey, BuildConfig.MOMO_ENVIRONMENT, uuid)
    }

    /**
     * Gets the status of a deposit transaction.
     *
     * @param referenceId The reference ID of the transaction.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @return A [Response] containing the status of the deposit transaction.
     */
    suspend fun getDepositStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Response<ResponseBody> {
        return disbursementsService.getDepositStatus(referenceId, apiVersion, productSubscriptionKey, BuildConfig.MOMO_ENVIRONMENT)
    }

    /**
     * Requests a refund to be processed.
     *
     * @param accessToken The access token for authentication.
     * @param momoTransaction The transaction details.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param uuid A unique identifier for the request.
     * @return A [Response] indicating the result of the request.
     */
    suspend fun refund(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String
    ): Response<Unit> {
        return disbursementsService.refund(momoTransaction, apiVersion, productSubscriptionKey, BuildConfig.MOMO_ENVIRONMENT, uuid)
    }

    /**
     * Gets the status of a refund transaction.
     *
     * @param referenceId The reference ID of the transaction.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param accessToken The access token for authentication.
     * @return A [Response] containing the status of the refund transaction.
     */
    suspend fun getRefundStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Response<ResponseBody> {
        return disbursementsService.getRefundStatus(referenceId, apiVersion, productSubscriptionKey, BuildConfig.MOMO_ENVIRONMENT)
    }
}
