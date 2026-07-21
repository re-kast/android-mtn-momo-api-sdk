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

class RequestToPayTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullRequestToPay() = RequestToPay(
        amount = "500",
        currency = "EUR",
        externalId = "ext-001",
        payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerMessage = "Payment for order #42",
        payeeNote = "Order #42"
    )

    /** Verifies that all required [RequestToPay] fields and the nested [Party] payer are mapped. */
    @Test
    fun `RequestToPay required fields are mapped correctly from JSON`() {
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
        val result = json.decodeFromString<RequestToPay>(raw)
        assertNotNull(result)
        assertEquals("500.00", result.amount)
        assertEquals("UGX", result.currency)
        assertEquals("ext-001", result.externalId)
        assertEquals("Payment for goods", result.payerMessage)
        assertEquals("Thank you", result.payeeNote)
        assertNotNull(result.payer)
        assertEquals(PartyTypes.MSISDN, result.payer?.partyIdType)
        assertEquals("256700000001", result.payer?.partyId)
        assertNull(result.payee)
    }

    @Test
    fun `RequestToPay round-trips when fully populated`() {
        val original = fullRequestToPay()
        assertEquals(original, jsonWithDefaults.decodeFromString<RequestToPay>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `RequestToPay decoding fails when required fields are absent`() {
        json.decodeFromString<RequestToPay>("{}")
    }
}
