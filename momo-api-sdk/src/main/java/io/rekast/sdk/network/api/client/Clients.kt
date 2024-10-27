/*
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package io.rekast.sdk.network.api.client

import io.io.rekast.momoapi.utils.Settings
import io.rekast.sdk.BuildConfig
import io.rekast.sdk.network.Authentication
import io.rekast.sdk.network.okhttp.UnsafeOkHttpClient
import io.rekast.sdk.network.products.Collection
import io.rekast.sdk.network.products.Common
import io.rekast.sdk.network.products.Disbursements
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides an instance of retrofit to all classes that need it.
 */

open class Clients {
    /**
     * Common APIs across all the Products
     */
    fun getAuthentication(baseUrl: String, authentication: Interceptor?): Authentication =
        getRetrofit(baseUrl, authentication).create(Authentication::class.java)

    fun getCollection(baseUrl: String, authentication: Interceptor): Collection =
        getRetrofit(baseUrl, authentication).create(Collection::class.java)

    fun getCommon(baseUrl: String, authentication: Interceptor): Common =
        getRetrofit(baseUrl, authentication).create(Common::class.java)

    fun getDisbursement(baseUrl: String, authentication: Interceptor): Disbursements =
        getRetrofit(baseUrl, authentication).create(Disbursements::class.java)

    fun getRetrofit(baseUrl: String, authentication: Interceptor?): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val builder = if (baseUrl == BuildConfig.MOMO_BASE_URL) {
            UnsafeOkHttpClient().unsafeOkHttpClient.addInterceptor(httpLoggingInterceptor)
        } else {
            OkHttpClient.Builder()
        }
        val client = returnOkHttpClient(builder, authentication)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun returnOkHttpClient(
        builder: OkHttpClient.Builder,
        authentication: Interceptor?
    ): OkHttpClient {
        return if (authentication == null) {
            builder.connectTimeout(Settings.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Settings.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Settings.READ_TIMEOUT, TimeUnit.SECONDS)
                .build()
        } else {
            builder.connectTimeout(Settings.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Settings.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Settings.READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(authentication)
                .build()
        }
    }
}