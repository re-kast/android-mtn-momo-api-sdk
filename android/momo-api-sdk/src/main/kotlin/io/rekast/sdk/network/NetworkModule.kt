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
package io.rekast.sdk.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.rekast.sdk.BuildConfig
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
import io.rekast.sdk.utils.Settings
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides network-related dependencies using Dagger Hilt.
 *
 * This module includes methods to provide instances of Retrofit, OkHttpClient,
 * authentication credentials, and various services used in the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides an instance of [BasicAuthCredentials].
     *
     * @return A new instance of [BasicAuthCredentials] initialized with empty strings.
     */
    @Provides
    @Singleton
    fun provideBasicAuthCredentials(): BasicAuthCredentials {
        return BasicAuthCredentials("", "")
    }

    /**
     * Provides an instance of [AccessTokenCredentials].
     *
     * @return A new instance of [AccessTokenCredentials] initialized with an empty string.
     */
    @Provides
    @Singleton
    fun provideAccessTokenCredentials(): AccessTokenCredentials {
        return AccessTokenCredentials("")
    }

    /**
     * Provides an instance of [AuthenticationService].
     *
     * @param retrofit The Retrofit instance used to create the service.
     * @return An instance of [AuthenticationService].
     */
    @Provides
    @Singleton
    fun getAuthentication(retrofit: Retrofit): AuthenticationService =
        retrofit.create(AuthenticationService::class.java)

    /**
     * Provides an instance of [CollectionService].
     *
     * @param retrofit The Retrofit instance used to create the service.
     * @return An instance of [CollectionService].
     */
    @Provides
    @Singleton
    fun getCollection(retrofit: Retrofit): CollectionService =
        retrofit.create(CollectionService::class.java)

    /**
     * Provides an instance of [DisbursementsService].
     *
     * @param retrofit The Retrofit instance used to create the service.
     * @return An instance of [DisbursementsService].
     */
    @Provides
    @Singleton
    fun getDisbursement(retrofit: Retrofit): DisbursementsService =
        retrofit.create(DisbursementsService::class.java)

    /**
     * Provides an instance of [CommonService].
     *
     * @param retrofit The Retrofit instance used to create the service.
     * @return An instance of [CommonService].
     */
    @Provides
    @Singleton
    fun getCommonService(retrofit: Retrofit): CommonService =
        retrofit.create(CommonService::class.java)

    /**
     * Provides an instance of [Retrofit].
     *
     * @param okHttpClient The OkHttpClient instance to be used by Retrofit.
     * @param gson The Gson instance for JSON serialization/deserialization.
     * @param baseUrl The base URL for the API.
     * @return A configured instance of [Retrofit].
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    /**
     * Provides an instance of [AuthInterface].
     *
     * @param basicAuthCredentials The basic authentication credentials.
     * @param accessTokenCredentials The access token credentials.
     * @return An instance of [AuthInterface] implemented by [AuthImplementation].
     */
    @Provides
    @Singleton
    fun provideApiAuthenticator(
        basicAuthCredentials: BasicAuthCredentials,
        accessTokenCredentials: AccessTokenCredentials
    ): AuthInterface {
        return AuthImplementation(basicAuthCredentials, accessTokenCredentials)
    }

    /**
     * Provides an instance of [HttpLoggingInterceptor].
     *
     * @return A configured instance of [HttpLoggingInterceptor] with body logging enabled.
     */
    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    /**
     * Provides an instance of [Gson].
     *
     * @return A new instance of [Gson] created using GsonBuilder.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    /**
     * Provides the base URL for the API.
     *
     * @return The base URL defined in the BuildConfig.
     */
    @Provides
    @Singleton
    fun providesBaseUrl(): String {
        return BuildConfig.MOMO_BASE_URL
    }

    /**
     * Provides an instance of [OkHttpClient].
     *
     * @param httpLoggingInterceptor The logging interceptor for HTTP requests.
     * @param basicAuthCredentials The basic authentication credentials.
     * @param accessTokenCredentials The access token credentials.
     * @param baseUrl The base URL for the API.
     * @return A configured instance of [OkHttpClient].
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        basicAuthCredentials: BasicAuthCredentials,
        accessTokenCredentials: AccessTokenCredentials,
        baseUrl: String
    ): OkHttpClient {
        val builder = if (baseUrl.contains("https")) {
            OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
        } else {
            UnsafeOkHttpClient().unsafeOkHttpClient.addInterceptor(httpLoggingInterceptor)
        }

        builder.addInterceptor(BasicAuthenticationInterceptor(basicAuthCredentials))
        builder.addInterceptor(AccessTokenInterceptor(accessTokenCredentials))

        return builder.connectTimeout(Settings().CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Settings().WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Settings().READ_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
}
