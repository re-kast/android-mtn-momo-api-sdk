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

class RequestToPayStatusTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    /** Verifies all fields map, including the [Party] payer, typed [StatusTypes], and reused [ErrorResponse]. */
    @Test
    fun `RequestToPayStatus maps all fields from JSON`() {
        val raw = """
            {
              "amount": "1500",
              "currency": "EUR",
              "financialTransactionId": "ftx-1",
              "externalId": "ext-1",
              "payer": { "partyIdType": "MSISDN", "partyId": "256770000000" },
              "payerMessage": "Pay",
              "payeeNote": "Note",
              "status": "SUCCESSFUL",
              "reason": { "code": "PAYER_NOT_FOUND", "message": "payer missing" }
            }
        """.trimIndent()
        val result = json.decodeFromString<RequestToPayStatus>(raw)
        assertEquals("1500", result.amount)
        assertEquals("EUR", result.currency)
        assertEquals("ftx-1", result.financialTransactionId)
        assertEquals("ext-1", result.externalId)
        assertEquals(PartyTypes.MSISDN, result.payer?.partyIdType)
        assertEquals("256770000000", result.payer?.partyId)
        assertEquals("Pay", result.payerMessage)
        assertEquals("Note", result.payeeNote)
        assertEquals(StatusTypes.SUCCESSFUL, result.status)
        assertEquals("PAYER_NOT_FOUND", result.reason?.code)
        assertEquals("payer missing", result.reason?.message)
    }

    /** An empty object deserializes with all fields null (every field is optional). */
    @Test
    fun `RequestToPayStatus decodes an empty object with null fields`() {
        val result = json.decodeFromString<RequestToPayStatus>("{}")
        assertNull(result.amount)
        assertNull(result.payer)
        assertNull(result.status)
        assertNull(result.reason)
    }

    @Test
    fun `RequestToPayStatus round-trips when fully populated`() {
        val original = RequestToPayStatus(
            amount = "1500",
            currency = "EUR",
            financialTransactionId = "ftx-1",
            externalId = "ext-1",
            payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
            payerMessage = "Pay",
            payeeNote = "Note",
            status = StatusTypes.PENDING,
            reason = ErrorResponse(code = "PAYER_NOT_FOUND", message = "x")
        )
        assertEquals(original, jsonWithDefaults.decodeFromString<RequestToPayStatus>(jsonWithDefaults.encodeToString(original)))
    }
}
