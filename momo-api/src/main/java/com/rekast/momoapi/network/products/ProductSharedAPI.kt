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
import com.rekast.momoapi.model.api.UserInfoWithConsent
import com.rekast.momoapi.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
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
    @GET("/{productType}/{apiVersion}/account/balance")
    fun getAccountBalance(
        @Path("productType") productType: String,
        @Path("apiVersion") apiVersion: String,
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
    @GET("/{productType}/{apiVersion}/accountholder/msisdn/{acountHolder}/basicuserinfo")
    fun getBasicUserInfo(
        @Path("acountHolder") acountHolder: String,
        @Path("productType") productType: String,
        @Path("apiVersion") apiVersion: String,
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
    @GET("/{productType}/oauth2/{apiVersion}/userinfo")
    fun getUserInfoWithoutConsent(
        @Path("productType") productType: String,
        @Path("apiVersion") apiVersion: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<UserInfoWithConsent>

    @GET("/{productType}/{apiVersion}/transfer/{referenceId}")
    fun getTransferStatus(
        @Path("referenceId") referenceId: String,
        @Path("apiVersion") apiVersion: String,
        @Path("productType") productType: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(Constants.Headers.X_TARGET_ENVIRONMENT) environment: String,
    ): Call<ResponseBody>
}
