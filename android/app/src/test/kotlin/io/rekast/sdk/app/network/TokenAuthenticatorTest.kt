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

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.rekast.sdk.model.BackChannelAuthorize
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.utils.ApiConfig
import io.rekast.sdk.utils.Constants
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response as RetrofitResponse

/**
 * Unit tests for [TokenAuthenticator].
 *
 * Covers every early-return guard in [TokenAuthenticator.authenticate], the happy-path where both
 * the Bearer token and the OAuth2 token are refreshed, and the non-fatal OAuth2 failure path where
 * only the Bearer token is refreshed. [AuthenticationService] is mocked with MockK to keep the
 * tests self-contained and fast. [TokenAuthenticator.retryCount] is exercised indirectly through
 * the retry-limit tests, which chain prior responses to simulate previous retry attempts.
 */
class TokenAuthenticatorTest {
    private val mockStorage: CredentialStorage = mockk(relaxed = true)
    private val mockAuthService: AuthenticationService = mockk()
    private val config =
        ApiConfig(
            baseUrl = "https://sandbox.momodeveloper.mtn.com/",
            apiUserId = "test-user-id",
            environment = "sandbox"
        )

    private lateinit var authenticator: TokenAuthenticator

    @Before
    fun setUp() {
        authenticator = TokenAuthenticator(mockStorage, mockAuthService, config)
    }

    /**
     * Builds a minimal 401 [Response] with the given URL and headers.
     * An optional [priorResponse] is chained to simulate a previous retry.
     */
    private fun buildUnauthorizedResponse(
        url: String,
        authHeader: String? = "${Constants.TokenTypes.BEARER} some-token",
        subscriptionKey: String? = "sub-key-001",
        priorResponse: Response? = null
    ): Response {
        val requestBuilder = Request.Builder().url(url).get()
        authHeader?.let { requestBuilder.header(Constants.Headers.AUTHORIZATION, it) }
        subscriptionKey?.let { requestBuilder.header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY, it) }
        val request = requestBuilder.build()

        val builder =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized")
                .body("".toResponseBody(null))

        priorResponse?.let { builder.priorResponse(it) }
        return builder.build()
    }

    private fun collectionUrl() = "https://sandbox.momodeveloper.mtn.com/collection/v1_0/accounts"

    private fun stubAccessTokenSuccess(token: String = "new-token") {
        coEvery { mockAuthService.getAccessToken(any(), any()) } returns
            RetrofitResponse.success(AccessToken(accessToken = token, tokenType = "Bearer", expiresIn = 3600))
    }

    private fun stubOauthTokenSuccess(token: String = "new-oauth-token") {
        coEvery { mockAuthService.getOauth2AccessToken(any(), any(), any(), any(), any()) } returns
            RetrofitResponse.success(
                Oauth2AccessToken(
                    accessToken = token,
                    tokenType = "Bearer",
                    expiresIn = 3600,
                    scope = "profile",
                    refreshToken = "refresh-123",
                    refreshTokenExpiredIn = 86400
                )
            )
    }

    private fun stubBcAuthorizeSuccess(authReqId: String = "bc-req-id-123") {
        coEvery { mockAuthService.bcAuthorize(any(), any(), any(), any(), any(), any(), any()) } returns
            RetrofitResponse.success(BackChannelAuthorize(authReqId = authReqId, interval = 5, expiresIn = 300))
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` when the failed request
     * has no `Authorization` header — the request was unauthenticated so there is nothing to refresh.
     */
    @Test
    fun `authenticate returns null when Authorization header is absent`() {
        val response = buildUnauthorizedResponse(collectionUrl(), authHeader = null)

        assertNull(authenticator.authenticate(null, response))
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` when the `Authorization`
     * header uses Basic auth rather than Bearer — token refresh only applies to Bearer requests.
     */
    @Test
    fun `authenticate returns null when Authorization header is Basic not Bearer`() {
        val response =
            buildUnauthorizedResponse(
                collectionUrl(),
                authHeader = "${Constants.TokenTypes.BASIC} dXNlcjpwYXNz"
            )

        assertNull(authenticator.authenticate(null, response))
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` when a prior response exists
     * in the response chain, preventing infinite retry loops by limiting to one refresh attempt.
     */
    @Test
    fun `authenticate returns null when retry count is already 1`() {
        val prior = buildUnauthorizedResponse(collectionUrl())
        val response = buildUnauthorizedResponse(collectionUrl(), priorResponse = prior)

        assertNull(authenticator.authenticate(null, response))
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` when [CredentialStorage]
     * returns a blank API key — a Basic Auth token-exchange request cannot be formed without it.
     */
    @Test
    fun `authenticate returns null when API key is blank`() {
        every { mockStorage.getApiKey() } returns ""

        val response = buildUnauthorizedResponse(collectionUrl())

        assertNull(authenticator.authenticate(null, response))
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` when the request URL has no
     * meaningful path segment from which to determine the product type (e.g. "collection").
     */
    @Test
    fun `authenticate returns null when URL has no meaningful path segment`() {
        every { mockStorage.getApiKey() } returns "test-api-key"

        val response = buildUnauthorizedResponse("https://sandbox.momodeveloper.mtn.com/")

        assertNull(authenticator.authenticate(null, response))
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` when the original request
     * is missing the `Ocp-Apim-Subscription-Key` header required by the token endpoint.
     */
    @Test
    fun `authenticate returns null when subscription key header is missing`() {
        every { mockStorage.getApiKey() } returns "test-api-key"

        val response = buildUnauthorizedResponse(collectionUrl(), subscriptionKey = null)

        assertNull(authenticator.authenticate(null, response))
    }

    /**
     * Happy path: all guards pass, the Bearer token is refreshed and saved. The OAuth2 token is
     * still valid so [AuthenticationService.getOauth2AccessToken] is never called.
     */
    @Test
    fun `authenticate returns original request and saves access token when OAuth2 token is valid`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        every { mockStorage.getOauthAccessToken() } returns "valid-oauth-token"
        stubAccessTokenSuccess("new-token")

        val response = buildUnauthorizedResponse(collectionUrl())
        val result = authenticator.authenticate(null, response)

        assertNotNull("Should return the original request for retry", result)
        assertEquals(response.request.url, result!!.url)

        verify(exactly = 1) {
            mockStorage.saveAccessToken(
                withArg { token -> assertEquals("new-token", token.accessToken) }
            )
        }
        verify(exactly = 0) { mockStorage.saveOauthAccessToken(any()) }
    }

    /**
     * Verifies that when the OAuth2 token is expired and the `auth_req_id` is already stored,
     * [TokenAuthenticator.authenticate] refreshes both the Bearer token and the OAuth2 token
     * directly — without calling bc-authorize again.
     */
    @Test
    fun `authenticate refreshes OAuth2 token using stored authReqId when OAuth2 token is expired`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns "stored-auth-req-id"
        stubAccessTokenSuccess()
        stubOauthTokenSuccess("new-oauth-token")

        val result = authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl()))

        assertNotNull(result)
        verify(exactly = 1) { mockStorage.saveAccessToken(any()) }
        verify(exactly = 1) {
            mockStorage.saveOauthAccessToken(
                withArg { token -> assertEquals("new-oauth-token", token.accessToken) }
            )
        }
        coVerify(exactly = 0) { mockAuthService.bcAuthorize(any(), any(), any(), any(), any(), any(), any()) }
    }

    /**
     * Verifies that when the OAuth2 token is expired, the `auth_req_id` is blank, but a login
     * hint is stored, [TokenAuthenticator.authenticate] calls bc-authorize to get a new
     * `auth_req_id` and then exchanges it for an OAuth2 token.
     */
    @Test
    fun `authenticate calls bc-authorize then refreshes OAuth2 when authReqId is blank but loginHint is present`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns ""
        every { mockStorage.getLoginHint() } returns "MSISDN:256770000000"
        stubAccessTokenSuccess()
        stubBcAuthorizeSuccess("bc-req-id-123")
        stubOauthTokenSuccess("new-oauth-token")

        val result = authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl()))

        assertNotNull(result)
        verify(exactly = 1) { mockStorage.saveAccessToken(any()) }
        verify(exactly = 1) { mockStorage.saveBackChannelAuthorizationRequestId("bc-req-id-123", 300) }
        verify(exactly = 1) {
            mockStorage.saveOauthAccessToken(
                withArg { token -> assertEquals("new-oauth-token", token.accessToken) }
            )
        }
    }

    /**
     * Verifies that when the OAuth2 token is expired and both the `auth_req_id` and the login
     * hint are blank, [TokenAuthenticator.authenticate] skips the OAuth2 refresh entirely and
     * still returns the original request for retry with the refreshed Bearer token.
     */
    @Test
    fun `authenticate skips OAuth2 refresh when authReqId and loginHint are both blank`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns ""
        every { mockStorage.getLoginHint() } returns ""
        stubAccessTokenSuccess()

        val result = authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl()))

        assertNotNull("Should return the original request even without OAuth2 refresh", result)
        verify(exactly = 0) { mockStorage.saveOauthAccessToken(any()) }
    }

    /**
     * Verifies that a bc-authorize failure is non-fatal: the original request is still returned
     * for retry (with the refreshed Bearer token) even when bc-authorize returns non-2xx.
     */
    @Test
    fun `authenticate returns original request when bc-authorize fails`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns ""
        every { mockStorage.getLoginHint() } returns "MSISDN:256770000000"
        stubAccessTokenSuccess()
        coEvery { mockAuthService.bcAuthorize(any(), any(), any(), any(), any(), any(), any()) } returns
            RetrofitResponse.error(500, "".toResponseBody(null))

        val result = authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl()))

        assertNotNull("Should still return request for retry even if bc-authorize fails", result)
        verify(exactly = 1) { mockStorage.saveAccessToken(any()) }
        verify(exactly = 0) { mockStorage.saveOauthAccessToken(any()) }
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] skips the OAuth2 refresh entirely when the
     * stored OAuth2 token has not expired, avoiding an unnecessary network call.
     */
    @Test
    fun `authenticate skips OAuth2 refresh when OAuth2 token is still valid`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        every { mockStorage.getOauthAccessToken() } returns "valid-oauth-token"
        stubAccessTokenSuccess()

        authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl()))

        verify(exactly = 0) { mockStorage.saveOauthAccessToken(any()) }
    }

    /**
     * Verifies that a failing OAuth2 refresh is non-fatal: the original request is still returned
     * for retry (with the refreshed Bearer token) even when the OAuth2 endpoint returns non-2xx.
     */
    @Test
    fun `authenticate returns original request even when OAuth2 refresh fails`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns "stored-auth-req-id"
        stubAccessTokenSuccess()
        coEvery { mockAuthService.getOauth2AccessToken(any(), any(), any(), any(), any()) } returns
            RetrofitResponse.error(500, "".toResponseBody(null))

        val result = authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl()))

        assertNotNull("Should still return request for retry even if OAuth2 refresh fails", result)
        verify(exactly = 1) { mockStorage.saveAccessToken(any()) }
        verify(exactly = 0) { mockStorage.saveOauthAccessToken(any()) }
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` and does not save any token
     * when the Bearer token endpoint responds with a non-2xx status code.
     */
    @Test
    fun `authenticate returns null when token endpoint returns non-2xx`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        coEvery { mockAuthService.getAccessToken(any(), any()) } returns
            RetrofitResponse.error(500, "".toResponseBody(null))

        val result = authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl()))

        assertNull("Should give up when token refresh fails", result)
        verify(exactly = 0) { mockStorage.saveAccessToken(any()) }
    }

    /**
     * Verifies that a request with zero prior responses (retryCount == 0) is not blocked by the
     * retry-limit guard, and the token refresh proceeds normally.
     */
    @Test
    fun `authenticate succeeds on first attempt when no prior responses exist`() {
        every { mockStorage.getApiKey() } returns "test-api-key"
        every { mockStorage.getOauthAccessToken() } returns "valid-oauth-token"
        stubAccessTokenSuccess("tok")

        assertNotNull(authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl())))
    }

    /**
     * Verifies that a request chain with two prior responses (retryCount == 2) is still blocked
     * by the retry-limit guard — the guard triggers at count >= 1.
     */
    @Test
    fun `authenticate is blocked when two prior responses exist`() {
        val prior1 = buildUnauthorizedResponse(collectionUrl())
        val prior2 = buildUnauthorizedResponse(collectionUrl(), priorResponse = prior1)
        val response = buildUnauthorizedResponse(collectionUrl(), priorResponse = prior2)

        assertNull(authenticator.authenticate(null, response))
    }
}
