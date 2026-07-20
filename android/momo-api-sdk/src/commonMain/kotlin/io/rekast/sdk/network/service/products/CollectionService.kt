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

import io.rekast.sdk.model.Invoice
import io.rekast.sdk.model.MomoNotification
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.model.PreApproval
import io.rekast.sdk.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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
     * @param momoTransaction The transaction payload containing amount, currency, and party details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Response` with an empty body; HTTP 202 indicates the request was accepted.
     */
    @POST(Constants.EndPoints.REQUEST_TO_PAY)
    suspend fun requestToPay(
        @Body momoTransaction: MomoTransaction,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Retrieves the status of a previously initiated request-to-pay transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToPay].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` whose body contains the transaction status as a `ResponseBody`.
     */
    @GET(Constants.EndPoints.REQUEST_TO_PAY_STATUS)
    suspend fun requestToPayTransactionStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>

    /**
     * Initiates a request-to-withdraw, prompting the specified payer to approve a withdrawal.
     *
     * @param momoTransaction The transaction payload containing amount, currency, and party details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Response` with an empty body; HTTP 202 indicates the request was accepted.
     */
    @POST(Constants.EndPoints.REQUEST_TO_WITHDRAW)
    suspend fun requestToWithdraw(
        @Body momoTransaction: MomoTransaction,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Retrieves the status of a previously initiated request-to-withdraw transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToWithdraw].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` whose body contains the withdrawal status as a `ResponseBody`.
     */
    @GET(Constants.EndPoints.REQUEST_TO_WITHDRAW_STATUS)
    suspend fun requestToWithdrawTransactionStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>

    /**
     * Creates a Collection invoice, prompting the [Invoice.intendedPayer] to pay via their wallet.
     *
     * The invoice expires after [Invoice.validityDuration] seconds. Poll [getInvoiceStatus] using
     * the same [uuid] as the reference ID to check whether payment has been completed.
     *
     * @param invoice The invoice payload containing amount, currency, and optional payer details.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @param uuid A UUID V4 used as the X-Reference-Id; use this same ID to query the invoice status.
     * @return A `Response` with an empty body; HTTP 202 indicates the invoice was accepted.
     */
    @POST(Constants.EndPoints.INVOICE)
    suspend fun createInvoice(
        @Body invoice: Invoice,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Retrieves the current status of a previously created invoice.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createInvoice].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` whose body contains the invoice status details as a `ResponseBody`.
     */
    @GET(Constants.EndPoints.INVOICE_STATUS)
    suspend fun getInvoiceStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>

    /**
     * Cancels a pending invoice before it is paid or expires.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createInvoice].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` with an empty body; HTTP 200 indicates successful cancellation.
     */
    @DELETE(Constants.EndPoints.INVOICE_STATUS)
    suspend fun cancelInvoice(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<Unit>

    /**
     * Creates a pre-approval, allowing the merchant to charge the [PreApproval.payer]'s wallet
     * without per-transaction prompts until the pre-approval expires.
     *
     * @param preApproval The pre-approval payload containing the payer, currency, and validity.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @param uuid A UUID V4 used as the X-Reference-Id; use this same ID to query the pre-approval status.
     * @return A `Response` with an empty body; HTTP 202 indicates the pre-approval was accepted.
     */
    @POST(Constants.EndPoints.PRE_APPROVAL)
    suspend fun createPreApproval(
        @Body preApproval: PreApproval,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Retrieves the current status of a previously created pre-approval.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createPreApproval].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` whose body contains the pre-approval status details as a `ResponseBody`.
     */
    @GET(Constants.EndPoints.PRE_APPROVAL_STATUS)
    suspend fun getPreApprovalStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>

    /**
     * Cancels an active pre-approval, immediately revoking the merchant's ability to debit
     * the payer's wallet without per-transaction consent.
     *
     * @param referenceId The UUID V4 reference ID used when calling [createPreApproval].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` with an empty body; HTTP 200 indicates successful cancellation.
     */
    @DELETE(Constants.EndPoints.PRE_APPROVAL_STATUS)
    suspend fun cancelPreApproval(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<Unit>

    /**
     * Retrieves the list of approved pre-approvals for a given account holder.
     *
     * @param accountHolderIdType The type of the account holder identifier (e.g., MSISDN).
     * @param accountHolderId The account holder identifier of the payer whose pre-approvals to list.
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` whose body contains the approved pre-approvals as a `ResponseBody`.
     */
    @GET(Constants.EndPoints.GET_APPROVED_PRE_APPROVALS)
    suspend fun getApprovedPreApprovals(
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_TYPE) accountHolderIdType: String,
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_ID) accountHolderId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>

    /**
     * Sends a delivery notification for a request-to-withdraw transaction.
     *
     * @param referenceId The UUID V4 reference ID used when calling [requestToWithdraw].
     * @param apiVersion The API version to target (e.g., v1_0 or v2_0).
     * @param momoNotification The notification payload containing the message to deliver.
     * @param notificationMessage The notification message text (also sent as a header per MTN API spec).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Collection product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` whose body contains the delivery result as a `ResponseBody`.
     */
    @POST(Constants.EndPoints.REQUEST_TO_WITHDRAW_DELIVERY_NOTIFICATION)
    suspend fun requestToWithdrawDeliveryNotification(
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Body momoNotification: MomoNotification,
        @Header(Constants.Headers.NOTIFICATION_MESSAGE) notificationMessage: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>
}
