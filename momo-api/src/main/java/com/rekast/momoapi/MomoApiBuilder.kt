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

import com.rekast.momoapi.repository.MomoApiRepository
import com.rekast.momoapi.utils.TransactionType

/**
 * Keys Builder. Creates a [MomoApi] payment load.
 * @param [consumerKey] for your application
 * @param [consumerSecret] for your application
 */

class MomoApiBuilder(private var consumerKey: String, private var consumerSecret: String) {

    private lateinit var businessShortCode: String
    private lateinit var passKey: String
    private lateinit var transactionType: TransactionType
    private lateinit var callbackUrl: String
    // private lateinit var environment: Environment

    fun setPassKey(passKey: String): MomoApiBuilder {
        this.passKey = passKey
        return this
    }

    fun setTransactionType(transactionType: TransactionType): MomoApiBuilder {
        this.transactionType = transactionType
        return this
    }

    fun setCallbackUrl(callbackUrl: String): MomoApiBuilder {
        this.callbackUrl = callbackUrl
        return this
    }

    fun setBusinessShortCode(businessShortCode: String): MomoApiBuilder {
        this.businessShortCode = businessShortCode
        return this
    }

/*    fun setEnvironment(environment: Environment): MomoApiBuilder {
        this.environment = environment
        return this
    }*/

    fun build(): MomoApi {
        val momoApi = MomoApi
        MomoApi.consumerKey = consumerKey
        MomoApi.consumerSecret = consumerSecret
        MomoApi.businessShortCode = businessShortCode
        MomoApi.passKey = passKey
        MomoApi.transactionType = transactionType
        MomoApi.callbackUrl = callbackUrl
        // MomoApi.baseUrl = environment.url

        MomoApi.repo = MomoApiRepository(
            MomoApi.consumerKey,
            MomoApi.consumerSecret,
            MomoApi.baseUrl,
        )

        return momoApi
    }
}
