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

import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials
import io.rekast.sdk.utils.MomoConstants
import java.io.IOException
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * Interceptor used to add Bearer Token authentication headers to requests.
 *
 * This interceptor adds the access token to the request headers for endpoints
 * that require Bearer Token Authentication.
 *
 * @param accessTokenCredentials The credentials containing the access token.
 */
class AccessTokenInterceptor @Inject constructor(
    private var accessTokenCredentials: AccessTokenCredentials
) : Interceptor {

    /**
     * Intercepts the request and adds the Bearer Token header.
     *
     * @param chain The interceptor chain.
     * @return The response after adding the authorization header.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = accessTokenCredentials.accessToken

        val request = chain.request().newBuilder()

        if (accessToken.isNotEmpty()) {
            Timber.d("Access Token: $accessToken")
            return chain.proceed(
                request.addHeader(MomoConstants.Headers.AUTHORIZATION, "${MomoConstants.TokenTypes.BEARER} $accessToken")
                    .build()
            )
        }

        return chain.proceed(request.build())
    }
}
