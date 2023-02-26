/*
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package com.rekast.momoapi.network.products

import com.rekast.momoapi.model.api.Transaction
import com.rekast.momoapi.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * This is the retrofit interface to handle the various calls to the Collection API. This interface defines the
 * method, the request and response from the API.
 */
sealed interface CollectionAPI : ProductSharedAPI {

    /**
     * Makes a request to pay a specific user
     * @param[transaction] -- This is the Transfer Payload [Transaction]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[Unit] -- Returns the Transfer Status
     */
    @POST(Constants.EndPoints.REQUEST_TO_PAY)
    fun requestToPay(
        @Body transaction: Transaction,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String,
    ): Call<Unit>

    /**
     * Makes a request to check the status of the payment request
     * @param[referenceId] -- The Transfer Reference ID. This is a UUID V4.
     * This is the ID used here [requestToPay]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[ResponseBody] -- Returns the Transfer Status
     */
    @GET(Constants.EndPoints.REQUEST_TO_PAY_STATUS)
    fun requestToPayTransactionStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<ResponseBody>

    /**
     * Makes a request to withdraw from a specific user
     * @param[transaction] -- This is the Transfer Payload [Transaction]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[Unit] -- Returns the Transfer Status
     */
    @POST(Constants.EndPoints.REQUEST_TO_WITHDRAW)
    fun requestToWithdraw(
        @Body transaction: Transaction,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String,
    ): Call<Unit>

    /**
     * Makes a request to check the status fo the withdrawal
     * @param[referenceId] -- The Transfer Reference ID. This is a UUID V4.
     * This is the ID used here [requestToPay]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[ResponseBody] -- Returns the Transfer Status
     */
    @GET(Constants.EndPoints.REQUEST_TO_WITHDRAW_STATUS)
    fun requestToWithdrawTransactionStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<ResponseBody>
}
