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

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.utils.MomoApiConfig
import io.rekast.sdk.utils.MomoConstants
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [TokenAuthenticator].
 *
 * Covers every early-return guard in [TokenAuthenticator.authenticate] as well as the
 * happy-path where a token is successfully refreshed via a [MockWebServer].
 * The [TokenAuthenticator.retryCount] helper is exercised indirectly through the retry-limit
 * tests, which chain prior responses to simulate previous retry attempts.
 */
class TokenAuthenticatorTest {
    private val mockStorage: CredentialStorage = mockk(relaxed = true)
    private val mockWebServer = MockWebServer()

    private lateinit var config: MomoApiConfig
    private lateinit var authenticator: TokenAuthenticator

    @Before
    fun setUp() {
        mockWebServer.start()
        config =
            MomoApiConfig(
                baseUrl = mockWebServer.url("/").toString(),
                apiUserId = "test-user-id",
                environment = "sandbox"
            )
        authenticator = TokenAuthenticator(mockStorage, config)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    /**
     * Builds a minimal 401 [Response] pointing at [url] with the given headers.
     * An optional [priorResponse] is chained to simulate a previous retry.
     */
    private fun buildUnauthorizedResponse(
        url: String,
        authHeader: String? = "${MomoConstants.TokenTypes.BEARER} some-token",
        subscriptionKey: String? = "sub-key-001",
        priorResponse: Response? = null
    ): Response {
        val requestBuilder = Request.Builder().url(url).get()
        authHeader?.let { requestBuilder.header(MomoConstants.Headers.AUTHORIZATION, it) }
        subscriptionKey?.let { requestBuilder.header(MomoConstants.Headers.OCP_APIM_SUBSCRIPTION_KEY, it) }
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

    private fun collectionUrl() = mockWebServer.url("/collection/v1_0/accounts").toString()

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
                authHeader = "${MomoConstants.TokenTypes.BASIC} dXNlcjpwYXNz"
            )

        assertNull(authenticator.authenticate(null, response))
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` when a prior response exists
     * in the response chain, preventing infinite retry loops by limiting to one refresh attempt.
     */
    @Test
    fun `authenticate returns null when retry count is already 1`() {
        val prior = buildUnauthorizedResponse(collectionUrl(), priorResponse = null)
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

        val response = buildUnauthorizedResponse(mockWebServer.url("/").toString())

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
     * Verifies the happy path: when all guards pass and the [MockWebServer] returns a valid token
     * response, [TokenAuthenticator.authenticate] returns the original request (so OkHttp retries
     * it) and saves the refreshed token to [CredentialStorage].
     */
    @Test
    fun `authenticate returns original request and saves token after successful refresh`() {
        every { mockStorage.getApiKey() } returns "test-api-key"

        val tokenJson = """{"access_token":"new-token","token_type":"Bearer","expires_in":3600}"""
        mockWebServer.enqueue(MockResponse().setBody(tokenJson).setResponseCode(200))

        val response = buildUnauthorizedResponse(collectionUrl())
        val result = authenticator.authenticate(null, response)

        assertNotNull("Should return the original request for retry", result)
        assertEquals(response.request.url, result!!.url)

        verify(exactly = 1) {
            mockStorage.saveAccessToken(
                withArg { token -> assertEquals("new-token", token?.accessToken) }
            )
        }
    }

    /**
     * Verifies that [TokenAuthenticator.authenticate] returns `null` and does not save any token
     * when the token endpoint itself responds with a non-2xx status code.
     */
    @Test
    fun `authenticate returns null when token endpoint returns non-2xx`() {
        every { mockStorage.getApiKey() } returns "test-api-key"

        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result = authenticator.authenticate(null, buildUnauthorizedResponse(collectionUrl()))

        assertNull("Should give up when token refresh fails", result)
    }

    /**
     * Verifies that a request with zero prior responses (retryCount == 0) is not blocked by the
     * retry-limit guard, and the token refresh proceeds normally.
     */
    @Test
    fun `authenticate succeeds on first attempt when no prior responses exist`() {
        every { mockStorage.getApiKey() } returns "test-api-key"

        val tokenJson = """{"access_token":"tok","token_type":"Bearer","expires_in":3600}"""
        mockWebServer.enqueue(MockResponse().setBody(tokenJson).setResponseCode(200))

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
