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

import io.rekast.sdk.model.BcAuthorizeRequest
import io.rekast.sdk.model.CashTransfer
import io.rekast.sdk.model.Invoice
import io.rekast.sdk.model.InvoiceCancellation
import io.rekast.sdk.model.Notifications
import io.rekast.sdk.model.Party
import io.rekast.sdk.model.Payment
import io.rekast.sdk.model.PreApproval
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.Transfer
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.CommonService
import io.rekast.sdk.network.service.products.RemittanceService
import javax.inject.Inject

/**
 * DefaultSource is responsible for handling API calls related to user management
 * and authentication in the MTN MOMO SDK.
 *
 * This class acts as a bridge between the repository and the authentication service,
 * providing methods to create API users, retrieve user details, create API keys,
 * and obtain access tokens.
 *
 * @property authenticationService The service for handling authentication-related API calls.
 * @property commonService The service for handling common API calls.
 * @property collectionService The service for Collection-product-specific API calls (request-to-pay, invoice, pre-approval, request-to-pay delivery notification).
 * @property remittanceService The service for Remittance-product-specific API calls (transfer, cash transfer V2).
 */
class DefaultSource @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val commonService: CommonService,
    private val collectionService: CollectionService,
    private val remittanceService: RemittanceService
) {

    /**
     * Creates a new API user.
     *
     * @param providerCallBackHost The callback host for the provider.
     * @param apiVersion The version of the API to use.
     * @param uuid A unique identifier for the request.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the created [io.rekast.sdk.model.authentication.ApiUser].
     */
    suspend fun createApiUser(providerCallBackHost: ProviderCallBackHost, apiVersion: String, uuid: String, productSubscriptionKey: String) = authenticationService.createApiUser(
        providerCallBackHost = providerCallBackHost,
        apiVersion = apiVersion,
        uuid = uuid,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves the details of an existing API user.
     *
     * @param apiVersion The version of the API to use.
     * @param userId The ID of the API user to retrieve.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the requested [io.rekast.sdk.model.authentication.ApiUser].
     */
    suspend fun getApiUser(apiVersion: String, userId: String, productSubscriptionKey: String) = authenticationService.getApiUser(
        apiVersion = apiVersion,
        apiUser = userId,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Creates a new API key for the specified API user.
     *
     * @param apiVersion The version of the API to use.
     * @param userId The ID of the API user for whom to create the key.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the generated [io.rekast.sdk.model.authentication.ApiKey].
     */
    suspend fun createApiKey(apiVersion: String, userId: String, productSubscriptionKey: String) = authenticationService.createApiKey(
        apiVersion = apiVersion,
        apiUser = userId,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Obtains an access token for the specified product type.
     *
     * @param productType The type of product for which to obtain the access token.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the obtained [io.rekast.sdk.model.authentication.AccessToken].
     */
    suspend fun getAccessToken(productType: String, productSubscriptionKey: String) = authenticationService.getAccessToken(
        productType = productType,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves an OAuth2 access token for the specified product type.
     *
     * This function interacts with the authentication service to obtain an access token
     * that can be used for subsequent API calls. The access token is essential for
     * authenticating requests to the MTN MOMO API.
     *
     * @param productType The type of product for which to obtain the OAuth2 access token.
     * @param productSubscriptionKey The subscription key for the product.
     * @param backChannelAuthorizationRequestId The `auth_req_id` from a prior bc-authorize call, exchanged for the OAuth2 token.
     * @return A `Response` containing the obtained [io.rekast.sdk.model.authentication.Oauth2AccessToken].
     */
    suspend fun getOauth2AccessToken(productType: String, productSubscriptionKey: String, backChannelAuthorizationRequestId: String) = authenticationService.getOauth2AccessToken(
        productType = productType,
        productSubscriptionKey = productSubscriptionKey,
        authReqId = backChannelAuthorizationRequestId
    )

    /**
     * Retrieves the basic user information for a specified MTN MOMO user.
     *
     * @param productType The type of product for which to retrieve the user information.
     * @param apiVersion The version of the API to use.
     * @param accountHolder The identifier for the account holder.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the [io.rekast.sdk.model.BasicUserInfo] of the specified user.
     */
    suspend fun getBasicUserInfo(productType: String, apiVersion: String, accountHolder: String, productSubscriptionKey: String) = commonService.getBasicUserInfo(
        productType = productType,
        apiVersion = apiVersion,
        accountHolder = accountHolder,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Validates the status of an account holder.
     *
     * @param productType The type of product for which to validate the account holder.
     * @param apiVersion The version of the API to use.
     * @param party The account holder details.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the parsed [io.rekast.sdk.model.AccountHolderStatus].
     */
    suspend fun validateAccountHolderStatus(productType: String, apiVersion: String, party: Party, productSubscriptionKey: String) = commonService.validateAccountHolderStatus(
        productType = productType,
        apiVersion = apiVersion,
        accountHolderId = party.partyId,
        accountHolderType = party.partyIdType.partyType,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves the account balance for a specified product type. This only works with the [io.rekast.sdk.utils.ProductTypes.COLLECTION]. It seems to break with the other API product type.
     *
     * @param productType The type of product for which to retrieve the account balance.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the [io.rekast.sdk.model.AccountBalance].
     */
    suspend fun getAccountBalance(productType: String, apiVersion: String, productSubscriptionKey: String) = commonService.getAccountBalance(
        productType = productType,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves the account balance in a specific currency. This only works with the [io.rekast.sdk.utils.ProductTypes.COLLECTION]. It seems to break with the other API product type.
     *
     * @param productType The type of product for which to retrieve the account balance.
     * @param apiVersion The version of the API to use.
     * @param currency The currency for which to retrieve the account balance.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the [io.rekast.sdk.model.AccountBalance].
     */
    suspend fun getAccountBalanceInSpecificCurrency(productType: String, apiVersion: String, currency: String, productSubscriptionKey: String) = commonService.getAccountBalanceInSpecificCurrency(
        productType = productType,
        apiVersion = apiVersion,
        currency = currency,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves user information with consent for a specified product type.
     *
     * @param productType The type of product for which to retrieve the user information.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the user information with consent.
     */
    suspend fun getUserInfoWithConsent(productType: String, apiVersion: String, productSubscriptionKey: String) = commonService.getUserInfoWithConsent(
        productType = productType,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Initiates a transfer for a specified product type.
     *
     * @param productType The type of product for which to initiate the transfer.
     * @param apiVersion The version of the API to use.
     * @param transfer The transfer details.
     * @param uuid A unique identifier for the request.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` indicating the result of the transfer.
     */
    suspend fun transfer(productType: String, apiVersion: String, transfer: Transfer, uuid: String, productSubscriptionKey: String) = remittanceService.transfer(
        productType = productType,
        apiVersion = apiVersion,
        transfer = transfer,
        uuid = uuid,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves the status of a transfer for a specified product type.
     *
     * @param productType The type of product for which to retrieve the transfer status.
     * @param apiVersion The version of the API to use.
     * @param referenceId The reference ID of the transfer.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the parsed [io.rekast.sdk.model.TransferStatus] transfer status.
     */
    suspend fun getTransferStatus(productType: String, apiVersion: String, referenceId: String, productSubscriptionKey: String) = remittanceService.getTransferStatus(
        productType = productType,
        apiVersion = apiVersion,
        referenceId = referenceId,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Sends a delivery notification for a request to pay.
     *
     * @param productType The type of product for which to send the notification.
     * @param apiVersion The version of the API to use.
     * @param referenceId The reference ID of the request to pay.
     * @param notifications The notification details.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` indicating the result of the notification request.
     */
    suspend fun requestToPayDeliveryNotification(productType: String, apiVersion: String, referenceId: String, notifications: Notifications, productSubscriptionKey: String) =
        collectionService.requestToPayDeliveryNotification(
            productType = productType,
            apiVersion = apiVersion,
            referenceId = referenceId,
            notifications = notifications,
            notificationMessage = notifications.notificationMessage,
            productSubscriptionKey = productSubscriptionKey
        )

    /**
     * Initiates a backchannel (CIBA) authorization request.
     *
     * @param productType The type of product initiating the authorization (e.g., collection).
     * @param apiVersion The version of the API (e.g., v1_0).
     * @param bcAuthorizeRequest The authorization request parameters.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A `Response` containing the [io.rekast.sdk.model.BcAuthorizeResponse] with the authorization request details.
     */
    suspend fun bcAuthorize(productType: String, apiVersion: String, bcAuthorizeRequest: BcAuthorizeRequest, productSubscriptionKey: String) = authenticationService.bcAuthorize(
        productType = productType,
        apiVersion = apiVersion,
        loginHint = bcAuthorizeRequest.loginHint,
        scope = bcAuthorizeRequest.scope,
        accessType = bcAuthorizeRequest.accessType,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Creates a Collection invoice.
     *
     * @param invoice The invoice payload containing amount, currency, and optional payer details.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id; poll [getInvoiceStatus] with the same ID.
     * @return A `Response` with an empty body; HTTP 202 indicates the invoice was accepted.
     */
    suspend fun createInvoice(invoice: Invoice, apiVersion: String, productSubscriptionKey: String, uuid: String) = collectionService.createInvoice(
        invoice = invoice,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey,
        uuid = uuid
    )

    /**
     * Creates a Collection payment (V2).
     *
     * @param payment The payment payload.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id; poll [getPaymentStatus] with the same ID.
     * @return A `Response` with an empty body; HTTP 202 indicates the payment was accepted.
     */
    suspend fun createPayment(payment: Payment, apiVersion: String, productSubscriptionKey: String, uuid: String) = collectionService.createPayment(
        payment = payment,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey,
        uuid = uuid
    )

    /**
     * Retrieves the status of a previously created Collection payment.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createPayment].
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @return A `Response` whose body is the parsed [io.rekast.sdk.model.PaymentStatus].
     */
    suspend fun getPaymentStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String) = collectionService.getPaymentStatus(
        referenceId = referenceId,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves the status of a previously created Collection invoice.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createInvoice].
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @return A `Response` whose body contains the invoice status details.
     */
    suspend fun getInvoiceStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String) = collectionService.getInvoiceStatus(
        referenceId = referenceId,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Cancels a pending Collection invoice.
     *
     * The invoice is identified by [externalId] (the value used when it was created).
     *
     * @param referenceId Per MTN: the UUID of the transaction used to get the result — uniquely identifies this invoice cancellation (URL path).
     * @param apiVersion The version of the API to use.
     * @param externalId The `externalId` sent when the invoice was created; echoed in the request body.
     * @param uuid The `X-Reference-Id` header. Per MTN: the UUID V4 resource ID of the transaction, used
     *   e.g. for validating the status of the request.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @return A `Response` with an empty body; HTTP 200 indicates successful cancellation.
     */
    suspend fun cancelInvoice(referenceId: String, apiVersion: String, externalId: String, uuid: String, productSubscriptionKey: String) = collectionService.cancelInvoice(
        referenceId = referenceId,
        apiVersion = apiVersion,
        invoiceCancellation = InvoiceCancellation(externalId = externalId),
        productSubscriptionKey = productSubscriptionKey,
        uuid = uuid
    )

    /**
     * Creates a Collection pre-approval, authorising the merchant to charge the payer's wallet
     * without per-transaction prompts until the pre-approval expires.
     *
     * @param preApproval The pre-approval payload containing the payer, currency, and validity.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id; poll [getPreApprovalStatus] with the same ID.
     * @return A `Response` with an empty body; HTTP 202 indicates the pre-approval was accepted.
     */
    suspend fun createPreApproval(preApproval: PreApproval, apiVersion: String, productSubscriptionKey: String, uuid: String) = collectionService.createPreApproval(
        preApproval = preApproval,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey,
        uuid = uuid
    )

    /**
     * Retrieves the status of a previously created Collection pre-approval.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createPreApproval].
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @return A `Response` whose body is the parsed [io.rekast.sdk.model.PreApprovalStatus].
     */
    suspend fun getPreApprovalStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String) = collectionService.getPreApprovalStatus(
        referenceId = referenceId,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Initiates a Remittance cash transfer using the V2 endpoint with optional KYC fields.
     *
     * @param cashTransfer The cash transfer payload including recipient and optional payer KYC details.
     * @param apiVersion The version of the API to use (e.g., v2_0).
     * @param productSubscriptionKey The subscription key for the Remittance product.
     * @param uuid A UUID V4 used as the X-Reference-Id; poll [getCashTransferStatus] with the same ID.
     * @return A `Response` with an empty body; HTTP 202 indicates the transfer was accepted.
     */
    suspend fun cashTransfer(cashTransfer: CashTransfer, apiVersion: String, productSubscriptionKey: String, uuid: String) = remittanceService.cashTransfer(
        cashTransfer = cashTransfer,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey,
        uuid = uuid
    )

    /**
     * Retrieves the status of a previously initiated Remittance cash transfer.
     *
     * @param referenceId The UUID V4 reference ID used when calling [cashTransfer].
     * @param apiVersion The version of the API to use (e.g., v2_0).
     * @param productSubscriptionKey The subscription key for the Remittance product.
     * @return A `Response` whose body is the parsed [io.rekast.sdk.model.CashTransferStatus] cash transfer status.
     */
    suspend fun getCashTransferStatus(referenceId: String, apiVersion: String, productSubscriptionKey: String) = remittanceService.getCashTransferStatus(
        referenceId = referenceId,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Cancels an active Collection pre-approval.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createPreApproval].
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @return A `Response` with an empty body; HTTP 200 indicates successful cancellation.
     */
    suspend fun cancelPreApproval(referenceId: String, apiVersion: String, productSubscriptionKey: String) = collectionService.cancelPreApproval(
        referenceId = referenceId,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves the list of approved pre-approvals for a given account holder.
     *
     * @param accountHolderIdType The type of the account holder identifier (e.g., MSISDN).
     * @param accountHolderId The account holder identifier of the payer whose pre-approvals to list.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the Collection product.
     * @return A `Response` whose body is the parsed [io.rekast.sdk.model.ApprovedPreApprovals].
     */
    suspend fun getApprovedPreApprovals(accountHolderIdType: String, accountHolderId: String, apiVersion: String, productSubscriptionKey: String) = collectionService.getApprovedPreApprovals(
        accountHolderIdType = accountHolderIdType,
        accountHolderId = accountHolderId,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey
    )
}
