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
import io.rekast.sdk.utils.StatusTypes
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DepositStatusTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    /** Verifies all fields map, including the [Party] payee, typed [StatusTypes], and reused [ErrorResponse]. */
    @Test
    fun `DepositStatus maps all fields from JSON`() {
        val raw = """
            {
              "amount": "1500",
              "currency": "EUR",
              "financialTransactionId": "ftx-1",
              "externalId": "ext-1",
              "payee": { "partyIdType": "MSISDN", "partyId": "256770000000" },
              "payerMessage": "Pay",
              "payeeNote": "Note",
              "status": "SUCCESSFUL",
              "reason": { "code": "PAYEE_NOT_FOUND", "message": "payee missing" }
            }
        """.trimIndent()
        val result = json.decodeFromString<DepositStatus>(raw)
        assertEquals("1500", result.amount)
        assertEquals("EUR", result.currency)
        assertEquals("ftx-1", result.financialTransactionId)
        assertEquals("ext-1", result.externalId)
        assertEquals(PartyTypes.MSISDN, result.payee?.partyIdType)
        assertEquals("256770000000", result.payee?.partyId)
        assertEquals("Pay", result.payerMessage)
        assertEquals("Note", result.payeeNote)
        assertEquals(StatusTypes.SUCCESSFUL, result.status)
        assertEquals("PAYEE_NOT_FOUND", result.reason?.code)
        assertEquals("payee missing", result.reason?.message)
    }

    /** An empty object deserializes with all fields null (every field is optional). */
    @Test
    fun `DepositStatus decodes an empty object with null fields`() {
        val result = json.decodeFromString<DepositStatus>("{}")
        assertNull(result.amount)
        assertNull(result.payee)
        assertNull(result.status)
        assertNull(result.reason)
    }

    @Test
    fun `DepositStatus round-trips when fully populated`() {
        val original = DepositStatus(
            amount = "1500",
            currency = "EUR",
            financialTransactionId = "ftx-1",
            externalId = "ext-1",
            payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
            payerMessage = "Pay",
            payeeNote = "Note",
            status = StatusTypes.PENDING,
            reason = ErrorResponse(code = "PAYEE_NOT_FOUND", message = "x")
        )
        assertEquals(original, jsonWithDefaults.decodeFromString<DepositStatus>(jsonWithDefaults.encodeToString(original)))
    }
}
