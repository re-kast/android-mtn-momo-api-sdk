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
import io.rekast.sdk.utils.Constants
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [EnvironmentInterceptor].
 *
 * Verifies that the `X-Target-Environment` header is attached to product and OAuth2 endpoints,
 * skipped for the auth-bootstrap endpoints (`apiuser`, plain `token`) and a blank environment,
 * and still applied to the OAuth2 token endpoint.
 */
class EnvironmentInterceptorTest {

    private val mockChain = mockk<Interceptor.Chain>()

    private fun mockResponse(request: Request) = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .build()

    private fun captureHeaderFor(url: String, environment: String = "sandbox"): String? {
        val interceptor = EnvironmentInterceptor(environment)
        val request = Request.Builder().url(url).build()
        val captured = slot<Request>()
        every { mockChain.request() } returns request
        every { mockChain.proceed(capture(captured)) } returns mockResponse(request)
        interceptor.intercept(mockChain)
        return captured.captured.header(Constants.Headers.X_TARGET_ENVIRONMENT)
    }

    /** Adds the header on a product endpoint. */
    @Test
    fun `adds environment header on product endpoint`() {
        assertEquals("sandbox", captureHeaderFor("https://example.com/collection/v1_0/requesttopay"))
    }

    /** Adds the header on the OAuth2 token endpoint (it contains an `oauth2` segment). */
    @Test
    fun `adds environment header on oauth2 token endpoint`() {
        assertEquals("sandbox", captureHeaderFor("https://example.com/collection/oauth2/token/"))
    }

    /** Adds the header on the bc-authorize endpoint. */
    @Test
    fun `adds environment header on bc-authorize endpoint`() {
        assertEquals("sandbox", captureHeaderFor("https://example.com/collection/v1_0/bc-authorize"))
    }

    /** Skips the plain access-token endpoint. */
    @Test
    fun `omits environment header on plain token endpoint`() {
        assertNull(captureHeaderFor("https://example.com/collection/token/"))
    }

    /** Skips the API-user provisioning and API-key endpoints (paths containing `apiuser`). */
    @Test
    fun `omits environment header on apiuser endpoints`() {
        assertNull(captureHeaderFor("https://example.com/v1_0/apiuser"))
        assertNull(captureHeaderFor("https://example.com/v1_0/apiuser/ref-1/apikey"))
    }

    /** Omits the header when the configured environment is blank. */
    @Test
    fun `omits environment header when environment is blank`() {
        assertNull(captureHeaderFor("https://example.com/collection/v1_0/requesttopay", environment = ""))
    }
}
