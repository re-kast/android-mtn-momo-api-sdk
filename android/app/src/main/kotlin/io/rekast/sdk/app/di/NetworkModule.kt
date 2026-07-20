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

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.rekast.sdk.app.network.TokenAuthenticator
import io.rekast.sdk.network.interceptor.UnsafeOkHttpClient
import io.rekast.sdk.network.interceptor.auth.AccessTokenInterceptor
import io.rekast.sdk.network.interceptor.auth.BasicAuthenticationInterceptor
import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.CommonService
import io.rekast.sdk.network.service.products.DisbursementsService
import io.rekast.sdk.network.service.products.RemittanceService
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.utils.ApiConfig
import io.rekast.sdk.utils.Settings
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import io.rekast.sdk.app.network.CredentialProvider as AppCredentialProvider

/**
 * Provides network-related dependencies using Dagger Hilt.
 *
 * Lives in the :app module (not :sample) because com.android.kotlin.multiplatform.library's
 * compile JAR does not include KSP-generated Java factory classes.
 *
 * Credential storage is handled by [CredentialStorage] (EncryptedSharedPreferences).
 * Expired tokens are refreshed automatically by [TokenAuthenticator] on every 401 response.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Provides the app's [CredentialProvider] that the SDK's interceptors call on every request.
     * Reads credentials from [CredentialStorage] at request time — the SDK never stores credentials.
     */
    @Provides
    @Singleton
    fun provideMomoCredentialProvider(
        storage: CredentialStorage,
        sampleConfig: SampleConfig
    ): CredentialProvider = AppCredentialProvider(storage, sampleConfig)

    /**
     * Provides a dedicated [AuthenticationService] backed by a minimal [OkHttpClient] that only
     * attaches Basic Auth. Used exclusively by [TokenAuthenticator] to avoid a circular dependency
     * with the main client (which has the authenticator wired in).
     *
     * The anonymous [CredentialProvider] always returns the raw API key and never returns a Bearer
     * token, ensuring the Basic Auth header is always attached on token-refresh requests even when
     * an (expired) token is still present in [CredentialStorage].
     */
    @Provides
    @Singleton
    @Named("tokenRefresh")
    fun provideTokenRefreshAuthenticationService(
        config: ApiConfig,
        storage: CredentialStorage,
        json: Json
    ): AuthenticationService {
        val credentialProvider: CredentialProvider = TokenRefreshCredentialProvider(config, storage)
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(BasicAuthenticationInterceptor(credentialProvider))
                .build()
        return Retrofit
            .Builder()
            .baseUrl(config.baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(AuthenticationService::class.java)
    }

    /**
     * Provides the [TokenAuthenticator] that refreshes the Bearer access token whenever
     * a 401 is received from a protected endpoint.
     */
    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        storage: CredentialStorage,
        @Named("tokenRefresh") authService: AuthenticationService,
        config: ApiConfig
    ): TokenAuthenticator = TokenAuthenticator(storage, authService, config)

    /** Provides the HTTP logging interceptor configured to log full request and response bodies. */
    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    /** Provides the [Json] instance used by the Retrofit converter factory; unknown keys are ignored. */
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    /**
     * Provides the singleton [OkHttpClient] wired with:
     * - Logging interceptor
     * - [BasicAuthenticationInterceptor] (adds Basic Auth when no access token is present)
     * - [AccessTokenInterceptor] (adds Bearer token when one is available)
     * - [TokenAuthenticator] (refreshes the token automatically on 401)
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        credentialProvider: CredentialProvider,
        tokenAuthenticator: TokenAuthenticator,
        config: ApiConfig
    ): OkHttpClient {
        val builder =
            if (config.baseUrl.startsWith("https")) {
                OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            } else {
                UnsafeOkHttpClient().unsafeOkHttpClient.addInterceptor(httpLoggingInterceptor)
            }

        builder.addInterceptor(BasicAuthenticationInterceptor(credentialProvider))
        builder.addInterceptor(AccessTokenInterceptor(credentialProvider))
        builder.authenticator { route, response -> tokenAuthenticator.authenticate(route, response) }

        val settings = Settings()
        return builder
            .connectTimeout(settings.connectTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(settings.writeTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(settings.readTimeout, TimeUnit.MILLISECONDS)
            .build()
    }

    /** Provides the shared [Retrofit] instance used by all product-specific service factories. */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        config: ApiConfig
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(config.baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()

    /** Provides the [AuthenticationService] Retrofit service for token and API-user endpoints. */
    @Provides
    @Singleton
    fun getAuthentication(retrofit: Retrofit): AuthenticationService = retrofit.create(AuthenticationService::class.java)

    /** Provides the [CollectionService] Retrofit service for Collection product endpoints. */
    @Provides
    @Singleton
    fun getCollection(retrofit: Retrofit): CollectionService = retrofit.create(CollectionService::class.java)

    /** Provides the [DisbursementsService] Retrofit service for Disbursement product endpoints. */
    @Provides
    @Singleton
    fun getDisbursement(retrofit: Retrofit): DisbursementsService = retrofit.create(DisbursementsService::class.java)

    /** Provides the [CommonService] Retrofit service for cross-product endpoints (balance, account status, etc.). */
    @Provides
    @Singleton
    fun getCommonService(retrofit: Retrofit): CommonService = retrofit.create(CommonService::class.java)

    /** Provides the [RemittanceService] Retrofit service for Remittance product endpoints (cash transfer V2). */
    @Provides
    @Singleton
    fun getRemittance(retrofit: Retrofit): RemittanceService = retrofit.create(RemittanceService::class.java)
}
