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
import org.junit.Test

class TransferTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullTransfer() = Transfer(
        amount = "250",
        currency = "EUR",
        externalId = "ext-003",
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerMessage = "Salary payment",
        payeeNote = "March salary"
    )

    /** Verifies that all required [Transfer] fields and the nested [Party] payee are mapped. */
    @Test
    fun `Transfer required fields are mapped correctly from JSON`() {
        val raw = """
            {
              "amount": "250.00",
              "currency": "UGX",
              "externalId": "ext-003",
              "payerMessage": "Salary",
              "payeeNote": "Received",
              "payee": {
                "partyIdType": "MSISDN",
                "partyId": "256700000002"
              }
            }
        """.trimIndent()
        val result = json.decodeFromString<Transfer>(raw)
        assertNotNull(result)
        assertEquals("250.00", result.amount)
        assertEquals("UGX", result.currency)
        assertEquals("ext-003", result.externalId)
        assertEquals("Salary", result.payerMessage)
        assertEquals("Received", result.payeeNote)
        assertNotNull(result.payee)
        assertEquals(PartyTypes.MSISDN, result.payee?.partyIdType)
        assertEquals("256700000002", result.payee?.partyId)
    }

    @Test
    fun `Transfer round-trips when fully populated`() {
        val original = fullTransfer()
        assertEquals(original, jsonWithDefaults.decodeFromString<Transfer>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `Transfer decoding fails when required fields are absent`() {
        json.decodeFromString<Transfer>("{}")
    }
}
