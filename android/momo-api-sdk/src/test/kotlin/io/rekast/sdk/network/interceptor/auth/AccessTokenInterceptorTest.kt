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
package io.rekast.sdk.network.interceptor.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.utils.Constants
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [AccessTokenInterceptor].
 *
 * Verifies that the interceptor attaches a `Bearer` Authorization header
 * when a non-empty access token is provided, omits the header when the
 * token is empty, and always delegates the request to the underlying
 * [Interceptor.Chain] exactly once regardless of token state.
 */
class AccessTokenInterceptorTest {

    private val mockChain = mockk<Interceptor.Chain>()

    private fun mockResponse(request: Request) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .build()

    private fun provider(token: String): CredentialProvider = mockk {
        every { getApiUserId() } returns ""
        every { getApiKey() } returns ""
        every { getAccessToken() } returns token
    }

    /** Verifies the Authorization header value is `Bearer <token>` for a valid token. */
    @Test
    fun `adds bearer authorization header when token is not empty`() {
        val interceptor = AccessTokenInterceptor(provider("test-token-123"))
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertEquals(
            "${Constants.TokenTypes.BEARER} test-token-123",
            capturedRequest.captured.header(Constants.Headers.AUTHORIZATION)
        )
    }

    /** Verifies the Authorization header is absent when the token is an empty string. */
    @Test
    fun `does not add authorization header when token is empty`() {
        val interceptor = AccessTokenInterceptor(provider(""))
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertNull(capturedRequest.captured.header(Constants.Headers.AUTHORIZATION))
    }

    /** Verifies the interceptor returns the response produced by the chain unchanged. */
    @Test
    fun `returns response from chain`() {
        val interceptor = AccessTokenInterceptor(provider("some-token"))
        val request = Request.Builder().url("https://example.com").build()
        val expected = mockResponse(request)

        every { mockChain.request() } returns request
        every { mockChain.proceed(any()) } returns expected

        val result = interceptor.intercept(mockChain)

        assertEquals(expected, result)
        verify(exactly = 1) { mockChain.proceed(any()) }
    }

    /** Verifies the chain is called exactly once for both a valid and an empty token. */
    @Test
    fun `proceeds once regardless of token state`() {
        listOf("valid-token", "").forEach { token ->
            val chain = mockk<Interceptor.Chain>()
            val interceptor = AccessTokenInterceptor(provider(token))
            val request = Request.Builder().url("https://example.com").build()

            every { chain.request() } returns request
            every { chain.proceed(any()) } returns mockResponse(request)

            interceptor.intercept(chain)

            verify(exactly = 1) { chain.proceed(any()) }
        }
    }
}
