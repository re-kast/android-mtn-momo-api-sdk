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
package io.rekast.sdk.network.interceptor.headers

import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.utils.Constants
import java.io.IOException
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that attaches the optional `X-Callback-Url` header to transaction-initiation requests.
 *
 * MTN MOMO supports a callback URL on the asynchronous POST operations that start a transaction —
 * request-to-pay/withdraw, deposit, refund, transfer, cash transfer, invoice, payment, and
 * pre-approval — to which the platform posts the result once the operation completes. It is *not*
 * used by status (GET), cancel (DELETE), account/balance/user-info queries, delivery-notification
 * POSTs, or the auth/token POSTs, so this interceptor scopes the header to the initiation endpoints
 * by matching the request method (`POST`) and the final path segment.
 *
 * The URL is read from [CredentialProvider.getCallbackUrl] on every request; when it is blank the
 * header is omitted, making the callback opt-in (no behavior change until a URL is configured).
 *
 * @param credentialProvider Supplies the configured callback URL at request time.
 */
class CallbackUrlInterceptor @Inject constructor(private val credentialProvider: CredentialProvider) : Interceptor {

    /**
     * Adds the `X-Callback-Url` header when a callback URL is configured and the request targets a
     * transaction-initiation endpoint; otherwise forwards the request unchanged.
     *
     * @param chain The interceptor chain.
     * @return The response after conditionally adding the callback header.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val operation = request.url.pathSegments.lastOrNull()

        if (request.method == "POST" && operation in INITIATION_ENDPOINTS) {
            val callbackUrl = credentialProvider.getCallbackUrl(operation!!)
            if (callbackUrl.isNotBlank()) {
                return chain.proceed(
                    request.newBuilder()
                        .header(Constants.Headers.X_CALLBACK_URL, callbackUrl)
                        .build()
                )
            }
        }
        return chain.proceed(request)
    }

    private companion object {
        /**
         * Final path segments of the transaction-initiation POST endpoints that accept a callback URL.
         * Status/cancel endpoints end in the reference id, and notification POSTs end in
         * `deliverynotification`, so neither collides with these.
         */
        val INITIATION_ENDPOINTS = setOf(
            "requesttopay",
            "requesttowithdraw",
            "deposit",
            "refund",
            "transfer",
            "cashtransfer",
            "invoice",
            "preapproval",
            "payment"
        )
    }
}
