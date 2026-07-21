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
package io.rekast.sdk.network.interceptor.headers

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.utils.Constants
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [CallbackUrlInterceptor].
 *
 * Verifies that the `X-Callback-Url` header is attached only to transaction-initiation POST
 * requests when a callback URL is configured, and omitted for a blank URL, non-POST methods,
 * status/cancel paths, and non-initiation POSTs (delivery notifications, auth/token endpoints).
 */
class CallbackUrlInterceptorTest {

    private val mockChain = mockk<Interceptor.Chain>()
    private val body = "{}".toRequestBody("application/json".toMediaType())

    private fun mockResponse(request: Request) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .build()

    private fun provider(callbackUrl: String): CredentialProvider = mockk {
        every { getCallbackUrl(any()) } returns callbackUrl
    }

    private fun capturePost(url: String, callbackUrl: String): Request {
        val interceptor = CallbackUrlInterceptor(provider(callbackUrl))
        val request = Request.Builder().url(url).post(body).build()
        val captured = slot<Request>()
        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(captured)) } returns mockResponse(request)
        interceptor.intercept(mockChain)
        return captured.captured
    }

    /** Attaches the header on a POST to an initiation endpoint when a callback URL is configured. */
    @Test
    fun `adds callback header for initiation POST when url configured`() {
        val captured = capturePost("https://example.com/collection/v1_0/requesttopay", "https://cb.example.com/hook")
        assertEquals("https://cb.example.com/hook", captured.header(Constants.Headers.X_CALLBACK_URL))
    }

    /** Applies to the other initiation endpoints too (e.g. disbursement deposit). */
    @Test
    fun `adds callback header for deposit initiation POST`() {
        val captured = capturePost("https://example.com/disbursement/v1_0/deposit", "https://cb.example.com/hook")
        assertEquals("https://cb.example.com/hook", captured.header(Constants.Headers.X_CALLBACK_URL))
    }

    /** Omits the header when the configured callback URL is blank (opt-in). */
    @Test
    fun `omits callback header when url is blank`() {
        val captured = capturePost("https://example.com/collection/v1_0/requesttopay", "")
        assertNull(captured.header(Constants.Headers.X_CALLBACK_URL))
    }

    /** Omits the header on GET status requests even for an initiation-related path. */
    @Test
    fun `omits callback header on GET status request`() {
        val interceptor = CallbackUrlInterceptor(provider("https://cb.example.com/hook"))
        val request = Request.Builder().url("https://example.com/collection/v1_0/requesttopay/ref-123").build()
        val captured = slot<Request>()
        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(captured)) } returns mockResponse(request)

        interceptor.intercept(mockChain)

        assertNull(captured.captured.header(Constants.Headers.X_CALLBACK_URL))
    }

    /** Omits the header on a delivery-notification POST (not a transaction-initiation endpoint). */
    @Test
    fun `omits callback header on delivery notification POST`() {
        val captured = capturePost(
            "https://example.com/collection/v1_0/requesttopay/ref-123/deliverynotification",
            "https://cb.example.com/hook"
        )
        assertNull(captured.header(Constants.Headers.X_CALLBACK_URL))
    }

    /** Omits the header on an auth/token POST. */
    @Test
    fun `omits callback header on token POST`() {
        val captured = capturePost("https://example.com/collection/token/", "https://cb.example.com/hook")
        assertNull(captured.header(Constants.Headers.X_CALLBACK_URL))
    }

    /** Returns the response produced by the chain, proceeding exactly once. */
    @Test
    fun `returns response from chain proceeding once`() {
        val interceptor = CallbackUrlInterceptor(provider("https://cb.example.com/hook"))
        val request = Request.Builder().url("https://example.com/collection/v1_0/requesttopay").post(body).build()
        val expected = mockResponse(request)
        every { mockChain.request() } returns request
        every { mockChain.proceed(any()) } returns expected

        val result = interceptor.intercept(mockChain)

        assertEquals(expected, result)
        verify(exactly = 1) { mockChain.proceed(any()) }
    }
}
