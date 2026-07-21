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
package io.rekast.sdk.repository

import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.model.ApprovedPreApprovals
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.BcAuthorizeRequest
import io.rekast.sdk.model.BcAuthorizeResponse
import io.rekast.sdk.model.CashTransfer
import io.rekast.sdk.model.CashTransferStatus
import io.rekast.sdk.model.Deposit
import io.rekast.sdk.model.DepositStatus
import io.rekast.sdk.model.Invoice
import io.rekast.sdk.model.InvoiceStatus
import io.rekast.sdk.model.Notifications
import io.rekast.sdk.model.Party
import io.rekast.sdk.model.Payment
import io.rekast.sdk.model.PaymentStatus
import io.rekast.sdk.model.PreApproval
import io.rekast.sdk.model.PreApprovalStatus
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.Refund
import io.rekast.sdk.model.RefundStatus
import io.rekast.sdk.model.RequestToPay
import io.rekast.sdk.model.RequestToPayStatus
import io.rekast.sdk.model.RequestToWithdraw
import io.rekast.sdk.model.RequestToWithdrawStatus
import io.rekast.sdk.model.Transfer
import io.rekast.sdk.model.TransferStatus
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
 * @property config The API configuration supplying the base URL, API user ID, environment, and API version.
 */
@Singleton
class DefaultRepository @Inject constructor(private val defaultSource: DefaultSource, private val disbursementsService: DisbursementsService, private val collection: CollectionService, private val config: ApiConfig) :
    DataResponse() {

    /**
     * Wraps a Retrofit suspend call in a cold `Flow` that always emits two items:
     * 1. [NetworkResult.Loading] — emitted immediately so that collectors can show a loading indicator.
     * 2. [NetworkResult.Success] or [NetworkResult.Error] — the terminal result from [safeApiCall].
     *
     * The flow runs entirely on `Dispatchers.IO`; collectors need not specify their own dispatcher.
     *
     * @param apiCall The suspend lambda that performs the Retrofit call and returns a `Response`.
     * @return A cold `Flow` that emits exactly two [NetworkResult] values then completes.
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
     * @return A `Flow` emitting a [NetworkResult] containing the created [ApiUser].
     */
    fun createApiUser(providerCallBackHost: ProviderCallBackHost, apiVersion: String, uuid: String, productSubscriptionKey: String): Flow<NetworkResult<ApiUser>> =
        executeApiCall { defaultSource.createApiUser(providerCallBackHost = providerCallBackHost, apiVersion = apiVersion, uuid = uuid, productSubscriptionKey = productSubscriptionKey) }

    /**
     * Checks whether the supplied API user exists.
     *
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Flow` emitting a [NetworkResult] containing the [ApiUser] if found.
     */
    fun checkApiUser(apiVersion: String, productSubscriptionKey: String): Flow<NetworkResult<ApiUser>> =
        executeApiCall { defaultSource.getApiUser(apiVersion, userId = config.apiUserId, productSubscriptionKey = productSubscriptionKey) }

    /**
     * Gets the API Key based on the ApiUser Id and OCP Subscription Id.
     *
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Flow` emitting a [NetworkResult] containing the [ApiKey].
     */
    fun createApiKey(apiVersion: String, productSubscriptionKey: String): Flow<NetworkResult<ApiKey>> =
        executeApiCall { defaultSource.createApiKey(apiVersion = apiVersion, userId = config.apiUserId, productSubscriptionKey = productSubscriptionKey) }

    /**
     * Gets the Access Token based on the ApiUser ID, OCP Subscription Id, and the API Key.
     *
     * @param productSubscriptionKey The subscription key for the product.
     * @param productType The type of product for which to obtain the access token.
     * @return A `Flow` emitting a [NetworkResult] containing the obtained [AccessToken].
     */
    fun getAccessToken(productSubscriptionKey: String, productType: String): Flow<NetworkResult<AccessToken>> =
        executeApiCall { defaultSource.getAccessToken(productType = productType, productSubscriptionKey = productSubscriptionKey) }

    /**
     * Obtains an OAuth2 access token for use with consent-based API endpoints.
     *
     * Always uses the CIBA grant (`grant_type=urn:openid:params:grant-type:ciba`), which requires
     * a valid `auth_req_id` from a prior [bcAuthorize] call. Passing a blank
     * [backChannelAuthorizationRequestId] is a programming error: the method emits
     * [NetworkResult.Error] immediately rather than forwarding an invalid request to the server.
     *
     * @param productType The type of product for which to obtain the OAuth2 access token.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @param backChannelAuthorizationRequestId The `auth_req_id` returned by a prior [bcAuthorize]
     *   call. Must not be blank.
     * @return A `Flow` emitting [NetworkResult.Error] immediately if [backChannelAuthorizationRequestId]
     *   is blank, otherwise emitting [NetworkResult.Loading] then a terminal [NetworkResult.Success]
     *   or [NetworkResult.Error] from the network call.
     */
    fun getOauthAccessToken(productType: String, productSubscriptionKey: String, environment: String, backChannelAuthorizationRequestId: String): Flow<NetworkResult<Oauth2AccessToken>> {
        if (backChannelAuthorizationRequestId.isBlank()) {
            return flow { emit(NetworkResult.Error("backChannelAuthorizationRequestId must not be blank — call bcAuthorize() first")) }
        }
        return executeApiCall {
            defaultSource.getOauth2AccessToken(productType = productType, productSubscriptionKey = productSubscriptionKey, environment = environment, backChannelAuthorizationRequestId = backChannelAuthorizationRequestId)
        }
    }

    /**
     * Retrieves the basic user information for a specified MTN MOMO user.
     *
     * @param productType The type of product for which to retrieve the user information.
     * @param apiVersion The version of the API to use.
     * @param accountHolder The MSISDN or other identifier for the account holder.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] containing the [BasicUserInfo] of the specified user.
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
     * @return A `Flow` emitting a [NetworkResult] containing the [UserInfoWithConsent] of the user.
     */
    fun getUserInfoWithConsent(productType: String, apiVersion: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<UserInfoWithConsent>> = executeApiCall {
        defaultSource.getUserInfoWithConsent(productType = productType, apiVersion = apiVersion, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Validates whether the specified account holder is active in the MTN MOMO system.
     *
     * @param productType The type of product for which to validate the account holder.
     * @param apiVersion The version of the API to use.
     * @param party The account holder details (ID and type) to validate.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [AccountHolderStatus] (`{"result": <bool>}`).
     */
    fun validateAccountHolderStatus(productType: String, apiVersion: String, party: Party, productSubscriptionKey: String, environment: String): Flow<NetworkResult<AccountHolderStatus>> = executeApiCall {
        defaultSource.validateAccountHolderStatus(productType, apiVersion = apiVersion, party = party, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Retrieves the account balance, optionally filtered by currency.
     *
     * Currently only works reliably with [io.rekast.sdk.utils.ProductTypes.COLLECTION].
     * Use EUR as the currency value when testing on the sandbox environment.
     *
     * @param productType The type of product for which to get the account balance.
     * @param apiVersion The version of the API to use.
     * @param currency An optional ISO currency code; when provided, the balance is returned for that currency only.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] containing the [AccountBalance].
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
     * @param transfer The transfer details including amount, currency, and party information.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success.
     */
    fun transfer(productType: String, apiVersion: String, transfer: Transfer, uuid: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Unit>> = executeApiCall {
        defaultSource.transfer(productType = productType, apiVersion = apiVersion, transfer = transfer, uuid = uuid, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Retrieves the status of a previously initiated fund transfer.
     *
     * @param productType The type of product for the transfer.
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID used when calling [transfer].
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [TransferStatus].
     */
    fun getTransferStatus(productType: String, apiVersion: String, referenceId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<TransferStatus>> = executeApiCall {
        defaultSource.getTransferStatus(productType = productType, apiVersion = apiVersion, referenceId = referenceId, productSubscriptionKey = productSubscriptionKey, environment = environment)
    }

    /**
     * Sends a delivery notification to the payer for an existing request-to-pay transaction.
     *
     * @param productType The type of product for the notification.
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID of the original request-to-pay transaction.
     * @param notifications The notification payload containing the message to deliver.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with the raw result as a `ResponseBody`.
     */
    fun requestToPayDeliveryNotification(productType: String, apiVersion: String, referenceId: String, notifications: Notifications, productSubscriptionKey: String, environment: String): Flow<NetworkResult<ResponseBody>> =
        executeApiCall {
            defaultSource.requestToPayDeliveryNotification(
                productType = productType,
                apiVersion = apiVersion,
                referenceId = referenceId,
                notifications = notifications,
                productSubscriptionKey = productSubscriptionKey,
                environment = environment
            )
        }

    /**
     * Initiates a request-to-pay via the Collection service, prompting the payer to approve a debit.
     *
     * @param requestToPay The request-to-pay payload containing amount, currency, and party details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success (HTTP 202).
     */
    fun requestToPay(requestToPay: RequestToPay, apiVersion: String, productSubscriptionKey: String, uuid: String): Flow<NetworkResult<Unit>> = executeApiCall {
        collection.requestToPay(requestToPay, apiVersion, productSubscriptionKey, config.environment, uuid)
    }

    /**
     * Retrieves the status of a previously initiated request-to-pay transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToPay].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [RequestToPayStatus].
     */
    fun requestToPayTransactionStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String): Flow<NetworkResult<RequestToPayStatus>> = executeApiCall {
        collection.requestToPayTransactionStatus(referenceId, apiVersion, productSubscriptionKey, config.environment)
    }

    /**
     * Initiates a request-to-withdraw via the Collection service, prompting the payer to approve a debit.
     *
     * @param requestToWithdraw The request-to-withdraw payload containing amount, currency, and party details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success (HTTP 202).
     */
    fun requestToWithdraw(requestToWithdraw: RequestToWithdraw, apiVersion: String, productSubscriptionKey: String, uuid: String): Flow<NetworkResult<Unit>> = executeApiCall {
        collection.requestToWithdraw(requestToWithdraw, apiVersion, productSubscriptionKey, config.environment, uuid)
    }

    /**
     * Retrieves the status of a previously initiated request-to-withdraw transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToWithdraw].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [RequestToWithdrawStatus].
     */
    fun requestToWithdrawTransactionStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String): Flow<NetworkResult<RequestToWithdrawStatus>> = executeApiCall {
        collection.requestToWithdrawTransactionStatus(referenceId, apiVersion, productSubscriptionKey, config.environment)
    }

    /**
     * Initiates a deposit via the Disbursements service, sending funds to the payee.
     *
     * @param deposit The deposit payload containing amount, currency, and payee details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Disbursements product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success (HTTP 202).
     */
    fun deposit(deposit: Deposit, apiVersion: String, productSubscriptionKey: String, uuid: String): Flow<NetworkResult<Unit>> = executeApiCall {
        disbursementsService.deposit(deposit, apiVersion, productSubscriptionKey, config.environment, uuid)
    }

    /**
     * Retrieves the status of a previously initiated deposit transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [deposit].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Disbursements product.
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [DepositStatus].
     */
    fun getDepositStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String): Flow<NetworkResult<DepositStatus>> = executeApiCall {
        disbursementsService.getDepositStatus(referenceId, apiVersion, productSubscriptionKey, config.environment)
    }

    /**
     * Initiates a refund via the Disbursements service, reversing a previous transaction.
     *
     * @param refund The refund payload; set [Refund.referenceIdToRefund] to the original transaction ID.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Disbursements product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success (HTTP 202).
     */
    fun refund(refund: Refund, apiVersion: String, productSubscriptionKey: String, uuid: String): Flow<NetworkResult<Unit>> = executeApiCall {
        disbursementsService.refund(refund, apiVersion, productSubscriptionKey, config.environment, uuid)
    }

    /**
     * Retrieves the status of a previously initiated refund transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [refund].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Disbursements product.
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [RefundStatus].
     */
    fun getRefundStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String): Flow<NetworkResult<RefundStatus>> = executeApiCall {
        disbursementsService.getRefundStatus(referenceId, apiVersion, productSubscriptionKey, config.environment)
    }

    /**
     * Initiates a backchannel (CIBA) authorization request.
     *
     * @param productType The type of product initiating the authorization (e.g., collection).
     * @param apiVersion The version of the API (e.g., v1_0).
     * @param bcAuthorizeRequest The authorization request parameters.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] containing the [BcAuthorizeResponse] with the authorization request details.
     */
    fun bcAuthorize(productType: String, apiVersion: String, bcAuthorizeRequest: BcAuthorizeRequest, productSubscriptionKey: String, environment: String): Flow<NetworkResult<BcAuthorizeResponse>> = executeApiCall {
        defaultSource.bcAuthorize(
            productType = productType,
            apiVersion = apiVersion,
            bcAuthorizeRequest = bcAuthorizeRequest,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Creates a Collection invoice, prompting the intended payer to approve payment from their wallet.
     *
     * Poll [getInvoiceStatus] with the same [uuid] as the reference ID to check whether the invoice
     * has been paid. The invoice expires after the duration specified in [Invoice.validityDuration].
     *
     * @param apiVersion The version of the API to use.
     * @param invoice The invoice payload containing amount, currency, and optional payer details.
     * @param uuid A UUID V4 used as the X-Reference-Id; reuse this same ID when calling [getInvoiceStatus].
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success (HTTP 202).
     */
    fun createInvoice(apiVersion: String, invoice: Invoice, uuid: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Unit>> = executeApiCall {
        defaultSource.createInvoice(
            invoice = invoice,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment,
            uuid = uuid
        )
    }

    /**
     * Creates a Collection payment (V2). Poll [getPaymentStatus] with the same [uuid] as the
     * reference ID to check the outcome.
     *
     * @param apiVersion The API version to target; use `v2_0` for this endpoint.
     * @param payment The payment payload (amount/currency, references, notes, and options).
     * @param uuid A UUID V4 used as the X-Reference-Id; reuse this same ID when calling [getPaymentStatus].
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success (HTTP 202).
     */
    fun createPayment(apiVersion: String, payment: Payment, uuid: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Unit>> = executeApiCall {
        defaultSource.createPayment(
            payment = payment,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment,
            uuid = uuid
        )
    }

    /**
     * Retrieves the current status of a previously created Collection payment.
     *
     * @param apiVersion The API version to target; use `v2_0` for this endpoint.
     * @param referenceId The UUID V4 reference ID used when calling [createPayment].
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [PaymentStatus].
     */
    fun getPaymentStatus(apiVersion: String, referenceId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<PaymentStatus>> = executeApiCall {
        defaultSource.getPaymentStatus(
            referenceId = referenceId,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Retrieves the current status of a previously created Collection invoice.
     *
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID used when calling [createInvoice].
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] whose body contains the invoice status as a `ResponseBody`.
     */
    fun getInvoiceStatus(apiVersion: String, referenceId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<InvoiceStatus>> = executeApiCall {
        defaultSource.getInvoiceStatus(
            referenceId = referenceId,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Cancels a pending Collection invoice before it is paid or expires.
     *
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID used when calling [createInvoice].
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success.
     */
    fun cancelInvoice(apiVersion: String, referenceId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Unit>> = executeApiCall {
        defaultSource.cancelInvoice(
            referenceId = referenceId,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Creates a Collection pre-approval, authorising the merchant to debit the payer's wallet
     * without requiring per-transaction consent until the pre-approval expires.
     *
     * @param apiVersion The version of the API to use.
     * @param preApproval The pre-approval payload containing the payer, currency, and validity duration.
     * @param uuid A UUID V4 used as the X-Reference-Id; reuse this same ID when calling [getPreApprovalStatus].
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success (HTTP 202).
     */
    fun createPreApproval(apiVersion: String, preApproval: PreApproval, uuid: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Unit>> = executeApiCall {
        defaultSource.createPreApproval(
            preApproval = preApproval,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment,
            uuid = uuid
        )
    }

    /**
     * Retrieves the current status of a previously created Collection pre-approval.
     *
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID used when calling [createPreApproval].
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [PreApprovalStatus].
     */
    fun getPreApprovalStatus(apiVersion: String, referenceId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<PreApprovalStatus>> = executeApiCall {
        defaultSource.getPreApprovalStatus(
            referenceId = referenceId,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Initiates a Remittance cash transfer using the V2 endpoint with optional KYC fields about
     * the sending party. This replaces the legacy V1 transfer for cross-border remittance use cases
     * where the sender is not a registered MTN mobile money subscriber.
     *
     * Poll [getCashTransferStatus] with the same [uuid] as the reference ID to check the outcome.
     *
     * @param apiVersion The API version to target; use `v2_0` for this endpoint.
     * @param cashTransfer The cash transfer payload including recipient, amounts, and optional KYC fields.
     * @param uuid A UUID V4 used as the X-Reference-Id; reuse this same ID when calling [getCashTransferStatus].
     * @param productSubscriptionKey The subscription key for the Remittance product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success (HTTP 202).
     */
    fun cashTransfer(apiVersion: String, cashTransfer: CashTransfer, uuid: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Unit>> = executeApiCall {
        defaultSource.cashTransfer(
            cashTransfer = cashTransfer,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment,
            uuid = uuid
        )
    }

    /**
     * Retrieves the status of a previously initiated Remittance cash transfer.
     *
     * @param apiVersion The API version to target; use `v2_0` for this endpoint.
     * @param referenceId The UUID V4 reference ID used when calling [cashTransfer].
     * @param productSubscriptionKey The subscription key for the Remittance product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [CashTransferStatus].
     */
    fun getCashTransferStatus(apiVersion: String, referenceId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<CashTransferStatus>> = executeApiCall {
        defaultSource.getCashTransferStatus(
            referenceId = referenceId,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Cancels an active Collection pre-approval, immediately revoking the merchant's ability
     * to debit the payer's wallet without per-transaction consent.
     *
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID used when calling [createPreApproval].
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with an empty [Unit] body on success.
     */
    fun cancelPreApproval(apiVersion: String, referenceId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<Unit>> = executeApiCall {
        defaultSource.cancelPreApproval(
            referenceId = referenceId,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Retrieves the list of approved pre-approvals for a given account holder.
     *
     * @param apiVersion The version of the API to use.
     * @param accountHolderIdType The type of the account holder identifier (e.g., MSISDN).
     * @param accountHolderId The account holder identifier of the payer whose pre-approvals to list.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] whose body is the parsed [ApprovedPreApprovals]
     *   (a `preApprovalDetails` list of [io.rekast.sdk.model.PreApprovalDetails]).
     */
    fun getApprovedPreApprovals(apiVersion: String, accountHolderIdType: String, accountHolderId: String, productSubscriptionKey: String, environment: String): Flow<NetworkResult<ApprovedPreApprovals>> = executeApiCall {
        defaultSource.getApprovedPreApprovals(
            accountHolderIdType = accountHolderIdType,
            accountHolderId = accountHolderId,
            apiVersion = apiVersion,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }

    /**
     * Sends a delivery notification to the payer for an existing request-to-withdraw transaction.
     *
     * @param apiVersion The version of the API to use.
     * @param referenceId The UUID V4 reference ID of the original request-to-withdraw transaction.
     * @param notifications The notification payload containing the message to deliver.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Flow` emitting a [NetworkResult] with the raw result as a `ResponseBody`.
     */
    fun requestToWithdrawDeliveryNotification(apiVersion: String, referenceId: String, notifications: Notifications, productSubscriptionKey: String, environment: String): Flow<NetworkResult<ResponseBody>> = executeApiCall {
        defaultSource.requestToWithdrawDeliveryNotification(
            apiVersion = apiVersion,
            referenceId = referenceId,
            notifications = notifications,
            productSubscriptionKey = productSubscriptionKey,
            environment = environment
        )
    }
}
