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

import com.rekast.momoapi.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This interceptor will be responsible for adding the MTN MOMO API specific headers.
 * @param[ocpAPImSubscriptionKey] -- API subscription key
 * @param[xReferenceId] -- API User ID
 * @param[xTargetEnvironment] -- API Target environment
 * @param[language] -- The language to use for the request
 * @param[xCallbackUrl] -- The API Callback URL
 * @param[notificationMessage] -- The notification message for the endpoint
 * @param[contentType] -- API request content type.
 */
class MomoApiHeadersInterceptor(
    private val ocpAPImSubscriptionKey: String,
    private val xReferenceId: String,
    private val xTargetEnvironment: String,
    private val xCallbackUrl: String? = null,
    private val language: String? = null,
    private val notificationMessage: String? = null,
    private val contentType: String? = null,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(Constants.Headers.OCP_APIM_SUBSCRIPTION_KEY, ocpAPImSubscriptionKey)
            .header(Constants.Headers.X_REFERENCE_ID, xReferenceId)
            .header(Constants.Headers.X_TARGET_ENVIRONMENT, xTargetEnvironment)
            .header(Constants.Headers.X_CALLBACK_URL, xCallbackUrl!!)
            .header(Constants.Headers.CONTENT_TYPE, contentType!!)
            .header(Constants.Headers.LANGUAGE, language!!)
            .header(Constants.Headers.NOTIFICATION_MESSAGE, notificationMessage!!)
            .build()

        return chain.proceed(request)
    }
}
