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
package com.rekast.momoapi.network.okhttp

import com.rekast.momoapi.utils.MomoConstants
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This is an authentication Interceptor. It is used after the client has received and Access Token.
 * It adds access token  to all endpoints that need Access Token Authentication.
 * @param [accessToken]
 */

class AccessTokenInterceptor(
    private var accessToken: String
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(MomoConstants.Headers.AUTHORIZATION, "${MomoConstants.TokenTypes.BEARER} $accessToken")
            .build()

        return chain.proceed(request)
    }
}
