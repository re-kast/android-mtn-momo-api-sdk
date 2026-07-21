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

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RefundTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullRefund() = Refund(
        amount = "100",
        currency = "EUR",
        externalId = "ext-005",
        payerMessage = "Refund for order #42",
        payeeNote = "Refund",
        referenceIdToRefund = "orig-txn-001"
    )

    /** Verifies that all required [Refund] fields and the [Refund.referenceIdToRefund] are mapped. */
    @Test
    fun `Refund required fields are mapped correctly from JSON`() {
        val raw = """
            {
              "amount": "100.00",
              "currency": "UGX",
              "externalId": "ext-005",
              "payerMessage": "Refund",
              "payeeNote": "Refund",
              "referenceIdToRefund": "orig-txn-001"
            }
        """.trimIndent()
        val result = json.decodeFromString<Refund>(raw)
        assertNotNull(result)
        assertEquals("100.00", result.amount)
        assertEquals("UGX", result.currency)
        assertEquals("ext-005", result.externalId)
        assertEquals("Refund", result.payerMessage)
        assertEquals("Refund", result.payeeNote)
        assertEquals("orig-txn-001", result.referenceIdToRefund)
    }

    /** Verifies that the optional [Refund.referenceIdToRefund] defaults to null when absent. */
    @Test
    fun `Refund referenceIdToRefund defaults to null when absent`() {
        val raw = """
            {
              "amount": "100.00",
              "currency": "UGX",
              "externalId": "ext-005",
              "payerMessage": "Refund",
              "payeeNote": "Refund"
            }
        """.trimIndent()
        val result = json.decodeFromString<Refund>(raw)
        assertNotNull(result)
        assertNull(result.referenceIdToRefund)
    }

    @Test
    fun `Refund round-trips when fully populated`() {
        val original = fullRefund()
        assertEquals(original, jsonWithDefaults.decodeFromString<Refund>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `Refund decoding fails when required fields are absent`() {
        json.decodeFromString<Refund>("{}")
    }
}
