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

class DepositTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullDeposit() = Deposit(
        amount = "100",
        currency = "EUR",
        externalId = "ext-004",
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerMessage = "Cash deposit",
        payeeNote = "Deposit"
    )

    /** Verifies that all required [Deposit] fields and the nested [Party] payee are mapped. */
    @Test
    fun `Deposit required fields are mapped correctly from JSON`() {
        val raw = """
            {
              "amount": "100.00",
              "currency": "UGX",
              "externalId": "ext-004",
              "payerMessage": "Cash deposit",
              "payeeNote": "Deposit",
              "payee": {
                "partyIdType": "MSISDN",
                "partyId": "256700000003"
              }
            }
        """.trimIndent()
        val result = json.decodeFromString<Deposit>(raw)
        assertNotNull(result)
        assertEquals("100.00", result.amount)
        assertEquals("UGX", result.currency)
        assertEquals("ext-004", result.externalId)
        assertEquals("Cash deposit", result.payerMessage)
        assertEquals("Deposit", result.payeeNote)
        assertNotNull(result.payee)
        assertEquals(PartyTypes.MSISDN, result.payee?.partyIdType)
        assertEquals("256700000003", result.payee?.partyId)
    }

    @Test
    fun `Deposit round-trips when fully populated`() {
        val original = fullDeposit()
        assertEquals(original, jsonWithDefaults.decodeFromString<Deposit>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `Deposit decoding fails when required fields are absent`() {
        json.decodeFromString<Deposit>("{}")
    }
}
