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

import android.util.Base64
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials
import io.rekast.sdk.utils.MomoConstants
import java.io.IOException
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * Interceptor used to add Basic Authentication headers to requests.
 *
 * This interceptor encodes the API user ID and API key in Base64 and adds them
 * to the request headers for endpoints that require Basic Authentication.
 *
 * @param basicAuthCredentials The credentials containing the API user ID and API key.
 */
class BasicAuthenticationInterceptor @Inject constructor(
    private val basicAuthCredentials: BasicAuthCredentials
) : Interceptor {

    /**
     * Intercepts the request and adds the Basic Authentication header.
     *
     * @param chain The interceptor chain.
     * @return The response after adding the authorization header.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiUserId = basicAuthCredentials.apiUserId
        val apiKey = basicAuthCredentials.apiKey

        val request = chain.request().newBuilder()

        if (apiUserId.isNotEmpty() && apiKey.isNotEmpty()) {
            Timber.d("API User ID: $apiUserId, API Key: $apiKey")
            val keys = "$apiUserId:$apiKey"

            return chain.proceed(
                request.addHeader(
                    MomoConstants.Headers.AUTHORIZATION,
                    "${MomoConstants.TokenTypes.BASIC} " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)
                ).build()
            )
        }
        return chain.proceed(request.build())
    }
}
