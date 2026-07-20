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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PreApprovalTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullPreApproval() = PreApproval(
        payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerCurrency = "EUR",
        payerMessage = "Please approve this pre-approval",
        validityTime = 3600
    )

    /** Verifies that [PreApproval] required and optional fields are mapped correctly from JSON. */
    @Test
    fun `PreApproval fields are mapped correctly from JSON`() {
        val raw = """
            {
              "payer": { "partyIdType": "MSISDN", "partyId": "256700000001" },
              "payerCurrency": "EUR",
              "payerMessage": "Pre-approval for subscription",
              "validityTime": 7200
            }
        """.trimIndent()
        val result = json.decodeFromString<PreApproval>(raw)
        assertNotNull(result)
        assertEquals(PartyTypes.MSISDN, result.payer.partyIdType)
        assertEquals("256700000001", result.payer.partyId)
        assertEquals("EUR", result.payerCurrency)
        assertEquals("Pre-approval for subscription", result.payerMessage)
        assertEquals(7200, result.validityTime)
    }

    /** Verifies that [PreApproval.payerMessage] defaults to null when absent. */
    @Test
    fun `PreApproval payerMessage defaults to null when absent`() {
        val raw = """
            {
              "payer": { "partyIdType": "MSISDN", "partyId": "256700000002" },
              "payerCurrency": "UGX",
              "validityTime": 3600
            }
        """.trimIndent()
        val result = json.decodeFromString<PreApproval>(raw)
        assertNull(result.payerMessage)
    }

    @Test
    fun `PreApproval round-trips when fully populated`() {
        val original = fullPreApproval()
        assertEquals(original, jsonSkipDefaults.decodeFromString<PreApproval>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<PreApproval>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `PreApproval round-trips a minimal instance`() {
        val original = PreApproval(
            payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
            payerCurrency = "EUR",
            validityTime = 3600
        )
        assertEquals(original, jsonSkipDefaults.decodeFromString<PreApproval>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<PreApproval>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `PreApproval decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<PreApproval>("{}")
    }
}
