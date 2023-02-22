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

import com.rekast.momoapi.repository.products.MomoCollectionAPIRepository
import com.rekast.momoapi.repository.products.MomoDisbursementsAPIRepository
import com.rekast.momoapi.repository.products.MomoRemittanceAPIRepository
import com.rekast.momoapi.utils.ProductType

/**
 * Keys Builder. Creates a [MomoAPI] payment load.
 * @param [apiUserId] for your application
 * @param [consumerSecret] for your application
 */

class MomoAPIBuilder(private var apiUserId: String) {
    private lateinit var productType: ProductType
    private lateinit var environment: String
    private lateinit var baseURL: String

    fun getBaseURL(baseURL: String): MomoAPIBuilder {
        this.baseURL = baseURL
        return this
    }
    fun setEnvironment(environment: String): MomoAPIBuilder {
        this.environment = environment
        return this
    }

    fun setTransactionType(productType: ProductType): MomoAPIBuilder {
        this.productType = productType
        return this
    }

    fun build(): MomoAPI {
        val momoApi = MomoAPI
        momoApi.apiUserId = apiUserId
        momoApi.productType = productType
        momoApi.baseURL = baseURL
        momoApi.environment = environment
        getRepository(momoApi)
        return momoApi
    }

    private fun getRepository(momoApi: MomoAPI) {
        when {
            productType.equals(ProductType.COLLECTION) -> {
                momoApi.momoAPIRepository = MomoCollectionAPIRepository(
                    momoApi.apiUserId,
                    momoApi.baseURL,
                )
            }
            productType.equals(ProductType.DISBURSEMENTS) -> {
                momoApi.momoAPIRepository = MomoDisbursementsAPIRepository(
                    momoApi.apiUserId,
                    momoApi.baseURL,
                )
            }
            else -> {
                momoApi.momoAPIRepository = MomoRemittanceAPIRepository(
                    momoApi.apiUserId,
                    momoApi.baseURL,
                )
            }
        }
    }
}
