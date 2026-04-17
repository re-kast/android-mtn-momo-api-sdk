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
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.DisbursementsService
import io.rekast.sdk.utils.ApiConfig
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
     * instance configured with `ignoreUnknownKeys = true` so that forward-compatible API
     * responses do not throw on unrecognised fields.
     */
    @Test
    fun `provideJson returns Json with ignoreUnknownKeys true`() {
        val json = NetworkModule.provideJson()
        assertTrue(json.configuration.ignoreUnknownKeys)
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

    // Note: getCommonService cannot be tested here because CommonService is the root of a
    // sealed-interface hierarchy and the JVM's Proxy.validateProxyInterfaces rejects it
    // (isSealed() == true). The sub-interfaces (CollectionService, DisbursementsService) work
    // because they are not themselves the root of a PermittedSubclasses declaration.

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
