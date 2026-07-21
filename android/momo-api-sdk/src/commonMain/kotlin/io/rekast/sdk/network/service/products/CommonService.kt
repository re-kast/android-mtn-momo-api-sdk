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

import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.Notifications
import io.rekast.sdk.model.Transfer
import io.rekast.sdk.model.TransferStatus
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.utils.Constants
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
     * Makes a request to transfer funds.
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param transfer The transfer payload [Transfer].
     * @param uuid The unique reference ID for the transfer.
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @return A `Response` indicating the result of the transfer.
     */
    @POST(Constants.EndPoints.TRANSFER)
    suspend fun transfer(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Body transfer: Transfer,
        @Header(Constants.Headers.X_REFERENCE_ID) uuid: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<Unit>

    /**
     * Makes a request to send a delivery notification.
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param referenceId The transfer reference ID (UUID V4).
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
     * Makes a request to get the Basic ApiUser Info.
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param accountHolder The account holder ID.
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @return A `Response` containing the [BasicUserInfo].
     */
    @GET(Constants.EndPoints.GET_BASIC_USER_INFO)
    suspend fun getBasicUserInfo(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_ID) accountHolder: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<BasicUserInfo>

    /**
     * Makes a request to get the ApiUser Info with Consent.
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @return A `Response` containing the [UserInfoWithConsent].
     */
    @GET(Constants.EndPoints.GET_USER_INFO_WITH_CONSENT)
    suspend fun getUserInfoWithConsent(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<UserInfoWithConsent>

    /**
     * Makes a request to check the account holder status.
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param accountHolderId The account holder unique ID (e.g., phone number).
     * @param accountHolderType The account holder type (e.g., MSISDN).
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @return A `Response` whose body is the parsed [AccountHolderStatus] (`{"result": <bool>}`).
     */
    @GET(Constants.EndPoints.VALIDATE_ACCOUNT_HOLDER_STATUS)
    suspend fun validateAccountHolderStatus(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_ID) accountHolderId: String,
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_TYPE) accountHolderType: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<AccountHolderStatus>

    /**
     * Makes a request to get the Account Balance. This only works with the [io.rekast.sdk.utils.ProductTypes.COLLECTION]. It seems to break with the other API product type.
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @return A `Response` containing the [AccountBalance].
     */
    @GET(Constants.EndPoints.GET_ACCOUNT_BALANCE)
    suspend fun getAccountBalance(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<AccountBalance>

    /**
     * Makes a request to get the Account Balance in a specific currency. This only works with the [io.rekast.sdk.utils.ProductTypes.COLLECTION]. It seems to break with the other API product type.
     * Use EUR as the currency on sandbox
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param currency The currency based on the ISO standard.
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @return A `Response` containing the [AccountBalance].
     */
    @GET(Constants.EndPoints.GET_ACCOUNT_BALANCE_IN_SPECIFIC_CURRENCY)
    suspend fun getAccountBalanceInSpecificCurrency(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.CURRENCY) currency: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<AccountBalance>

    /**
     * Makes a request to get the transfer status.
     *
     * @param productType The API product ([io.rekast.sdk.utils.ProductTypes]).
     * @param apiVersion The app Version (e.g., v1_0 or v2_0).
     * @param referenceId The transfer reference ID (UUID V4).
     * @param productSubscriptionKey The Product subscription Key (Ocp-Apim-Subscription-Key).
     * @return A `Response` whose body is the parsed [TransferStatus].
     */
    @GET(Constants.EndPoints.GET_TRANSFER_STATUS)
    suspend fun getTransferStatus(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<TransferStatus>
}
