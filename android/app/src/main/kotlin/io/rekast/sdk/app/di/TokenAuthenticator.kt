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
package io.rekast.sdk.app.di

import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.utils.MomoApiConfig
import io.rekast.sdk.utils.MomoConstants
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * OkHttp [okhttp3.Authenticator] that automatically refreshes the Bearer access token
 * whenever a 401 Unauthorized response is received from a Bearer-protected endpoint.
 *
 * On a 401 the authenticator:
 * 1. Verifies the failed request was using Bearer auth (skips Basic-Auth or unauthenticated requests).
 * 2. Guards against infinite retry loops (max 1 retry per request).
 * 3. Calls the MTN MoMo token endpoint using a **separate, minimal [OkHttpClient]** (no
 *    authenticator attached) so there is no circular dependency with the main client.
 * 4. Saves the fresh token to [CredentialStorage].
 * 5. Returns the original request without modification — the [io.rekast.sdk.network.interceptor.auth.AccessTokenInterceptor]
 *    will read the new token from storage and attach the correct header on the retry pass.
 *
 * @param storage Encrypted credential store; used to read the API key and save the refreshed token.
 * @param config SDK configuration supplying the base URL and API user ID.
 */
class TokenAuthenticator(
    private val storage: CredentialStorage,
    private val config: MomoApiConfig
) {

    /** Separate client used only for token-refresh calls — no Authenticator, no Bearer interceptor. */
    private val authClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    /** JSON decoder configured to tolerate unknown fields returned by the token endpoint. */
    private val json: Json = Json { ignoreUnknownKeys = true }

    /**
     * Called by OkHttp whenever a response with HTTP 401 is received.
     *
     * Inspects the failed request to confirm it was using Bearer authentication, then
     * attempts a single token refresh. Returns the original request (unchanged) so that
     * OkHttp re-runs the application interceptors — [io.rekast.sdk.network.interceptor.auth.AccessTokenInterceptor]
     * will pick up the new token from [CredentialStorage] and attach the correct header.
     *
     * Returns `null` to give up without retrying in any of the following cases:
     * - The original request did not use Bearer auth.
     * - More than one prior response exists for this request (retry limit reached).
     * - The API key is unavailable (cannot form a Basic Auth token-exchange request).
     * - The product type or subscription key cannot be extracted from the original request.
     * - The token endpoint itself returns a non-2xx response.
     *
     * @param route The route for the failed request (unused but required by the interface).
     * @param response The 401 response received from the server.
     * @return The original request to trigger a retry, or `null` to propagate the 401 to the caller.
     */
    fun authenticate(route: Route?, response: Response): Request? {
        // Only handle responses to Bearer-authenticated requests.
        val authHeader = response.request.header(MomoConstants.Headers.AUTHORIZATION) ?: return null
        if (!authHeader.startsWith(MomoConstants.TokenTypes.BEARER)) return null

        // Stop after the first retry to avoid infinite 401 loops.
        if (retryCount(response) >= 1) return null

        val apiKey = storage.getApiKey()
        if (apiKey.isBlank()) {
            Timber.w("TokenAuthenticator: API key not available, cannot refresh token")
            return null
        }

        // Extract the product type (e.g. "collection") from the first URL path segment.
        val productType = response.request.url.pathSegments.firstOrNull()
        if (productType.isNullOrBlank()) {
            Timber.w("TokenAuthenticator: could not determine product type from URL")
            return null
        }

        // The subscription key used by the original request must be forwarded to the token endpoint.
        val subscriptionKey = response.request.header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY)
        if (subscriptionKey.isNullOrBlank()) {
            Timber.w("TokenAuthenticator: subscription key header missing from original request")
            return null
        }

        val newToken = refreshToken(productType, subscriptionKey, apiKey) ?: return null

        storage.saveAccessToken(newToken)
        Timber.d("TokenAuthenticator: token refreshed successfully")

        // Return the original request unchanged — the AccessTokenInterceptor will re-attach
        // the fresh Bearer token from storage on the retry pass.
        return response.request
    }

    /**
     * Calls the MTN MoMo token endpoint using Basic Authentication to obtain a new [AccessToken].
     *
     * Uses [authClient] (a minimal, authenticator-free [OkHttpClient]) to avoid a circular
     * dependency with the main client. The request is authenticated with the API user ID and
     * [apiKey] encoded as a Base64 Basic Auth credential.
     *
     * @param productType The first path segment of the original request URL (e.g. "collection").
     * @param subscriptionKey The `Ocp-Apim-Subscription-Key` header value from the original request.
     * @param apiKey The API key used to form the Basic Auth credential.
     * @return The decoded [AccessToken] on success, or `null` if the request fails or the body
     *         cannot be parsed.
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun refreshToken(productType: String, subscriptionKey: String, apiKey: String): AccessToken? {
        return try {
            val credentials = "${config.apiUserId}:$apiKey"
            val encoded = Base64.Default.encode(credentials.toByteArray())

            val tokenUrl = "${config.baseUrl.trimEnd('/')}/$productType/token/"
            val refreshRequest = Request.Builder()
                .url(tokenUrl)
                .post("".toRequestBody(null))
                .header(MomoConstants.Headers.AUTHORIZATION, "${MomoConstants.TokenTypes.BASIC} $encoded")
                .header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY, subscriptionKey)
                .build()

            val refreshResponse = authClient.newCall(refreshRequest).execute()
            if (!refreshResponse.isSuccessful) {
                Timber.e("TokenAuthenticator: token refresh failed with HTTP ${refreshResponse.code}")
                return null
            }

            val body = refreshResponse.body.string() ?: return null
            json.decodeFromString<AccessToken>(body)
        } catch (e: Exception) {
            Timber.e(e, "TokenAuthenticator: exception during token refresh")
            null
        }
    }

    /**
     * Counts how many prior 401 responses exist in the response chain.
     *
     * OkHttp chains responses via [Response.priorResponse] when the same request has been
     * retried before. A count of 1 or more means this request has already been retried once,
     * so [authenticate] will return `null` to stop further retries.
     *
     * @param response The current response from OkHttp.
     * @return The number of prior responses in the chain.
     */
    private fun retryCount(response: Response): Int {
        var count = 0
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
