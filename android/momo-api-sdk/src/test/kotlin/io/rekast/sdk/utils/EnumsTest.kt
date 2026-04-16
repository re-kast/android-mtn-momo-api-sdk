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
package io.rekast.sdk.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the SDK utility enums: [ProductType], [AccountHolderType], and [TransactionStatus].
 *
 * Verifies that each enum contains the expected constants, that [Enum.valueOf] resolves every
 * constant by name, and that the string property values exposed by [ProductType] and
 * [AccountHolderType] match the documented MTN MOMO API identifiers.
 */
class EnumsTest {

    // ---- ProductType ----

    /**
     * Verifies that [ProductType] exposes exactly three product types: Collection, Disbursements,
     * and Remittance — the three MTN MOMO product lines supported by this SDK.
     */
    @Test
    fun `ProductType has exactly 3 entries`() {
        assertEquals(3, ProductType.entries.size)
    }

    /**
     * Verifies that [ProductType.COLLECTION] carries the string `"collection"` used as the URL
     * path segment in the MTN MOMO API.
     */
    @Test
    fun `ProductType COLLECTION productType is collection`() {
        assertEquals("collection", ProductType.COLLECTION.productType)
    }

    /**
     * Verifies that [ProductType.DISBURSEMENTS] carries the string `"disbursements"`.
     */
    @Test
    fun `ProductType DISBURSEMENTS productType is disbursements`() {
        assertEquals("disbursements", ProductType.DISBURSEMENTS.productType)
    }

    /**
     * Verifies that [ProductType.REMITTANCE] carries the string `"remittance"`.
     */
    @Test
    fun `ProductType REMITTANCE productType is remittance`() {
        assertEquals("remittance", ProductType.REMITTANCE.productType)
    }

    /**
     * Verifies that all [ProductType] entries round-trip through [ProductType.valueOf].
     */
    @Test
    fun `ProductType all entries resolve by valueOf`() {
        for (entry in ProductType.entries) {
            assertEquals(entry, ProductType.valueOf(entry.name))
        }
    }

    // ---- AccountHolderType ----

    /**
     * Verifies that [AccountHolderType] exposes exactly three holder types: MSISDN, EMAIL,
     * and PARTY_CODE.
     */
    @Test
    fun `AccountHolderType has exactly 3 entries`() {
        assertEquals(3, AccountHolderType.entries.size)
    }

    /**
     * Verifies that [AccountHolderType.MSISDN] carries the string `"msisdn"` used in API requests.
     */
    @Test
    fun `AccountHolderType MSISDN accountHolderType is msisdn`() {
        assertEquals("msisdn", AccountHolderType.MSISDN.accountHolderType)
    }

    /**
     * Verifies that [AccountHolderType.EMAIL] carries the string `"email"`.
     */
    @Test
    fun `AccountHolderType EMAIL accountHolderType is email`() {
        assertEquals("email", AccountHolderType.EMAIL.accountHolderType)
    }

    /**
     * Verifies that [AccountHolderType.PARTY_CODE] carries the string `"party_code"`.
     */
    @Test
    fun `AccountHolderType PARTY_CODE accountHolderType is party_code`() {
        assertEquals("party_code", AccountHolderType.PARTY_CODE.accountHolderType)
    }

    /**
     * Verifies that all [AccountHolderType] entries round-trip through [AccountHolderType.valueOf].
     */
    @Test
    fun `AccountHolderType all entries resolve by valueOf`() {
        for (entry in AccountHolderType.entries) {
            assertEquals(entry, AccountHolderType.valueOf(entry.name))
        }
    }

    // ---- TransactionStatus ----

    /**
     * Verifies that [TransactionStatus] exposes exactly three states: SUCCESSFUL, PENDING,
     * and FAILED.
     */
    @Test
    fun `TransactionStatus has exactly 3 entries`() {
        assertEquals(3, TransactionStatus.entries.size)
    }

    /**
     * Verifies that all three [TransactionStatus] constants are accessible and round-trip
     * through [TransactionStatus.valueOf].
     */
    @Test
    fun `TransactionStatus all entries resolve by valueOf`() {
        for (entry in TransactionStatus.entries) {
            assertEquals(entry, TransactionStatus.valueOf(entry.name))
        }
    }

    /**
     * Spot-checks the ordinal positions of [TransactionStatus] constants to catch accidental
     * reordering.
     */
    @Test
    fun `TransactionStatus ordinals are stable`() {
        assertEquals(0, TransactionStatus.SUCCESSFUL.ordinal)
        assertEquals(1, TransactionStatus.PENDING.ordinal)
        assertEquals(2, TransactionStatus.FAILED.ordinal)
    }
}
