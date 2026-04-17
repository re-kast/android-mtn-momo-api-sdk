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
package io.rekast.sdk.network.interceptor.auth

import io.rekast.sdk.Logger
import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.utils.Constants
import java.io.IOException
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor used to add Basic Authentication headers to requests.
 *
 * This interceptor encodes the API user ID and API key in Base64 and adds them
 * to the request headers for endpoints that require Basic Authentication.
 *
 * Credentials are fetched from [CredentialProvider] on every request so that
 * the SDK never holds credential state internally.
 *
 * @param credentialProvider Supplies the API user ID and API key at request time.
 */
class BasicAuthenticationInterceptor @Inject constructor(private val credentialProvider: CredentialProvider) : Interceptor {

    /**
     * Intercepts the request and adds the Basic Authentication header.
     *
     * @param chain The interceptor chain.
     * @return The response after adding the authorization header.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiUserId = credentialProvider.getApiUserId()
        val apiKey = credentialProvider.getApiKey()

        val request = chain.request().newBuilder()

        if (apiUserId.isNotEmpty() && apiKey.isNotEmpty()) {
            Logger.d("BasicAuth", "API User ID: $apiUserId, API Key: $apiKey")
            val keys = "$apiUserId:$apiKey"

            @OptIn(ExperimentalEncodingApi::class)
            val encoded = Base64.Default.encode(keys.toByteArray())

            return chain.proceed(
                request.header(
                    Constants.Headers.AUTHORIZATION,
                    "${Constants.TokenTypes.BASIC} $encoded"
                ).build()
            )
        }
        return chain.proceed(request.build())
    }
}
