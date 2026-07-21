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
import io.rekast.sdk.utils.PayerIdentificationType
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CashTransferStatusTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    /**
     * Verifies all fields map, including the plain-string [CashTransferStatus.status] and
     * [CashTransferStatus.reason], the [PayerIdentificationType] enum, and that the
     * (misspelled) `orginatingCountry` wire field maps to [CashTransferStatus.originatingCountry].
     */
    @Test
    fun `CashTransferStatus maps all fields from JSON`() {
        val raw = """
            {
              "financialTransactionId": "ftx-1",
              "status": "SUCCESSFUL",
              "reason": "none",
              "amount": "1500",
              "currency": "EUR",
              "payee": { "partyIdType": "MSISDN", "partyId": "256770000000" },
              "externalId": "ext-1",
              "orginatingCountry": "UG",
              "originalAmount": "2000",
              "originalCurrency": "UGX",
              "payerMessage": "Pay",
              "payeeNote": "Note",
              "payerIdentificationType": "PASS",
              "payerIdentificationNumber": "A1234567",
              "payerIdentity": "256780000000",
              "payerFirstName": "Jane",
              "payerSurName": "Doe",
              "payerLanguageCode": "en",
              "payerEmail": "jane.doe@example.com",
              "payerMsisdn": "256780000001",
              "payerGender": "female"
            }
        """.trimIndent()
        val result = json.decodeFromString<CashTransferStatus>(raw)
        assertEquals("ftx-1", result.financialTransactionId)
        assertEquals("SUCCESSFUL", result.status)
        assertEquals("none", result.reason)
        assertEquals("1500", result.amount)
        assertEquals("EUR", result.currency)
        assertEquals(PartyTypes.MSISDN, result.payee?.partyIdType)
        assertEquals("256770000000", result.payee?.partyId)
        assertEquals("ext-1", result.externalId)
        assertEquals("UG", result.originatingCountry)
        assertEquals("2000", result.originalAmount)
        assertEquals("UGX", result.originalCurrency)
        assertEquals("Pay", result.payerMessage)
        assertEquals("Note", result.payeeNote)
        assertEquals(PayerIdentificationType.PASS, result.payerIdentificationType)
        assertEquals("A1234567", result.payerIdentificationNumber)
        assertEquals("256780000000", result.payerIdentity)
        assertEquals("Jane", result.payerFirstName)
        assertEquals("Doe", result.payerSurName)
        assertEquals("en", result.payerLanguageCode)
        assertEquals("jane.doe@example.com", result.payerEmail)
        assertEquals("256780000001", result.payerMsisdn)
        assertEquals("female", result.payerGender)
    }

    /** The correctly-spelled `originatingCountry` key is ignored; only `orginatingCountry` maps. */
    @Test
    fun `CashTransferStatus ignores the correctly-spelled originatingCountry key`() {
        val raw = """{ "originatingCountry": "UG" }"""
        val result = json.decodeFromString<CashTransferStatus>(raw)
        assertNull(result.originatingCountry)
    }

    /** An empty object deserializes with all fields null (every field is optional). */
    @Test
    fun `CashTransferStatus decodes an empty object with null fields`() {
        val result = json.decodeFromString<CashTransferStatus>("{}")
        assertNull(result.financialTransactionId)
        assertNull(result.status)
        assertNull(result.reason)
        assertNull(result.payee)
        assertNull(result.payerIdentificationType)
        assertNull(result.originatingCountry)
    }

    @Test
    fun `CashTransferStatus round-trips when fully populated`() {
        val original = CashTransferStatus(
            financialTransactionId = "ftx-1",
            status = "SUCCESSFUL",
            reason = "none",
            amount = "1500",
            currency = "EUR",
            payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
            externalId = "ext-1",
            originatingCountry = "UG",
            originalAmount = "2000",
            originalCurrency = "UGX",
            payerMessage = "Pay",
            payeeNote = "Note",
            payerIdentificationType = PayerIdentificationType.PASS,
            payerIdentificationNumber = "A1234567",
            payerIdentity = "256780000000",
            payerFirstName = "Jane",
            payerSurName = "Doe",
            payerLanguageCode = "en",
            payerEmail = "jane.doe@example.com",
            payerMsisdn = "256780000001",
            payerGender = "female"
        )
        assertEquals(original, jsonWithDefaults.decodeFromString<CashTransferStatus>(jsonWithDefaults.encodeToString(original)))
    }
}
