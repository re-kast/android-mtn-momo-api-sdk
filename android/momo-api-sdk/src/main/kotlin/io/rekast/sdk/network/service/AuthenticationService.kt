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
package io.rekast.sdk.network.service

import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.ApiKey
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import io.rekast.sdk.utils.MomoConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * This interface defines the Retrofit service for handling various authentication-related API calls.
 *
 * It includes methods for creating API users, retrieving API user details, generating API keys,
 * and obtaining access tokens. Each method corresponds to a specific endpoint in the MTN MOMO API.
 */
sealed interface AuthenticationService {

    /**
     * Creates a new API user.
     *
     * @param providerCallBackHost The callback host for the provider.
     * @param apiVersion The version of the API (e.g., v1_0 or v2_0).
     * @param uuid A unique identifier for the request.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Response] containing the created [ApiUser].
     */
    @POST(MomoConstants.EndPoints.CREATE_API_USER)
    suspend fun createApiUser(
        @Body providerCallBackHost: ProviderCallBackHost,
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Header(MomoConstants.Headers.X_REFERENCE_ID) uuid: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<ApiUser>

    /**
     * Retrieves the details of an existing API user.
     *
     * @param apiVersion The version of the API (e.g., v1_0 or v2_0).
     * @param apiUser The ID of the API user to retrieve.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Response] containing the requested [ApiUser].
     */
    @GET(MomoConstants.EndPoints.GET_API_USER)
    suspend fun getApiUser(
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.X_REFERENCE_ID) apiUser: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<ApiUser>

    /**
     * Creates a new API key for the specified API user.
     *
     * @param apiVersion The version of the API (e.g., v1_0 or v2_0).
     * @param apiUser The ID of the API user for whom to create the key.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Response] containing the generated [ApiKey].
     */
    @POST(MomoConstants.EndPoints.CREATE_API_KEY)
    suspend fun createApiKey(
        @Path(MomoConstants.EndpointPaths.API_VERSION) apiVersion: String,
        @Path(MomoConstants.EndpointPaths.X_REFERENCE_ID) apiUser: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<ApiKey>

    /**
     * Obtains an access token for the specified product type.
     *
     * @param productType The type of product for which to obtain the access token.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Response] containing the obtained [AccessToken].
     */
    @POST(MomoConstants.EndPoints.GET_ACCESS_TOKEN)
    suspend fun getAccessToken(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String
    ): Response<AccessToken>

    /**
     * Obtains an OAuth2 access token for the specified product type.
     *
     * @param productType The type of product for which to obtain the OAuth2 access token.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The target environment (e.g., production, sandbox).
     * @return A [Response] containing the obtained [Oauth2AccessToken].
     */
    @POST(MomoConstants.EndPoints.GET_OAUTH2_ACCESS_TOKEN)
    suspend fun getOauth2AccessToken(
        @Path(MomoConstants.EndpointPaths.PRODUCT_TYPE) productType: String,
        @Header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY) productSubscriptionKey: String,
        @Header(MomoConstants.Headers.X_TARGET_ENVIRONMENT) environment: String
    ): Response<Oauth2AccessToken>
}
