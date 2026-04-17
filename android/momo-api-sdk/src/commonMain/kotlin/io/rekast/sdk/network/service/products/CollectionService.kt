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
package io.rekast.sdk.network.service.products

import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
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
     * @return A [Response] with an empty body; HTTP 202 indicates the request was accepted.
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
     * @return A [Response] whose body contains the transaction status as a [ResponseBody].
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
     * @return A [Response] with an empty body; HTTP 202 indicates the request was accepted.
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
     * @return A [Response] whose body contains the withdrawal status as a [ResponseBody].
     */
    @GET(Constants.EndPoints.REQUEST_TO_WITHDRAW_STATUS)
    suspend fun requestToWithdrawTransactionStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>
}
