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

import com.rekast.momoapi.model.api.AccountBalance
import com.rekast.momoapi.model.api.BasicUserInfo
import com.rekast.momoapi.model.api.Notification
import com.rekast.momoapi.model.api.UserInfoWithConsent
import com.rekast.momoapi.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * This is the retrofit interface to handle the various calls to the Shared Product APIs. This interface defines the
 * method, the request and response from the API.
 */
sealed interface ProductSharedAPI {
    /**
     * Makes a request to get the Account Balance
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[AccountBalance] -- Returns the Account Balance
     */
    @GET(Constants.EndPoints.GET_ACCOUNT_BALANCE)
    fun getAccountBalance(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<AccountBalance>

    /**
     * Makes a request to get the Basic User Info
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[BasicUserInfo] -- Returns the Basic User Info
     */
    @GET(Constants.EndPoints.GET_BASIC_USER_INFO)
    fun getBasicUserInfo(
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_ID) accountHolder: String,
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<BasicUserInfo>

    /**
     * Makes a request to get the User Info with Consent
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[UserInfoWithConsent] -- Returns the User Info with Consent
     */
    @GET(Constants.EndPoints.GET_USER_INFO_WITH_CONSENT)
    fun getUserInfoWithConsent(
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<UserInfoWithConsent>

    /**
     * Makes a request to get the User Info with Consent
     * @param[referenceId] -- The Transfer Reference ID. This is a UUID V4.
     * This is the ID used here [RemittanceAPI.transfer]
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[ResponseBody] -- Returns the Transfer Status
     */
    @GET(Constants.EndPoints.GET_TRANSFER_STATUS)
    fun getTransferStatus(
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<ResponseBody>

    /**
     * Makes a request to send the delivery notification
     * @param[notification] -- The notification message. This is by default capped 1t 160 characters.
     * Use the [Setting.checkNotificationMessageLength] to check the length
     * @param[referenceId] -- The Transfer Reference ID. This is a UUID V4. It should be the customer Id.
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @param[notificationMessage] -- This is the notification message to be sent to the user.
     * It's the same as the [Notification.notificationMessage] added on the [notification]
     * @return[ResponseBody] -- Returns the Transfer Status
     */
    @POST(Constants.EndPoints.REQUEST_TO_PAY_DELIVERY_NOTIFICATION)
    fun requestToPayDeliveryNotification(
        @Body notification: Notification,
        @Path(Constants.EndpointPaths.REFERENCE_ID) referenceId: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
        // @Header(Constants.Headers.NOTIFICATION_MESSAGE) notificationMessage: String,
    ): Call<ResponseBody>

    /**
     * Makes a request to check the account holder status
     * @param[accountHolderId] -- This is the account holder unique Id e.g the phone number
     * @param[accountHolderType] -- This is the account holder type. e.g MSISDN
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[ResponseBody] -- Returns the Transfer Status
     */
    @GET(Constants.EndPoints.VALIDATE_ACCOUNT_HOLDER_STATUS)
    fun validateAccountHolderStatus(
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_ID) accountHolderId: String,
        @Path(Constants.EndpointPaths.ACCOUNT_HOLDER_TYPE) accountHolderType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<ResponseBody>

    /**
     * Makes a request to get the Account Balance with a currency.
     * @param[currency] -- This is the Currency based on the ISO standard. Read more here https://www.iban.com/currency-codes
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[environment] -- The API environment (X-Target-Environment)
     * @return[AccountBalance] -- Returns the Account Balance
     */
    @GET(Constants.EndPoints.GET_ACCOUNT_BALANCE_IN_SPECIFIC_CURRENCY)
    fun getAccountBalanceInSpecificCurrency(
        @Path(Constants.EndpointPaths.CURRENCY) currency: String,
        @Path(Constants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Path(Constants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<AccountBalance>
}