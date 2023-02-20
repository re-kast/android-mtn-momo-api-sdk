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
 * Keys Builder. Creates a [MomoAPI] payment load.
 * @param [apiUserId] for your application
 * @param [consumerSecret] for your application
 */

class MomoAPIBuilder(private var apiUserId: String) {

    private lateinit var businessShortCode: String
    private lateinit var passKey: String
    private lateinit var productType: ProductType
    private lateinit var callbackUrl: String
    // private lateinit var environment: Environment

    fun setPassKey(passKey: String): MomoAPIBuilder {
        this.passKey = passKey
        return this
    }

    fun setTransactionType(productType: ProductType): MomoAPIBuilder {
        this.productType = productType
        return this
    }

    fun setCallbackUrl(callbackUrl: String): MomoAPIBuilder {
        this.callbackUrl = callbackUrl
        return this
    }

    fun setBusinessShortCode(businessShortCode: String): MomoAPIBuilder {
        this.businessShortCode = businessShortCode
        return this
    }

/*    fun setEnvironment(environment: Environment): MomoApiBuilder {
        this.environment = environment
        return this
    }*/

    fun build(): MomoAPI {
        val momoApi = MomoAPI
        // MomoAPI.consumerKey = consumerKey
        // MomoAPI.consumerSecret = consumerSecret
        MomoAPI.businessShortCode = businessShortCode
        MomoAPI.passKey = passKey
        MomoAPI.productType = productType
        MomoAPI.callbackUrl = callbackUrl
        // MomoApi.baseUrl = environment.url

        MomoAPI.repo = MomoAPIRepository(
            MomoAPI.apiUserId,
            MomoAPI.baseUrl,
        )

        return momoApi
    }
}
