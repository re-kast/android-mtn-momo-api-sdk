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

class CashTransferTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullCashTransfer() = CashTransfer(
        amount = "1500",
        currency = "EUR",
        externalId = "ext-123",
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerMessage = "Sending funds",
        payeeNote = "Received funds",
        payerIdentificationType = "PASS",
        payerIdentificationNumber = "A1234567",
        payerIdentity = "256780000000",
        payerFirstName = "Jane",
        payerSurName = "Doe",
        payerLanguageCode = "en",
        payerEmail = "jane.doe@example.com",
        payerMsisdn = "256780000001",
        payerGender = "female",
        originatingCountry = "UG",
        originalAmount = "2000",
        originalCurrency = "UGX"
    )

    /** Verifies that required [CashTransfer] fields are mapped correctly from JSON. */
    @Test
    fun `CashTransfer required fields are mapped correctly from JSON`() {
        val raw = """
            {
              "amount": "1000",
              "currency": "EUR",
              "externalId": "ct-001",
              "payee": { "partyIdType": "MSISDN", "partyId": "256700000003" },
              "payerMessage": "Remittance transfer",
              "payeeNote": "Funds received"
            }
        """.trimIndent()
        val result = json.decodeFromString<CashTransfer>(raw)
        assertNotNull(result)
        assertEquals("1000", result.amount)
        assertEquals("EUR", result.currency)
        assertEquals("ct-001", result.externalId)
        assertEquals(PartyTypes.MSISDN, result.payee.partyIdType)
        assertEquals("256700000003", result.payee.partyId)
        assertEquals("Remittance transfer", result.payerMessage)
    }

    /** Verifies that optional [CashTransfer] KYC fields are mapped correctly when present. */
    @Test
    fun `CashTransfer optional KYC fields are mapped when present`() {
        val raw = """
            {
              "amount": "500",
              "currency": "XOF",
              "externalId": "ct-002",
              "payee": { "partyIdType": "MSISDN", "partyId": "221700000001" },
              "payerMessage": "Transfer",
              "payeeNote": "Received",
              "payerFirstName": "John",
              "payerSurName": "Doe",
              "payerLanguageCode": "en",
              "payerEmail": "john.doe@example.com",
              "originatingCountry": "GH",
              "originalAmount": "450",
              "originalCurrency": "GHS"
            }
        """.trimIndent()
        val result = json.decodeFromString<CashTransfer>(raw)
        assertNotNull(result)
        assertEquals("John", result.payerFirstName)
        assertEquals("Doe", result.payerSurName)
        assertEquals("en", result.payerLanguageCode)
        assertEquals("john.doe@example.com", result.payerEmail)
        assertEquals("GH", result.originatingCountry)
        assertEquals("450", result.originalAmount)
        assertEquals("GHS", result.originalCurrency)
    }

    /** Verifies that optional [CashTransfer] KYC fields default to null when absent. */
    @Test
    fun `CashTransfer optional fields default to null when absent`() {
        val raw = """
            {
              "amount": "100",
              "currency": "EUR",
              "externalId": "ct-003",
              "payee": { "partyIdType": "MSISDN", "partyId": "256700000004" },
              "payerMessage": "Transfer",
              "payeeNote": "Note"
            }
        """.trimIndent()
        val result = json.decodeFromString<CashTransfer>(raw)
        assertNull(result.payerIdentificationType)
        assertNull(result.payerIdentificationNumber)
        assertNull(result.payerFirstName)
        assertNull(result.payerEmail)
        assertNull(result.originatingCountry)
    }

    @Test
    fun `CashTransfer round-trips when fully populated`() {
        val original = fullCashTransfer()
        assertEquals(original, jsonSkipDefaults.decodeFromString<CashTransfer>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<CashTransfer>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `CashTransfer round-trips a minimal instance`() {
        val original = CashTransfer(
            amount = "1500",
            currency = "EUR",
            externalId = "ext-123",
            payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
            payerMessage = "Sending funds",
            payeeNote = "Received funds"
        )
        assertEquals(original, jsonSkipDefaults.decodeFromString<CashTransfer>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<CashTransfer>(jsonWithDefaults.encodeToString(original)))
    }
}
