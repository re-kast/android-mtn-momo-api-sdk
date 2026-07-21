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
package io.rekast.sdk.network.service.products

import io.rekast.sdk.model.ApprovedPreApprovals
import io.rekast.sdk.model.Invoice
import io.rekast.sdk.model.InvoiceCancellation
import io.rekast.sdk.model.InvoiceStatus
import io.rekast.sdk.model.Notifications
import io.rekast.sdk.model.Payment
import io.rekast.sdk.model.PaymentStatus
import io.rekast.sdk.model.PreApproval
import io.rekast.sdk.model.PreApprovalStatus
import io.rekast.sdk.model.RequestToPay
import io.rekast.sdk.model.RequestToPayStatus
import io.rekast.sdk.model.RequestToWithdraw
import io.rekast.sdk.model.RequestToWithdrawStatus
import io.rekast.sdk.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit service interface for the MTN MOMO Collection product API.
 *
 * Extends [CommonService] with collection-specific operations: request-to-pay and request-to-withdraw.
 */
sealed interface CollectionService : CommonService {
    /**
     * Initiates a request-to-pay, prompting the specified payer to approve a payment.
     *
     * @param requestToPay The request-to-pay payload containing amount, currency, and party details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Response` with an empty body; HTTP 202 indicates the request was accepted.
     */
    @POST(Constants.EndPoints.REQUEST_TO_PAY)
    suspend fun requestToPay(
        @Body requestToPay: RequestToPay,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Makes a request to send a request-to-pay delivery notification.
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param referenceId The request-to-pay reference ID (UUID V4).
     * @param notifications The notification message.
     * @param notificationMessage The message to be sent to the user.
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @return A `Response` whose body contains the result of the notification request as a `ResponseBody`.
     */
    @POST(Constants.EndPoints.REQUEST_TO_PAY_DELIVERY_NOTIFICATION)
    suspend fun requestToPayDeliveryNotification(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Body notifications: Notifications,
        @Header(Constants.Headers.NOTIFICATION_MESSAGE) notificationMessage: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<ResponseBody>

    /**
     * Initiates a request-to-withdraw, prompting the specified payer to approve a withdrawal.
     *
     * @param requestToWithdraw The request-to-withdraw payload containing amount, currency, and party details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Response` with an empty body; HTTP 202 indicates the request was accepted.
     */
    @POST(Constants.EndPoints.REQUEST_TO_WITHDRAW)
    suspend fun requestToWithdraw(
        @Body requestToWithdraw: RequestToWithdraw,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Creates a Collection invoice, prompting the [Invoice.intendedPayer] to pay via their wallet.
     *
     * The invoice expires after [Invoice.validityDuration] seconds. Poll [getInvoiceStatus] using
     * the same [uuid] as the reference ID to check whether payment has been completed.
     *
     * @param invoice The invoice payload containing amount, currency, and optional payer details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id; use this same ID to query the invoice status.
     * @return A `Response` with an empty body; HTTP 202 indicates the invoice was accepted.
     */
    @POST(Constants.EndPoints.INVOICE)
    suspend fun createInvoice(
        @Body invoice: Invoice,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Creates a Collection payment (V2). Poll [getPaymentStatus] with the same [uuid] as the
     * reference ID to check the outcome.
     *
     * @param payment The payment payload (amount/currency, references, notes, and options).
     * @param apiVersion The API version to target; use `v2_0` for this endpoint.
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id; use this same ID to query the payment status.
     * @return A `Response` with an empty body; HTTP 202 indicates the payment was accepted.
     */
    @POST(Constants.EndPoints.CREATE_PAYMENT)
    suspend fun createPayment(
        @Body payment: Payment,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Creates a pre-approval, allowing the merchant to charge the [PreApproval.payer]'s wallet
     * without per-transaction prompts until the pre-approval expires.
     *
     * @param preApproval The pre-approval payload containing the payer, currency, and validity.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid A UUID V4 used as the X-Reference-Id; use this same ID to query the pre-approval status.
     * @return A `Response` with an empty body; HTTP 202 indicates the pre-approval was accepted.
     */
    @POST(Constants.EndPoints.PRE_APPROVAL)
    suspend fun createPreApproval(
        @Body preApproval: PreApproval,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Retrieves the status of a previously initiated request-to-pay transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToPay].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Response` whose body is the parsed [RequestToPayStatus].
     */
    @GET(Constants.EndPoints.REQUEST_TO_PAY_STATUS)
    suspend fun requestToPayTransactionStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<RequestToPayStatus>

    /**
     * Retrieves the status of a previously initiated request-to-withdraw transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToWithdraw].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Response` whose body is the parsed [RequestToWithdrawStatus].
     */
    @GET(Constants.EndPoints.REQUEST_TO_WITHDRAW_STATUS)
    suspend fun requestToWithdrawTransactionStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<RequestToWithdrawStatus>

    /**
     * Retrieves the current status of a previously created Collection payment.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createPayment].
     * @param apiVersion The API version to target; use `v2_0` for this endpoint.
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Response` whose body is the parsed [PaymentStatus].
     */
    @GET(Constants.EndPoints.PAYMENT_STATUS)
    suspend fun getPaymentStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<PaymentStatus>

    /**
     * Retrieves the current status of a previously created invoice.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createInvoice].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Response` whose body contains the invoice status details as a `ResponseBody`.
     */
    @GET(Constants.EndPoints.INVOICE_STATUS)
    suspend fun getInvoiceStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<InvoiceStatus>

    /**
     * Retrieves the current status of a previously created pre-approval.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createPreApproval].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Response` whose body is the parsed [PreApprovalStatus].
     */
    @GET(Constants.EndPoints.PRE_APPROVAL_STATUS)
    suspend fun getPreApprovalStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<PreApprovalStatus>

    /**
     * Retrieves the list of approved pre-approvals for a given account holder.
     *
     * @param accountHolderIdType The type of the account holder identifier (e.g., MSISDN).
     * @param accountHolderId The account holder identifier of the payer whose pre-approvals to list.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Response` whose body is the parsed [ApprovedPreApprovals].
     */
    @GET(Constants.EndPoints.GET_APPROVED_PRE_APPROVALS)
    suspend fun getApprovedPreApprovals(
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_TYPE) accountHolderIdType: String,
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_ID) accountHolderId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<ApprovedPreApprovals>

    /**
     * Cancels a pending invoice before it is paid or expires.
     *
     * MTN's cancel-invoice endpoint is a `DELETE` that carries a JSON body ([InvoiceCancellation]),
     * so this uses [@HTTP][retrofit2.http.HTTP] with `hasBody = true` rather than a plain `@DELETE`.
     *
     * The invoice being cancelled is identified by the body's `externalId` (the value used when the
     * invoice was created).
     *
     * @param referenceId Per MTN: the UUID of the transaction used to get the result — uniquely identifies this invoice cancellation (URL path).
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param invoiceCancellation The request body; its `externalId` (reused from invoice creation) identifies the invoice.
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param uuid The `X-Reference-Id` header. Per MTN: the UUID V4 resource ID of the transaction, used
     *   e.g. for validating the status of the request.
     * @return A `Response` with an empty body; HTTP 200 indicates successful cancellation.
     */
    @HTTP(method = "DELETE", path = Constants.EndPoints.INVOICE_STATUS, hasBody = true)
    suspend fun cancelInvoice(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Body invoiceCancellation: InvoiceCancellation,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Cancels an active pre-approval, immediately revoking the merchant's ability to debit
     * the payer's wallet without per-transaction consent.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createPreApproval].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @return A `Response` with an empty body; HTTP 200 indicates successful cancellation.
     */
    @DELETE(Constants.EndPoints.PRE_APPROVAL_STATUS)
    suspend fun cancelPreApproval(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<Unit>
}
