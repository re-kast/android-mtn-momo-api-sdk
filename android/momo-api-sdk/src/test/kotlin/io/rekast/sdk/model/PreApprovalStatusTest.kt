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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PreApprovalStatusTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    /** Verifies the status payload maps, reusing [Party] for payer and [ErrorResponse] for reason. */
    @Test
    fun `PreApprovalStatus maps all fields including reused payer and reason`() {
        val raw = """
            {
              "payer": { "partyIdType": "MSISDN", "partyId": "256774290781" },
              "payerCurrency": "EUR",
              "payerMessage": "I PAY YOU",
              "status": "PENDING",
              "expirationDateTime": 0,
              "reason": { "code": "PAYEE_NOT_FOUND", "message": "payee missing" }
            }
        """.trimIndent()
        val result = json.decodeFromString<PreApprovalStatus>(raw)
        assertNotNull(result)
        assertEquals(PartyTypes.MSISDN, result.payer?.partyIdType)
        assertEquals("256774290781", result.payer?.partyId)
        assertEquals("EUR", result.payerCurrency)
        assertEquals(StatusTypes.PENDING, result.status)
        assertEquals(0L, result.expirationDateTime)
        assertEquals("PAYEE_NOT_FOUND", result.reason?.code)
        assertEquals("payee missing", result.reason?.message)
    }

    /** An empty object deserializes with all fields null (every field is optional). */
    @Test
    fun `PreApprovalStatus decodes an empty object with null fields`() {
        val result = json.decodeFromString<PreApprovalStatus>("{}")
        assertNull(result.payer)
        assertNull(result.status)
        assertNull(result.reason)
    }

    @Test
    fun `PreApprovalStatus round-trips`() {
        val original = PreApprovalStatus(
            payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256774290781"),
            payerCurrency = "EUR",
            payerMessage = "hi",
            status = StatusTypes.SUCCESSFUL,
            expirationDateTime = 120L,
            reason = ErrorResponse(code = "PAYEE_NOT_FOUND", message = "x")
        )
        assertEquals(original, jsonWithDefaults.decodeFromString<PreApprovalStatus>(jsonWithDefaults.encodeToString(original)))
    }
}
