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
package io.rekast.sdk.sample.utils

import io.rekast.sdk.utils.ProductType
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Utility object providing general-purpose helper functions for the sample app.
 *
 * Credential storage has moved to [CredentialStorage].
 */
object Utils {

    /**
     * Converts the given milliseconds to a formatted date string.
     *
     * @param milliseconds The time in milliseconds to convert.
     * @return A formatted date string in "yyyy-MM-dd" format.
     */
    fun convertToDate(milliseconds: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return simpleDateFormat.format(milliseconds)
    }

    /**
     * Retrieves the product subscription key for the specified product type.
     *
     * @param productType The MTN MOMO API product type.
     * @param config The sample app runtime configuration.
     * @return The corresponding product subscription key.
     */
    fun getProductSubscriptionKeys(productType: ProductType, config: SampleConfig): String =
        when (productType) {
            ProductType.COLLECTION -> config.collectionPrimaryKey.ifBlank { config.collectionSecondaryKey }
            ProductType.REMITTANCE -> config.remittancePrimaryKey.ifBlank { config.remittanceSecondaryKey }
            ProductType.DISBURSEMENTS -> config.disbursementsPrimaryKey.ifBlank { config.disbursementsSecondaryKey }
        }
}
