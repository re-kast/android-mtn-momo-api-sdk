/*
 *              Apache License
 *        Version 2.0, January 2004
 *     http://www.apache.org/licenses/
 *
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
package io.rekast.sdk.network

import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.model.authentication.ApiUserKey
import io.rekast.sdk.utils.MomoConstants
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
    @GET(MomoConstants.EndPoints.GET_API_USER)
    fun getApiUser(
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.API_USER) apiUser: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Call<ApiUser>

    /**
     * Makes a request to get the API Key.
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[apiUser] -- The API User ID (X-Reference-Id)
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[ApiUserKey] -- Returns the API Key
     */
    @POST(MomoConstants.EndPoints.GET_API_USER_KEY)
    fun getApiUserKey(
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.API_USER) apiUser: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Call<ApiUserKey>

    /**
     * Makes a request to get the Access Token
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[AccessToken] -- Returns the Access Token
     */
    @POST(MomoConstants.EndPoints.GET_ACCESS_TOKEN)
    fun getAccessToken(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Call<AccessToken>
}
