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
package io.rekast.sdk.app.di

import io.mockk.mockk
import io.rekast.sdk.app.network.TokenAuthenticator
import io.rekast.sdk.model.PaymentStatus
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.CommonService
import io.rekast.sdk.network.service.products.DisbursementsService
import io.rekast.sdk.network.service.products.RemittanceService
import io.rekast.sdk.utils.ApiConfig
import io.rekast.sdk.utils.StatusTypes
import kotlinx.serialization.decodeFromString
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit

/**
 * Unit tests for [NetworkModule].
 *
 * Because [NetworkModule] is a Dagger [dagger.Module] object, its `@Provides` methods are
 * plain functions that can be called directly in tests — no Hilt test component is needed.
 *
 * Tests verify:
 * - [NetworkModule.provideJson] creates a [kotlinx.serialization.json.Json] instance with
 *   `ignoreUnknownKeys = true`.
 * - [NetworkModule.providesHttpLoggingInterceptor] sets `BODY`-level logging.
 * - [NetworkModule.provideOkHttpClient] returns a non-null client for both `https` and `http` URLs.
 * - [NetworkModule.provideRetrofit] returns a [Retrofit] configured with the expected base URL.
 */
class NetworkModuleTest {
    private val httpsConfig =
        ApiConfig(
            baseUrl = "https://sandbox.momodeveloper.mtn.com/",
            apiUserId = "user-id",
            environment = "sandbox"
        )

    private val httpConfig =
        ApiConfig(
            baseUrl = "http://10.0.2.2:8080/",
            apiUserId = "user-id",
            environment = "sandbox"
        )

    /**
     * Verifies that [NetworkModule.provideJson] returns a [kotlinx.serialization.json.Json]
     * instance configured with `ignoreUnknownKeys = true` (forward-compatible with unrecognised
     * fields) and `coerceInputValues = true` (resilient to unrecognised enum values).
     */
    @Test
    fun `provideJson returns Json with ignoreUnknownKeys and coerceInputValues true`() {
        val json = NetworkModule.provideJson()
        assertTrue(json.configuration.ignoreUnknownKeys)
        assertTrue(json.configuration.coerceInputValues)
    }

    /**
     * Verifies the `coerceInputValues` hardening end-to-end: a payload carrying an enum value the SDK
     * does not model (an unknown `reason`) coerces that field to its `null` default instead of throwing,
     * while the known fields still deserialize.
     */
    @Test
    fun `provideJson coerces an unknown enum value to null`() {
        val json = NetworkModule.provideJson()
        val raw = """{ "referenceId": "ref-1", "status": "SUCCESSFUL", "reason": "SOME_BRAND_NEW_REASON" }"""

        val result = json.decodeFromString<PaymentStatus>(raw)

        assertEquals("ref-1", result.referenceId)
        assertEquals(StatusTypes.SUCCESSFUL, result.status)
        assertNull(result.reason)
    }

    /**
     * Verifies that [NetworkModule.providesHttpLoggingInterceptor] sets the logging level to
     * [HttpLoggingInterceptor.Level.BODY] so that full request and response payloads are logged.
     */
    @Test
    fun `providesHttpLoggingInterceptor sets BODY level`() {
        val interceptor = NetworkModule.providesHttpLoggingInterceptor()
        assertEquals(HttpLoggingInterceptor.Level.BODY, interceptor.level)
    }

    /**
     * Verifies that [NetworkModule.provideOkHttpClient] returns a non-null [okhttp3.OkHttpClient]
     * when the base URL uses the `https` scheme (standard TLS client path).
     */
    @Test
    fun `provideOkHttpClient returns non-null client for https base URL`() {
        val client =
            NetworkModule.provideOkHttpClient(
                httpLoggingInterceptor = NetworkModule.providesHttpLoggingInterceptor(),
                credentialProvider = mockk(relaxed = true),
                tokenAuthenticator = TokenAuthenticator(mockk(relaxed = true), mockk(relaxed = true), httpsConfig),
                config = httpsConfig
            )
        assertNotNull(client)
    }

    /**
     * Verifies that [NetworkModule.provideOkHttpClient] returns a non-null [okhttp3.OkHttpClient]
     * when the base URL uses the `http` scheme (unsafe client path used for local/emulator targets).
     */
    @Test
    fun `provideOkHttpClient returns non-null client for http base URL`() {
        val client =
            NetworkModule.provideOkHttpClient(
                httpLoggingInterceptor = NetworkModule.providesHttpLoggingInterceptor(),
                credentialProvider = mockk(relaxed = true),
                tokenAuthenticator = TokenAuthenticator(mockk(relaxed = true), mockk(relaxed = true), httpConfig),
                config = httpConfig
            )
        assertNotNull(client)
    }

    /**
     * Verifies that the [Retrofit] instance returned by [NetworkModule.provideRetrofit] has its
     * base URL set to the value from [ApiConfig.baseUrl].
     */
    @Test
    fun `provideRetrofit sets base URL from config`() {
        val json = NetworkModule.provideJson()
        val client =
            NetworkModule.provideOkHttpClient(
                httpLoggingInterceptor = NetworkModule.providesHttpLoggingInterceptor(),
                credentialProvider = mockk(relaxed = true),
                tokenAuthenticator = TokenAuthenticator(mockk(relaxed = true), mockk(relaxed = true), httpsConfig),
                config = httpsConfig
            )
        val retrofit: Retrofit = NetworkModule.provideRetrofit(client, json, httpsConfig)

        assertEquals(httpsConfig.baseUrl, retrofit.baseUrl().toString())
    }

    /** Verifies that [NetworkModule.provideRetrofit] returns a non-null [Retrofit] instance. */
    @Test
    fun `provideRetrofit returns non-null Retrofit instance`() {
        val json = NetworkModule.provideJson()
        val client =
            NetworkModule.provideOkHttpClient(
                httpLoggingInterceptor = NetworkModule.providesHttpLoggingInterceptor(),
                credentialProvider = mockk(relaxed = true),
                tokenAuthenticator = TokenAuthenticator(mockk(relaxed = true), mockk(relaxed = true), httpsConfig),
                config = httpsConfig
            )
        assertNotNull(NetworkModule.provideRetrofit(client, json, httpsConfig))
    }

    /**
     * Verifies that [NetworkModule.getAuthentication] returns a non-null [AuthenticationService]
     * proxy for the given [Retrofit] instance.
     */
    @Test
    fun `getAuthentication returns non-null AuthenticationService`() {
        val retrofit = buildRetrofit(httpsConfig)
        assertNotNull(NetworkModule.getAuthentication(retrofit))
    }

    /**
     * Verifies that [NetworkModule.getCollection] returns a non-null [CollectionService]
     * proxy for the given [Retrofit] instance.
     */
    @Test
    fun `getCollection returns non-null CollectionService`() {
        val retrofit = buildRetrofit(httpsConfig)
        assertNotNull(NetworkModule.getCollection(retrofit))
    }

    /**
     * Verifies that [NetworkModule.getDisbursement] returns a non-null [DisbursementsService]
     * proxy for the given [Retrofit] instance.
     */
    @Test
    fun `getDisbursement returns non-null DisbursementsService`() {
        val retrofit = buildRetrofit(httpsConfig)
        assertNotNull(NetworkModule.getDisbursement(retrofit))
    }

    /**
     * Verifies that [NetworkModule.getRemittance] returns a non-null [RemittanceService]
     * proxy for the given [Retrofit] instance.
     */
    @Test
    fun `getRemittance returns non-null RemittanceService`() {
        val retrofit = buildRetrofit(httpsConfig)
        assertNotNull(NetworkModule.getRemittance(retrofit))
    }

    /**
     * [NetworkModule.getCommonService] cannot return a proxy because [CommonService] is the root of
     * a sealed-interface hierarchy and the JVM's `Proxy.validateProxyInterfaces` rejects it
     * (`isSealed() == true`). Calling it still executes the factory line and must raise
     * [IllegalArgumentException] rather than silently return.
     */
    @Test
    fun `getCommonService throws for the sealed CommonService interface`() {
        val retrofit = buildRetrofit(httpsConfig)
        assertThrows(IllegalArgumentException::class.java) {
            NetworkModule.getCommonService(retrofit)
        }
    }

    /**
     * Verifies that [NetworkModule.provideMomoCredentialProvider] returns a non-null
     * [io.rekast.sdk.network.interfaces.CredentialProvider] built from the storage and config.
     */
    @Test
    fun `provideMomoCredentialProvider returns non-null provider`() {
        val provider = NetworkModule.provideMomoCredentialProvider(mockk(relaxed = true), mockk(relaxed = true))
        assertNotNull(provider)
    }

    /**
     * Verifies that [NetworkModule.provideTokenRefreshAuthenticationService] returns a non-null,
     * Basic-Auth-only [AuthenticationService] for the token-refresh path.
     */
    @Test
    fun `provideTokenRefreshAuthenticationService returns non-null service`() {
        val service =
            NetworkModule.provideTokenRefreshAuthenticationService(
                config = httpsConfig,
                storage = mockk(relaxed = true),
                json = NetworkModule.provideJson()
            )
        assertNotNull(service)
    }

    /**
     * Verifies that [NetworkModule.provideTokenAuthenticator] returns a non-null
     * [TokenAuthenticator] wired with the supplied dependencies.
     */
    @Test
    fun `provideTokenAuthenticator returns non-null authenticator`() {
        val authenticator =
            NetworkModule.provideTokenAuthenticator(
                storage = mockk(relaxed = true),
                authService = mockk(relaxed = true),
                config = httpsConfig
            )
        assertNotNull(authenticator)
    }

    /** Builds a [Retrofit] instance using [NetworkModule] helpers for the given [config]. */
    private fun buildRetrofit(config: ApiConfig): Retrofit {
        val json = NetworkModule.provideJson()
        val client =
            NetworkModule.provideOkHttpClient(
                httpLoggingInterceptor = NetworkModule.providesHttpLoggingInterceptor(),
                credentialProvider = mockk(relaxed = true),
                tokenAuthenticator = TokenAuthenticator(mockk(relaxed = true), mockk(relaxed = true), config),
                config = config
            )
        return NetworkModule.provideRetrofit(client, json, config)
    }
}
