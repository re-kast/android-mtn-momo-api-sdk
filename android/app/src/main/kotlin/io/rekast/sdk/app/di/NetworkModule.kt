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
package io.rekast.sdk.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials
import io.rekast.sdk.network.interceptor.UnsafeOkHttpClient
import io.rekast.sdk.network.interceptor.auth.AccessTokenInterceptor
import io.rekast.sdk.network.interceptor.auth.BasicAuthenticationInterceptor
import io.rekast.sdk.network.interfaces.auth.AuthInterface
import io.rekast.sdk.network.interfaces.implementation.auth.AuthImplementation
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.CommonService
import io.rekast.sdk.network.service.products.DisbursementsService
import io.rekast.sdk.utils.MomoApiConfig
import io.rekast.sdk.utils.Settings
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Provides network-related dependencies using Dagger Hilt.
 *
 * Lives in the :app module (not :sample) because com.android.kotlin.multiplatform.library's
 * compile JAR does not include KSP-generated Java factory classes; moving @Module providers
 * here ensures hiltJavaCompileDebug can find them on the classpath.
 *
 * Supplies Retrofit, OkHttpClient, authentication credentials, and the various
 * API service instances consumed by the repository layer.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Provides an empty [BasicAuthCredentials] placeholder; credentials are set at runtime
     * via [io.rekast.sdk.network.interfaces.auth.AuthInterface].
     */
    @Provides
    @Singleton
    fun provideBasicAuthCredentials(): BasicAuthCredentials = BasicAuthCredentials("", "")

    /**
     * Provides an empty [AccessTokenCredentials] placeholder; the token is set at runtime
     * after a successful token-exchange call.
     */
    @Provides
    @Singleton
    fun provideAccessTokenCredentials(): AccessTokenCredentials = AccessTokenCredentials("")

    /**
     * Provides the [AuthInterface] implementation that manages Basic-Auth and access-token
     * credentials shared across the interceptors and repositories.
     */
    @Provides
    @Singleton
    fun provideApiAuthenticator(
        basicAuthCredentials: BasicAuthCredentials,
        accessTokenCredentials: AccessTokenCredentials
    ): AuthInterface = AuthImplementation(basicAuthCredentials, accessTokenCredentials)

    /**
     * Provides an [HttpLoggingInterceptor] configured to log full request/response bodies.
     */
    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    /**
     * Provides a [Json] instance configured to ignore unknown keys so that API responses
     * with extra fields do not cause deserialisation failures.
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    /**
     * Provides the singleton [OkHttpClient] wired with logging, Basic-Auth, and access-token
     * interceptors and timeouts sourced from [Settings].
     *
     * Uses [UnsafeOkHttpClient] (no certificate validation) for non-HTTPS base URLs such as
     * local test servers; standard [OkHttpClient.Builder] is used for HTTPS endpoints.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        basicAuthCredentials: BasicAuthCredentials,
        accessTokenCredentials: AccessTokenCredentials,
        config: MomoApiConfig
    ): OkHttpClient {
        val builder =
            if (config.baseUrl.startsWith("https")) {
                OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            } else {
                UnsafeOkHttpClient().unsafeOkHttpClient.addInterceptor(httpLoggingInterceptor)
            }

        builder.addInterceptor(BasicAuthenticationInterceptor(basicAuthCredentials))
        builder.addInterceptor(AccessTokenInterceptor(accessTokenCredentials))

        val settings = Settings()
        return builder
            .connectTimeout(settings.connectTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(settings.writeTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(settings.readTimeout, TimeUnit.MILLISECONDS)
            .build()
    }

    /**
     * Provides the singleton [Retrofit] instance configured with the base URL from [MomoApiConfig],
     * a kotlinx.serialization converter, and the shared [OkHttpClient].
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        config: MomoApiConfig
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(config.baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()

    /** Provides the [AuthenticationService] Retrofit service for token and user-provisioning endpoints. */
    @Provides
    @Singleton
    fun getAuthentication(retrofit: Retrofit): AuthenticationService = retrofit.create(AuthenticationService::class.java)

    /** Provides the [CollectionService] Retrofit service for Collection product endpoints. */
    @Provides
    @Singleton
    fun getCollection(retrofit: Retrofit): CollectionService = retrofit.create(CollectionService::class.java)

    /** Provides the [DisbursementsService] Retrofit service for Disbursements product endpoints. */
    @Provides
    @Singleton
    fun getDisbursement(retrofit: Retrofit): DisbursementsService = retrofit.create(DisbursementsService::class.java)

    /** Provides the [CommonService] Retrofit service for cross-product common endpoints. */
    @Provides
    @Singleton
    fun getCommonService(retrofit: Retrofit): CommonService = retrofit.create(CommonService::class.java)
}
