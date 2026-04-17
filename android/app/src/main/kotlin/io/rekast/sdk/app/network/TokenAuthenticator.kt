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
package io.rekast.sdk.app.network

import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.utils.ApiConfig
import io.rekast.sdk.utils.Constants
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

/**
 * OkHttp [okhttp3.Authenticator] that automatically refreshes the Bearer access token
 * whenever a 401 Unauthorized response is received from a Bearer-protected endpoint.
 *
 * On a 401 the authenticator:
 * 1. Verifies the failed request was using Bearer auth (skips Basic-Auth or unauthenticated requests).
 * 2. Guards against infinite retry loops (max 1 retry per request).
 * 3. Calls the MTN MoMo token endpoint via a **dedicated [AuthenticationService]** backed by
 *    a Basic-Auth-only OkHttpClient with no authenticator, avoiding circular dependency.
 * 4. Saves the refreshed Bearer token to [CredentialStorage].
 * 5. If the OAuth2 access token is also expired, refreshes it via [AuthenticationService.getOauth2AccessToken]
 *    and saves it to [CredentialStorage].
 * 6. Returns the original request without modification — the [io.rekast.sdk.network.interceptor.auth.AccessTokenInterceptor]
 *    will read the new token from storage and attach the correct header on the retry pass.
 *
 * @param storage Encrypted credential store; used to read the API key and save refreshed tokens.
 * @param authService Token-refresh-only [AuthenticationService] backed by a Basic-Auth-only client.
 * @param config SDK configuration; supplies the target environment for OAuth2 token refresh calls.
 */
class TokenAuthenticator(
    private val storage: CredentialStorage,
    private val authService: AuthenticationService,
    private val config: ApiConfig
) {
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
     * - The Bearer token endpoint itself returns a non-2xx response.
     *
     * OAuth2 token refresh failure is non-fatal — the original request is still retried with the
     * refreshed Bearer token even if the OAuth2 refresh fails.
     *
     * @param route The route for the failed request (unused but required by the interface).
     * @param response The 401 response received from the server.
     * @return The original request to trigger a retry, or `null` to propagate the 401 to the caller.
     */
    fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        // Only handle responses to Bearer-authenticated requests.
        val authHeader = response.request.header(Constants.Headers.AUTHORIZATION) ?: return null
        if (!authHeader.startsWith(Constants.TokenTypes.BEARER)) return null

        // Stop after the first retry to avoid infinite 401 loops.
        if (retryCount(response) >= 1) return null

        val apiKey = storage.getApiKey()
        if (apiKey.isBlank()) {
            Timber.w("TokenAuthenticator: API key not available, cannot refresh token")
            return null
        }

        // Extract the product type (e.g. "collection") from the first URL path segment.
        val productType =
            response.request.url.pathSegments
                .firstOrNull()
        if (productType.isNullOrBlank()) {
            Timber.w("TokenAuthenticator: could not determine product type from URL")
            return null
        }

        // The subscription key used by the original request must be forwarded to the token endpoint.
        val subscriptionKey = response.request.header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY)
        if (subscriptionKey.isNullOrBlank()) {
            Timber.w("TokenAuthenticator: subscription key header missing from original request")
            return null
        }

        val newToken = refreshToken(productType, subscriptionKey) ?: return null
        storage.saveAccessToken(newToken)

        if (storage.getOauthAccessToken().isBlank()) {
            var authReqId = storage.getBackChannelAuthorizationRequestId()
            if (authReqId.isBlank()) {
                val loginHint = storage.getLoginHint()
                if (loginHint.isNotBlank()) {
                    authReqId = refreshBackChannelAuthorization(productType, subscriptionKey, loginHint) ?: ""
                } else {
                    Timber.w("TokenAuthenticator: login hint missing, cannot refresh bc-authorize")
                }
            }
            if (authReqId.isNotBlank()) {
                val newOauthToken = refreshOauthToken(productType, subscriptionKey, authReqId)
                if (newOauthToken != null) storage.saveOauthAccessToken(newOauthToken)
            }
        }

        Timber.d("TokenAuthenticator: token refreshed successfully")

        // Return the original request unchanged — the AccessTokenInterceptor will re-attach
        // the fresh Bearer token from storage on the retry pass.
        return response.request
    }

    /**
     * Calls the MTN MoMo token endpoint via [authService] to obtain a new [AccessToken].
     *
     * [authService] is backed by a minimal, authenticator-free [okhttp3.OkHttpClient] that
     * attaches Basic Auth automatically, avoiding any circular dependency with the main client.
     *
     * @param productType The first path segment of the original request URL (e.g. "collection").
     * @param subscriptionKey The `Ocp-Apim-Subscription-Key` header value from the original request.
     * @return The decoded [AccessToken] on success, or `null` if the request fails.
     */
    private fun refreshToken(
        productType: String,
        subscriptionKey: String
    ): AccessToken? =
        try {
            val response = runBlocking { authService.getAccessToken(productType, subscriptionKey) }
            if (response.isSuccessful) {
                response.body()
            } else {
                Timber.e("TokenAuthenticator: token refresh failed with HTTP ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "TokenAuthenticator: exception during token refresh")
            null
        }

    /**
     * Calls the bc-authorize endpoint to obtain a fresh `auth_req_id`, then persists it.
     *
     * @param productType The product type extracted from the original request URL.
     * @param subscriptionKey The `Ocp-Apim-Subscription-Key` from the original request.
     * @param loginHint The stored MSISDN login hint.
     * @return The new `auth_req_id` on success, or `null` if the request fails.
     */
    private fun refreshBackChannelAuthorization(
        productType: String,
        subscriptionKey: String,
        loginHint: String
    ): String? =
        try {
            val response =
                runBlocking {
                    authService.bcAuthorize(
                        productType = productType,
                        apiVersion = config.apiVersion,
                        loginHint = loginHint,
                        scope = Constants.FormFields.CIBA_SCOPE,
                        accessType = Constants.FormFields.CIBA_ACCESS_TYPE,
                        productSubscriptionKey = subscriptionKey,
                        environment = config.environment
                    )
                }
            if (response.isSuccessful) {
                response.body()?.also { storage.saveBackChannelAuthorizationRequestId(it.authReqId, it.expiresIn) }?.authReqId
            } else {
                Timber.e("TokenAuthenticator: bc-authorize failed with HTTP ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "TokenAuthenticator: exception during bc-authorize")
            null
        }

    /**
     * Calls the MTN MoMo OAuth2 token endpoint via [authService] to obtain a new [Oauth2AccessToken].
     *
     * Only called when [CredentialStorage.getOauthAccessToken] returns blank (i.e. the stored
     * OAuth2 token is missing or expired). A failure here is non-fatal — the caller continues with
     * the refreshed Bearer token regardless.
     *
     * @param productType The first path segment of the original request URL (e.g. "collection").
     * @param subscriptionKey The `Ocp-Apim-Subscription-Key` header value from the original request.
     * @param authReqId The `auth_req_id` from a prior bc-authorize response.
     * @return The decoded [Oauth2AccessToken] on success, or `null` if the request fails.
     */
    private fun refreshOauthToken(
        productType: String,
        subscriptionKey: String,
        authReqId: String
    ): Oauth2AccessToken? =
        try {
            val response =
                runBlocking {
                    authService.getOauth2AccessToken(productType, subscriptionKey, config.environment, authReqId = authReqId)
                }
            if (response.isSuccessful) {
                response.body()
            } else {
                Timber.e("TokenAuthenticator: OAuth2 token refresh failed with HTTP ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "TokenAuthenticator: exception during OAuth2 token refresh")
            null
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
