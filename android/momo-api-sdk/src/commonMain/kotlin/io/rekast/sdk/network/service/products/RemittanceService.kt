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

import io.rekast.sdk.model.CashTransfer
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit service interface for the MTN MOMO Remittance product API.
 *
 * Extends [CommonService] with remittance-specific operations: cash transfer V2.
 * The V1 transfer endpoint is inherited from [CommonService] via [Constants.EndPoints.TRANSFER].
 */
sealed interface RemittanceService : CommonService {

    /**
     * Initiates a remittance cash transfer using the V2 endpoint, which supports extended
     * KYC fields for cross-border compliance where the sender is not an MTN mobile money subscriber.
     *
     * The request is accepted asynchronously; poll [getCashTransferStatus] with the same [uuid]
     * as the reference ID to retrieve the final transaction outcome.
     *
     * @param cashTransfer The cash transfer payload including recipient, amounts, and optional KYC fields.
     * @param apiVersion The API version to target (e.g., v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Remittance product.
     * @param environment The target environment (e.g., sandbox or production).
     * @param uuid A UUID V4 used as the X-Reference-Id to uniquely identify this request.
     * @return A `Response` with an empty body; HTTP 202 indicates the transfer was accepted.
     */
    @POST(Constants.EndPoints.CASH_TRANSFER)
    suspend fun cashTransfer(
        @Body cashTransfer: CashTransfer,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String
    ): Response<Unit>

    /**
     * Retrieves the status of a previously initiated cash transfer.
     *
     * @param referenceId The UUID V4 reference ID used when calling [cashTransfer].
     * @param apiVersion The API version to target (e.g., v2_0).
     * @param productSubscriptionKey The Ocp-Apim-Subscription-Key for the Remittance product.
     * @param environment The target environment (e.g., sandbox or production).
     * @return A `Response` whose body is the parsed [MomoTransaction].
     */
    @GET(Constants.EndPoints.CASH_TRANSFER_STATUS)
    suspend fun getCashTransferStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<MomoTransaction>
}
