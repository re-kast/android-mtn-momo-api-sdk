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
package com.rekast.momoapi

import com.rekast.momoapi.repository.MomoAPIRepository
import com.rekast.momoapi.utils.ProductType

/**
 * Creates the MOMO Payment Details
 */
object MomoAPI {
    lateinit var apiUserId: String
    lateinit var productType: ProductType
    lateinit var baseURL: String
    lateinit var environment: String
    lateinit var momoAPIRepository: MomoAPIRepository
    fun builder(apiUserId: String): MomoAPIBuilder = MomoAPIBuilder(apiUserId)

    fun getBasicAuthenticationToken() {

    }

    /* fun getAccessToken(callback: ((momoAPIResult: MomoAPIResult<AccessToken>) -> Unit)) {
         repo.accessToken.enqueue(MomoAPICallback(callback))
     }*/

    /*fun initiatePayment(token: String, phoneNumber: String, amount: String, accountReference: String, description: String, callback: ((momoAPIResult: MomoAPIResult<PaymentResult>) -> Unit)) {
        repo.initiatePayment(
            token = token,
            phoneNumber = phoneNumber,
            amount = amount,
            accountReference = accountReference,
            description = description,
            businessShortCode = businessShortCode,
            passKey = passKey,
            transactionType = transactionType,
            callbackUrl = callbackUrl
        ).enqueue(MomoAPIPaymentCallback(callback))
    }*/
}
