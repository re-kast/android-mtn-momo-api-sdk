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

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MomoTransactionTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullMomoTransaction() = MomoTransaction(
        amount = "5000",
        currency = "EUR",
        financialTransactionId = "fin-789",
        externalId = "ext-789",
        payee = AccountHolder(partyIdType = "MSISDN", partyId = "256770000000"),
        payer = AccountHolder(partyIdType = "MSISDN", partyId = "256780000000"),
        payerMessage = "Payment message",
        payeeNote = "Payment note",
        status = "SUCCESSFUL",
        reason = "None",
        referenceIdToRefund = "ref-refund-1"
    )

    /** Verifies that all required [MomoTransaction] fields and the nested [AccountHolder] payer are mapped. */
    @Test
    fun `MomoTransaction required fields are mapped correctly from JSON`() {
        val raw = """
            {
              "amount": "500.00",
              "currency": "UGX",
              "externalId": "ext-001",
              "payerMessage": "Payment for goods",
              "payeeNote": "Thank you",
              "payer": {
                "partyIdType": "MSISDN",
                "partyId": "256700000001"
              }
            }
        """.trimIndent()
        val result = json.decodeFromString<MomoTransaction>(raw)
        assertNotNull(result)
        assertEquals("500.00", result.amount)
        assertEquals("UGX", result.currency)
        assertEquals("ext-001", result.externalId)
        assertEquals("Payment for goods", result.payerMessage)
        assertEquals("Thank you", result.payeeNote)
        assertNotNull(result.payer)
        assertEquals("MSISDN", result.payer?.partyIdType)
        assertEquals("256700000001", result.payer?.partyId)
    }

    /**
     * Verifies that optional [MomoTransaction] fields default to empty strings or null when
     * absent from the payload, matching the field default values declared on the model.
     */
    @Test
    fun `MomoTransaction optional fields default correctly when absent`() {
        val raw = """
            {
              "amount": "100.00",
              "currency": "GHS",
              "externalId": "ext-002",
              "payerMessage": "msg",
              "payeeNote": "note"
            }
        """.trimIndent()
        val result = json.decodeFromString<MomoTransaction>(raw)
        assertNotNull(result)
        assertEquals("", result.financialTransactionId)
        assertNull(result.payee)
        assertNull(result.payer)
        assertEquals("", result.status)
        assertEquals("", result.reason)
        assertEquals("", result.referenceIdToRefund)
    }

    /**
     * Verifies that both [MomoTransaction.payee] and [MomoTransaction.payer] are mapped correctly
     * when both are present, along with all other non-nullable fields.
     */
    @Test
    fun `MomoTransaction payee and payer are mapped when both present`() {
        val raw = """
            {
              "amount": "250.00",
              "currency": "XOF",
              "financialTransactionId": "fin-txn-999",
              "externalId": "ext-003",
              "payee": {
                "partyIdType": "MSISDN",
                "partyId": "221700000002"
              },
              "payer": {
                "partyIdType": "MSISDN",
                "partyId": "221700000003"
              },
              "payerMessage": "Invoice #42",
              "payeeNote": "Received",
              "status": "SUCCESSFUL",
              "reason": "",
              "referenceIdToRefund": "ref-001"
            }
        """.trimIndent()
        val result = json.decodeFromString<MomoTransaction>(raw)
        assertNotNull(result)
        assertEquals("fin-txn-999", result.financialTransactionId)
        assertEquals("SUCCESSFUL", result.status)
        assertEquals("ref-001", result.referenceIdToRefund)
        assertEquals("221700000002", result.payee?.partyId)
        assertEquals("221700000003", result.payer?.partyId)
    }

    @Test
    fun `MomoTransaction round-trips when fully populated`() {
        val original = fullMomoTransaction()
        assertEquals(original, jsonSkipDefaults.decodeFromString<MomoTransaction>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<MomoTransaction>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `MomoTransaction round-trips a minimal instance`() {
        val original = MomoTransaction(
            amount = "5000",
            currency = "EUR",
            externalId = "ext-789",
            payerMessage = "Payment message",
            payeeNote = "Payment note"
        )
        assertEquals(original, jsonSkipDefaults.decodeFromString<MomoTransaction>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<MomoTransaction>(jsonWithDefaults.encodeToString(original)))
    }
}
