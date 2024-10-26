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
package io.rekast.sdk.network.okhttp

import android.util.Base64
import io.rekast.sdk.utils.MomoConstants
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor used to add the authorization to allow the user to get an hit the Access Token endpoint.
 * It adds [apiUserId] and [apiKey] encoded to base 64 to all endpoints that need Basic Authentication.
 *
 * @param [apiUserId]
 * @param [apiKey]
 */
class BasicAuthInterceptor(
    private val apiUserId: String,
    private val apiKey: String
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val keys = "$apiUserId:$apiKey"

        val request = chain.request().newBuilder()
            .addHeader(
                MomoConstants.Headers.AUTHORIZATION,
                "${MomoConstants.TokenTypes.BASIC} " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)
            )
            .build()

        return chain.proceed(request)
    }
}
