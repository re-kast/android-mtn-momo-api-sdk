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

import android.util.Base64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.utils.Constants
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [BasicAuthenticationInterceptor].
 *
 * Verifies that the interceptor Base64-encodes `userId:apiKey` and attaches
 * a `Basic` Authorization header when both credentials are non-empty, omits
 * the header when either credential is blank, and always forwards the request
 * to the chain exactly once.
 *
 * [android.util.Base64] is statically mocked via MockK so that the tests run
 * on the JVM without requiring the Android framework.
 */
class BasicAuthenticationInterceptorTest {

    private val mockChain = mockk<Interceptor.Chain>()

    @Before
    fun setUp() {
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), any()) } answers {
            java.util.Base64.getEncoder().encodeToString(firstArg<ByteArray>())
        }
    }

    @After
    fun tearDown() {
        unmockkStatic(Base64::class)
    }

    private fun mockResponse(request: Request) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .build()

    private fun provider(userId: String, apiKey: String): CredentialProvider = mockk {
        every { getApiUserId() } returns userId
        every { getApiKey() } returns apiKey
        every { getAccessToken() } returns ""
    }

    /** Verifies the Authorization header is `Basic <base64(userId:apiKey)>` when both fields are set. */
    @Test
    fun `adds Basic authorization header when both userId and apiKey are non-empty`() {
        val interceptor = BasicAuthenticationInterceptor(provider("user-123", "key-abc"))
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        val expectedEncoded = java.util.Base64.getEncoder().encodeToString("user-123:key-abc".toByteArray())
        assertEquals(
            "${Constants.TokenTypes.BASIC} $expectedEncoded",
            capturedRequest.captured.header(Constants.Headers.AUTHORIZATION)
        )
    }

    /** Verifies the Authorization header is omitted when userId is an empty string. */
    @Test
    fun `does not add authorization header when userId is empty`() {
        val interceptor = BasicAuthenticationInterceptor(provider("", "key-abc"))
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertNull(capturedRequest.captured.header(Constants.Headers.AUTHORIZATION))
    }

    /** Verifies the Authorization header is omitted when apiKey is an empty string. */
    @Test
    fun `does not add authorization header when apiKey is empty`() {
        val interceptor = BasicAuthenticationInterceptor(provider("user-123", ""))
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertNull(capturedRequest.captured.header(Constants.Headers.AUTHORIZATION))
    }

    /** Verifies the Authorization header is omitted when both userId and apiKey are empty. */
    @Test
    fun `does not add authorization header when both are empty`() {
        val interceptor = BasicAuthenticationInterceptor(provider("", ""))
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
        val interceptor = BasicAuthenticationInterceptor(provider("user-123", "key-abc"))
        val request = Request.Builder().url("https://example.com").build()
        val expected = mockResponse(request)

        every { mockChain.request() } returns request
        every { mockChain.proceed(any()) } returns expected

        val result = interceptor.intercept(mockChain)

        assertEquals(expected, result)
        verify(exactly = 1) { mockChain.proceed(any()) }
    }
}
