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
package io.rekast.sdk.network.interceptor.auth

import android.util.Base64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials
import io.rekast.sdk.utils.MomoConstants
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

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

    @Test
    fun `adds Basic authorization header when both userId and apiKey are non-empty`() {
        val credentials = BasicAuthCredentials("user-123", "key-abc")
        val interceptor = BasicAuthenticationInterceptor(credentials)
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        val expectedEncoded = java.util.Base64.getEncoder().encodeToString("user-123:key-abc".toByteArray())
        assertEquals(
            "${MomoConstants.TokenTypes.BASIC} $expectedEncoded",
            capturedRequest.captured.header(MomoConstants.Headers.AUTHORIZATION)
        )
    }

    @Test
    fun `does not add authorization header when userId is empty`() {
        val credentials = BasicAuthCredentials("", "key-abc")
        val interceptor = BasicAuthenticationInterceptor(credentials)
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertNull(capturedRequest.captured.header(MomoConstants.Headers.AUTHORIZATION))
    }

    @Test
    fun `does not add authorization header when apiKey is empty`() {
        val credentials = BasicAuthCredentials("user-123", "")
        val interceptor = BasicAuthenticationInterceptor(credentials)
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertNull(capturedRequest.captured.header(MomoConstants.Headers.AUTHORIZATION))
    }

    @Test
    fun `does not add authorization header when both are empty`() {
        val credentials = BasicAuthCredentials("", "")
        val interceptor = BasicAuthenticationInterceptor(credentials)
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertNull(capturedRequest.captured.header(MomoConstants.Headers.AUTHORIZATION))
    }

    @Test
    fun `returns response from chain`() {
        val credentials = BasicAuthCredentials("user-123", "key-abc")
        val interceptor = BasicAuthenticationInterceptor(credentials)
        val request = Request.Builder().url("https://example.com").build()
        val expected = mockResponse(request)

        every { mockChain.request() } returns request
        every { mockChain.proceed(any()) } returns expected

        val result = interceptor.intercept(mockChain)

        assertEquals(expected, result)
        verify(exactly = 1) { mockChain.proceed(any()) }
    }
}
