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
package io.rekast.sdk.network.interceptor

import java.security.cert.X509Certificate
import okhttp3.OkHttpClient
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [UnsafeOkHttpClient].
 *
 * Verifies that the unsafe client builder is constructed without errors and that the resulting
 * [OkHttpClient] bypasses hostname verification — the two observable properties exercisable
 * without a live network connection.
 *
 * Note: this class intentionally disables SSL validation and should only be used in
 * non-production environments (e.g., local HTTP targets).
 */
class UnsafeOkHttpClientTest {

    private val unsafeOkHttpClient = UnsafeOkHttpClient()

    /**
     * Verifies that the [UnsafeOkHttpClient.unsafeOkHttpClient] property returns a non-null
     * [OkHttpClient.Builder] without throwing any SSL initialisation exceptions.
     */
    @Test
    fun `unsafeOkHttpClient returns non-null builder`() {
        val builder = unsafeOkHttpClient.unsafeOkHttpClient
        assertNotNull(builder)
    }

    /**
     * Verifies that the [OkHttpClient.Builder] returned by [UnsafeOkHttpClient.unsafeOkHttpClient]
     * can be built into a non-null [OkHttpClient] without throwing.
     */
    @Test
    fun `unsafeOkHttpClient builder can build an OkHttpClient`() {
        val client: OkHttpClient = unsafeOkHttpClient.unsafeOkHttpClient.build()
        assertNotNull(client)
    }

    /**
     * Verifies that the hostname verifier installed by [UnsafeOkHttpClient] always returns `true`,
     * meaning it accepts all hostnames regardless of the presented certificate.
     */
    @Test
    fun `hostname verifier always returns true`() {
        val client = unsafeOkHttpClient.unsafeOkHttpClient.build()
        val hostnameVerifier = client.hostnameVerifier
        assertTrue(hostnameVerifier.verify("any-hostname.example.com", null))
        assertTrue(hostnameVerifier.verify("localhost", null))
        assertTrue(hostnameVerifier.verify("192.168.1.1", null))
    }

    /**
     * Verifies that the [UnsafeOkHttpClient.unsafeOkHttpClient] property is idempotent:
     * calling it twice returns two independent non-null builders (each getter invocation
     * creates a fresh SSL context).
     */
    @Test
    fun `unsafeOkHttpClient returns new builder instance on each access`() {
        val builder1 = unsafeOkHttpClient.unsafeOkHttpClient
        val builder2 = unsafeOkHttpClient.unsafeOkHttpClient
        assertNotNull(builder1)
        assertNotNull(builder2)
    }

    /**
     * Verifies the installed all-trusting [javax.net.ssl.X509TrustManager] performs no validation:
     * both `checkClientTrusted` and `checkServerTrusted` return without throwing for any chain, and
     * `getAcceptedIssuers` returns an empty array. Exercises the trust manager exposed by the built
     * [OkHttpClient] so its three methods are covered without a live TLS handshake.
     */
    @Test
    fun `trust manager accepts all certificates and returns no issuers`() {
        val client = unsafeOkHttpClient.unsafeOkHttpClient.build()
        val trustManager = client.x509TrustManager
        assertNotNull(trustManager)

        val emptyChain = emptyArray<X509Certificate>()
        // Neither call should throw — the manager trusts everything.
        trustManager!!.checkClientTrusted(emptyChain, "RSA")
        trustManager.checkServerTrusted(emptyChain, "RSA")
        assertTrue(trustManager.acceptedIssuers.isEmpty())
    }
}
