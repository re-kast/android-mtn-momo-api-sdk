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

import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.MomoNotification
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.utils.MomoConstants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * This is the Retrofit interface to handle various calls to the Shared Product APIs.
 * This interface defines the methods, the request, and the response from the API.
 */
sealed interface CommonService {
    /**
     * Makes a request to get the Basic ApiUser Info.
     *
     * @param productType The API Products ([MomoConstants.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param accountHolder The account holder ID.
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @param environment The API environment (X-Target-Environment).
     * @return A [Response] containing the [BasicUserInfo].
     */
    @GET(MomoConstants.EndPoints.GET_BASIC_USER_INFO)
    suspend fun getBasicUserInfo(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.ACCOUNT_HOLDER_ID) accountHolder: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<BasicUserInfo>

    /**
     * Makes a request to get the ApiUser Info with Consent.
     *
     * @param productType The API Products ([MomoConstants.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @param environment The API environment (X-Target-Environment).
     * @return A [Response] containing the [UserInfoWithConsent].
     */
    @GET(MomoConstants.EndPoints.GET_USER_INFO_WITH_CONSENT)
    suspend fun getUserInfoWithConsent(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<UserInfoWithConsent>

    /**
     * Makes a request to check the account holder status.
     *
     * @param productType The API Products ([MomoConstants.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param accountHolderId The account holder unique ID (e.g., phone number).
     * @param accountHolderType The account holder type (e.g., MSISDN).
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @param environment The API environment (X-Target-Environment).
     * @return A [ResponseBody] indicating the result of the account holder status check.
     */
    @GET(MomoConstants.EndPoints.VALIDATE_ACCOUNT_HOLDER_STATUS)
    suspend fun validateAccountHolderStatus(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.ACCOUNT_HOLDER_ID) accountHolderId: String,
        @Path(MomoConstants.EndpointPaths.ACCOUNT_HOLDER_TYPE) accountHolderType: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>

    /**
     * Makes a request to get the Account Balance. This only works with the [ProductType.COLLECTION]. It seems to break with the other API product type.
     *
     * @param productType The API Products ([MomoConstants.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @param environment The API environment (X-Target-Environment).
     * @return A [Response] containing the [AccountBalance].
     */
    @GET(MomoConstants.EndPoints.GET_ACCOUNT_BALANCE)
    suspend fun getAccountBalance(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<AccountBalance>

    /**
     * Makes a request to get the Account Balance in a specific currency. This only works with the [ProductType.COLLECTION]. It seems to break with the other API product type.
     * User EUR as the currency on sandbox
     *
     * @param productType The API Products ([MomoConstants.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param currency The currency based on the ISO standard.
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @param environment The API environment (X-Target-Environment).
     * @return A [Response] containing the [AccountBalance].
     */
    @GET(MomoConstants.EndPoints.GET_ACCOUNT_BALANCE_IN_SPECIFIC_CURRENCY)
    suspend fun getAccountBalanceInSpecificCurrency(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.CURRENCY) currency: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<AccountBalance>

    /**
     * Makes a request to transfer funds.
     *
     * @param productType The API Products ([MomoConstants.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param momoTransaction The transfer payload [MomoTransaction].
     * @param uuid The unique reference ID for the transfer.
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @param environment The API environment (X-Target-Environment).
     * @return A [Response] indicating the result of the transfer.
     */
    @POST(MomoConstants.EndPoints.TRANSFER)
    suspend fun transfer(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Body momoTransaction: MomoTransaction,
        @Header(MomoConstants.Headers.X_REFERENCE_ID) uuid: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<Unit>

    /**
     * Makes a request to get the transfer status.
     *
     * @param productType The API Products ([MomoConstants.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param referenceId The transfer reference ID (UUID V4).
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @param environment The API environment (X-Target-Environment).
     * @return A [ResponseBody] containing the transfer status.
     */
    @GET(MomoConstants.EndPoints.GET_TRANSFER_STATUS)
    suspend fun getTransferStatus(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>

    /**
     * Makes a request to send a delivery notification.
     *
     * @param productType The API Products ([MomoConstants.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param referenceId The transfer reference ID (UUID V4).
     * @param momoNotification The notification message.
     * @param notificationMessage The message to be sent to the user.
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @param environment The API environment (X-Target-Environment).
     * @return A [ResponseBody] indicating the result of the notification request.
     */
    @POST(MomoConstants.EndPoints.REQUEST_TO_PAY_DELIVERY_NOTIFICATION)
    suspend fun requestToPayDeliveryNotification(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Body momoNotification: MomoNotification,
        @Header(MomoConstants.Headers.NOTIFICATION_MESSAGE) notificationMessage: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<ResponseBody>
}
