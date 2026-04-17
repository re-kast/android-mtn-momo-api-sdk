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
package io.rekast.sdk.sample.utils

import io.rekast.sdk.utils.ProductType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilsTest {

    private fun buildConfig(
        collectionPrimaryKey: String = "col-primary",
        collectionSecondaryKey: String = "col-secondary",
        remittancePrimaryKey: String = "rem-primary",
        remittanceSecondaryKey: String = "rem-secondary",
        disbursementsPrimaryKey: String = "dis-primary",
        disbursementsSecondaryKey: String = "dis-secondary"
    ) = SampleConfig(
        apiVersionV1 = "v1_0",
        apiVersionV2 = "v2_0",
        environment = "sandbox",
        providerCallbackHost = "example.com",
        apiUserId = "test-user-id",
        collectionPrimaryKey = collectionPrimaryKey,
        collectionSecondaryKey = collectionSecondaryKey,
        remittancePrimaryKey = remittancePrimaryKey,
        remittanceSecondaryKey = remittanceSecondaryKey,
        disbursementsPrimaryKey = disbursementsPrimaryKey,
        disbursementsSecondaryKey = disbursementsSecondaryKey
    )

    @Test
    fun `convertToDate returns yyyy-MM-dd formatted string`() {
        val result = Utils.convertToDate(0L)
        assertEquals("1970-01-01", result)
    }

    @Test
    fun `convertToDate formats a known Unix timestamp correctly`() {
        val result = Utils.convertToDate(1000000000000L)
        assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `getProductSubscriptionKeys returns collectionPrimaryKey for COLLECTION when not blank`() {
        val config = buildConfig(collectionPrimaryKey = "col-primary-key")
        val result = Utils.getProductSubscriptionKeys(ProductType.COLLECTION, config)
        assertEquals("col-primary-key", result)
    }

    @Test
    fun `getProductSubscriptionKeys falls back to collectionSecondaryKey when collectionPrimaryKey is blank`() {
        val config = buildConfig(collectionPrimaryKey = "", collectionSecondaryKey = "col-secondary-key")
        val result = Utils.getProductSubscriptionKeys(ProductType.COLLECTION, config)
        assertEquals("col-secondary-key", result)
    }

    @Test
    fun `getProductSubscriptionKeys returns remittancePrimaryKey for REMITTANCE when not blank`() {
        val config = buildConfig(remittancePrimaryKey = "rem-primary-key")
        val result = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, config)
        assertEquals("rem-primary-key", result)
    }

    @Test
    fun `getProductSubscriptionKeys falls back to remittanceSecondaryKey when remittancePrimaryKey is blank`() {
        val config = buildConfig(remittancePrimaryKey = "", remittanceSecondaryKey = "rem-secondary-key")
        val result = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, config)
        assertEquals("rem-secondary-key", result)
    }

    @Test
    fun `getProductSubscriptionKeys returns disbursementsPrimaryKey for DISBURSEMENTS when not blank`() {
        val config = buildConfig(disbursementsPrimaryKey = "dis-primary-key")
        val result = Utils.getProductSubscriptionKeys(ProductType.DISBURSEMENTS, config)
        assertEquals("dis-primary-key", result)
    }

    @Test
    fun `getProductSubscriptionKeys falls back to disbursementsSecondaryKey when disbursementsPrimaryKey is blank`() {
        val config = buildConfig(disbursementsPrimaryKey = "", disbursementsSecondaryKey = "dis-secondary-key")
        val result = Utils.getProductSubscriptionKeys(ProductType.DISBURSEMENTS, config)
        assertEquals("dis-secondary-key", result)
    }
}
