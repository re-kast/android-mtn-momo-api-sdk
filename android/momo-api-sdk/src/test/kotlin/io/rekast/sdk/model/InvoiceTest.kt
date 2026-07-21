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
package io.rekast.sdk.model

import io.rekast.sdk.utils.PartyTypes
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class InvoiceTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullInvoice() = Invoice(
        externalId = "ext-456",
        amount = "3000",
        currency = "EUR",
        validityDuration = "3600",
        intendedPayer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256780000000"),
        description = "Consulting services rendered"
    )

    /** Verifies that required [Invoice] fields and optional fields are mapped correctly. */
    @Test
    fun `Invoice required fields are mapped correctly from JSON`() {
        val raw = """
            {
              "externalId": "inv-001",
              "amount": "500",
              "currency": "EUR",
              "validityDuration": "3600",
              "description": "Monthly subscription",
              "intendedPayer": {
                "partyIdType": "MSISDN",
                "partyId": "256700000000"
              },
              "payee": {
                "partyIdType": "MSISDN",
                "partyId": "256711111111"
              }
            }
        """.trimIndent()
        val result = json.decodeFromString<Invoice>(raw)
        assertNotNull(result)
        assertEquals("inv-001", result.externalId)
        assertEquals("500", result.amount)
        assertEquals("EUR", result.currency)
        assertEquals("3600", result.validityDuration)
        assertEquals("Monthly subscription", result.description)
        assertNotNull(result.intendedPayer)
        assertEquals("256700000000", result.intendedPayer?.partyId)
        assertEquals("256711111111", result.payee?.partyId)
    }

    /** Verifies that [Invoice] optional fields default to null when absent. */
    @Test
    fun `Invoice optional fields default to null when absent`() {
        val raw = """{ "externalId": "inv-002", "amount": "100", "currency": "UGX" }"""
        val result = json.decodeFromString<Invoice>(raw)
        assertNotNull(result)
        assertNull(result.validityDuration)
        assertNull(result.intendedPayer)
        assertNull(result.payee)
        assertNull(result.description)
    }

    @Test
    fun `Invoice round-trips when fully populated`() {
        val original = fullInvoice()
        assertEquals(original, jsonSkipDefaults.decodeFromString<Invoice>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<Invoice>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `Invoice round-trips a minimal instance`() {
        val original = Invoice(
            externalId = "ext-456",
            amount = "3000",
            currency = "EUR"
        )
        assertEquals(original, jsonSkipDefaults.decodeFromString<Invoice>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<Invoice>(jsonWithDefaults.encodeToString(original)))
    }
}
