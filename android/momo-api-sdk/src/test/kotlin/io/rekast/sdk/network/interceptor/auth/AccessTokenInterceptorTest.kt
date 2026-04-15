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

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials
import io.rekast.sdk.utils.MomoConstants
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AccessTokenInterceptorTest {

    private val mockChain = mockk<Interceptor.Chain>()

    private fun mockResponse(request: Request) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .build()

    @Test
    fun `adds bearer authorization header when token is not empty`() {
        val credentials = AccessTokenCredentials("test-token-123")
        val interceptor = AccessTokenInterceptor(credentials)
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertEquals(
            "${MomoConstants.TokenTypes.BEARER} test-token-123",
            capturedRequest.captured.header(MomoConstants.Headers.AUTHORIZATION)
        )
    }

    @Test
    fun `does not add authorization header when token is empty`() {
        val credentials = AccessTokenCredentials("")
        val interceptor = AccessTokenInterceptor(credentials)
        val request = Request.Builder().url("https://example.com").build()
        val capturedRequest = slot<Request>()

        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(capturedRequest)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertNull(capturedRequest.captured.header(MomoConstants.Headers.AUTHORIZATION))
    }

    @Test
    fun `returns response from chain`() {
        val credentials = AccessTokenCredentials("some-token")
        val interceptor = AccessTokenInterceptor(credentials)
        val request = Request.Builder().url("https://example.com").build()
        val expected = mockResponse(request)

        every { mockChain.request() } returns request
        every { mockChain.proceed(any()) } returns expected

        val result = interceptor.intercept(mockChain)

        assertEquals(expected, result)
        verify(exactly = 1) { mockChain.proceed(any()) }
    }

    @Test
    fun `proceeds once regardless of token state`() {
        listOf("valid-token", "").forEach { token ->
            val chain = mockk<Interceptor.Chain>()
            val credentials = AccessTokenCredentials(token)
            val interceptor = AccessTokenInterceptor(credentials)
            val request = Request.Builder().url("https://example.com").build()

            every { chain.request() } returns request
            every { chain.proceed(any()) } returns mockResponse(request)

            interceptor.intercept(chain)

            verify(exactly = 1) { chain.proceed(any()) }
        }
    }
}
