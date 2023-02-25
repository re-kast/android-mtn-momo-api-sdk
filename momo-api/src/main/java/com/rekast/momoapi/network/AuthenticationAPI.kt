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
package com.rekast.momoapi.network

import com.rekast.momoapi.model.authentication.AccessToken
import com.rekast.momoapi.model.authentication.ApiUser
import com.rekast.momoapi.model.authentication.ApiUserKey
import com.rekast.momoapi.utils.Constants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * This is the retrofit interface to handle the various calls to the Auth api.
 * This interface defines the method, the request and response from the API.
 */
sealed interface AuthenticationAPI {
    /**
     * Makes a request to get the API User. Used to confirm whether the API User provided exists
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[apiUser] -- The API User ID (X-Reference-Id)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[ApiUser] -- Returns the API User if available
     */
    @GET("/{apiVersion}/apiuser/{apiUser}")
    fun getApiUser(
        @Path("apiVersion") apiVersion: String,
        @Path("apiUser") apiUser: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
    ): Call<ApiUser>

    /**
     * Makes a request to get the API Key.
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[apiUser] -- The API User ID (X-Reference-Id)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[ApiUserKey] -- Returns the API Key
     */
    @POST("/{apiVersion}/apiuser/{apiUser}/apikey")
    fun getApiUserKey(
        @Path("apiVersion") apiVersion: String,
        @Path("apiUser") apiUser: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
    ): Call<ApiUserKey>

    /**
     * Makes a request to get the Access Token
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[AccessToken] -- Returns the Access Token
     */
    @POST("/{productType}/token/")
    fun getAccessToken(
        @Path("productType") productType: String,
        @Header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
    ): Call<AccessToken>
}
