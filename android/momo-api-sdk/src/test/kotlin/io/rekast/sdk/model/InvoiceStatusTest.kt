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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InvoiceStatusTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    /** Verifies all fields map, including the reused [StatusTypes], [ErrorResponse], and [Party]. */
    @Test
    fun `InvoiceStatus maps all fields from JSON`() {
        val raw = """
            {
              "referenceId": "ref-1",
              "externalId": "ext-1",
              "amount": "1000",
              "currency": "EUR",
              "status": "SUCCESSFUL",
              "paymentReference": "pay-ref-1",
              "invoiceId": "inv-1",
              "expiryDateTime": "2026-07-21T12:00:00",
              "payeeFirstName": "Sand",
              "payeeLastName": "Box",
              "errorReason": { "code": "PAYEE_NOT_FOUND", "message": "payee missing" },
              "intendedPayer": { "partyIdType": "MSISDN", "partyId": "256774290781" },
              "description": "Invoice #99"
            }
        """.trimIndent()
        val result = json.decodeFromString<InvoiceStatus>(raw)
        assertEquals("ref-1", result.referenceId)
        assertEquals(StatusTypes.SUCCESSFUL, result.status)
        assertEquals("inv-1", result.invoiceId)
        assertEquals("Sand", result.payeeFirstName)
        assertEquals("PAYEE_NOT_FOUND", result.errorReason?.code)
        assertEquals(PartyTypes.MSISDN, result.intendedPayer?.partyIdType)
        assertEquals("256774290781", result.intendedPayer?.partyId)
    }

    /** An empty object deserializes with all fields null (every field is optional). */
    @Test
    fun `InvoiceStatus decodes an empty object with null fields`() {
        val result = json.decodeFromString<InvoiceStatus>("{}")
        assertNull(result.referenceId)
        assertNull(result.status)
        assertNull(result.errorReason)
        assertNull(result.intendedPayer)
    }

    @Test
    fun `InvoiceStatus round-trips`() {
        val original = InvoiceStatus(
            referenceId = "ref-1",
            status = StatusTypes.PENDING,
            invoiceId = "inv-1",
            errorReason = ErrorResponse(code = "C", message = "m"),
            intendedPayer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256774290781")
        )
        assertEquals(original, jsonWithDefaults.decodeFromString<InvoiceStatus>(jsonWithDefaults.encodeToString(original)))
    }
}
