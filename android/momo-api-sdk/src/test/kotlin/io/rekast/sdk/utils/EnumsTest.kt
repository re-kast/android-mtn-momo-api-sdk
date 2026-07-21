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
package io.rekast.sdk.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the SDK utility enums: [ProductTypes], [PartyTypes], and [StatusTypes].
 *
 * Verifies that each enum contains the expected constants, that [Enum.valueOf] resolves every
 * constant by name, and that the string property values exposed by [ProductTypes] and
 * [PartyTypes] match the documented MTN MOMO API identifiers.
 */
class EnumsTest {

    // ---- ProductTypes ----

    /**
     * Verifies that [ProductTypes] exposes exactly three product types: Collection, Disbursements,
     * and Remittance — the three MTN MOMO product lines supported by this SDK.
     */
    @Test
    fun `ProductType has exactly 3 entries`() {
        assertEquals(3, ProductTypes.entries.size)
    }

    /**
     * Verifies that [ProductTypes.COLLECTION] carries the string `"collection"` used as the URL
     * path segment in the MTN MOMO API.
     */
    @Test
    fun `ProductType COLLECTION productType is collection`() {
        assertEquals("collection", ProductTypes.COLLECTION.productType)
    }

    /**
     * Verifies that [ProductTypes.DISBURSEMENTS] carries the string `"disbursements"`.
     */
    @Test
    fun `ProductType DISBURSEMENTS productType is disbursements`() {
        assertEquals("disbursements", ProductTypes.DISBURSEMENTS.productType)
    }

    /**
     * Verifies that [ProductTypes.REMITTANCE] carries the string `"remittance"`.
     */
    @Test
    fun `ProductType REMITTANCE productType is remittance`() {
        assertEquals("remittance", ProductTypes.REMITTANCE.productType)
    }

    /**
     * Verifies that all [ProductTypes] entries round-trip through [ProductTypes.valueOf].
     */
    @Test
    fun `ProductType all entries resolve by valueOf`() {
        for (entry in ProductTypes.entries) {
            assertEquals(entry, ProductTypes.valueOf(entry.name))
        }
    }

    // ---- PartyTypes ----

    /**
     * Verifies that [PartyTypes] exposes exactly three holder types: MSISDN, EMAIL,
     * and PARTY_CODE.
     */
    @Test
    fun `AccountHolderType has exactly 3 entries`() {
        assertEquals(3, PartyTypes.entries.size)
    }

    /**
     * Verifies that [PartyTypes.MSISDN] carries the string `"msisdn"` used in API requests.
     */
    @Test
    fun `AccountHolderType MSISDN accountHolderType is msisdn`() {
        assertEquals("msisdn", PartyTypes.MSISDN.partyType)
    }

    /**
     * Verifies that [PartyTypes.EMAIL] carries the string `"email"`.
     */
    @Test
    fun `AccountHolderType EMAIL accountHolderType is email`() {
        assertEquals("email", PartyTypes.EMAIL.partyType)
    }

    /**
     * Verifies that [PartyTypes.PARTY_CODE] carries the string `"party_code"`.
     */
    @Test
    fun `AccountHolderType PARTY_CODE accountHolderType is party_code`() {
        assertEquals("party_code", PartyTypes.PARTY_CODE.partyType)
    }

    /**
     * Verifies that all [PartyTypes] entries round-trip through [PartyTypes.valueOf].
     */
    @Test
    fun `AccountHolderType all entries resolve by valueOf`() {
        for (entry in PartyTypes.entries) {
            assertEquals(entry, PartyTypes.valueOf(entry.name))
        }
    }

    // ---- StatusTypes ----

    /**
     * Verifies that [StatusTypes] exposes exactly eight states: the transaction states
     * (SUCCESSFUL, PENDING, FAILED, CREATED) plus the pre-approval states (APPROVED, CANCELLED,
     * EXPIRED, REJECTED).
     */
    @Test
    fun `StatusTypes has exactly 8 entries`() {
        assertEquals(8, StatusTypes.entries.size)
    }

    /**
     * Verifies that all [StatusTypes] constants are accessible and round-trip
     * through [StatusTypes.valueOf].
     */
    @Test
    fun `StatusTypes all entries resolve by valueOf`() {
        for (entry in StatusTypes.entries) {
            assertEquals(entry, StatusTypes.valueOf(entry.name))
        }
    }

    /**
     * Spot-checks the ordinal positions of [StatusTypes] constants to catch accidental
     * reordering.
     */
    @Test
    fun `StatusTypes ordinals are stable`() {
        assertEquals(0, StatusTypes.SUCCESSFUL.ordinal)
        assertEquals(1, StatusTypes.PENDING.ordinal)
        assertEquals(2, StatusTypes.FAILED.ordinal)
        assertEquals(3, StatusTypes.CREATED.ordinal)
        assertEquals(4, StatusTypes.APPROVED.ordinal)
        assertEquals(5, StatusTypes.CANCELLED.ordinal)
        assertEquals(6, StatusTypes.EXPIRED.ordinal)
        assertEquals(7, StatusTypes.REJECTED.ordinal)
    }

    // ---- FrequencyTypes ----

    /**
     * Verifies that [FrequencyTypes] exposes exactly three cadences: DAILY, WEEKLY, and MONTHLY.
     */
    @Test
    fun `FrequencyTypes has exactly 3 entries`() {
        assertEquals(3, FrequencyTypes.entries.size)
    }

    /**
     * Verifies that all [FrequencyTypes] constants are accessible and round-trip
     * through [FrequencyTypes.valueOf].
     */
    @Test
    fun `FrequencyTypes all entries resolve by valueOf`() {
        for (entry in FrequencyTypes.entries) {
            assertEquals(entry, FrequencyTypes.valueOf(entry.name))
        }
    }

    // ---- PayerIdentificationType ----

    /**
     * Verifies that [PayerIdentificationType] exposes exactly the ten identification-document
     * types defined by the MTN MOMO cash transfer API.
     */
    @Test
    fun `PayerIdentificationType has exactly 10 entries`() {
        assertEquals(10, PayerIdentificationType.entries.size)
    }

    /**
     * Verifies that [PayerIdentificationType] contains each expected constant so that a rename
     * or accidental removal is caught.
     */
    @Test
    fun `PayerIdentificationType contains the expected constants`() {
        val names = PayerIdentificationType.entries.map { it.name }.toSet()
        assertEquals(
            setOf("PASS", "CPFA", "SRSSA", "NRIN", "OTHR", "DRLC", "SOCS", "AREG", "IDCD", "EMID"),
            names
        )
    }

    /**
     * Verifies that all [PayerIdentificationType] constants round-trip through
     * [PayerIdentificationType.valueOf] by their API name.
     */
    @Test
    fun `PayerIdentificationType all entries resolve by valueOf`() {
        for (entry in PayerIdentificationType.entries) {
            assertEquals(entry, PayerIdentificationType.valueOf(entry.name))
        }
    }
}
