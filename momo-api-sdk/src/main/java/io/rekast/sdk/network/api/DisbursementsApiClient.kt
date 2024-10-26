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
package io.rekast.sdk.network.api

import io.rekast.sdk.network.MomoApiClient
import io.rekast.sdk.network.products.DisbursementsAPI
import okhttp3.Interceptor

object DisbursementsApiClient : MomoApiClient() {
    fun deposit(baseUrl: String, authentication: Interceptor): DisbursementsAPI =
        getRetrofit(baseUrl, authentication).create(DisbursementsAPI::class.java)

    fun getDepositStatus(baseUrl: String, authentication: Interceptor): DisbursementsAPI =
        getRetrofit(baseUrl, authentication).create(DisbursementsAPI::class.java)

    fun refund(baseUrl: String, authentication: Interceptor): DisbursementsAPI =
        getRetrofit(baseUrl, authentication).create(DisbursementsAPI::class.java)

    fun getRefundStatus(baseUrl: String, authentication: Interceptor): DisbursementsAPI =
        getRetrofit(baseUrl, authentication).create(DisbursementsAPI::class.java)
}
