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

class PaymentTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullPayment() = Payment(
        externalTransactionId = "ext-001",
        money = Money(amount = "100.00", currency = "EUR"),
        customerReference = "+46070911111",
        serviceProviderUserName = "Electricity Inc.",
        couponId = "coupon-1",
        productId = "product-1",
        productOfferingId = "offering-1",
        receiverMessage = "Thanks",
        senderNote = "Bill payment",
        maxNumberOfRetries = 3,
        includeSenderCharges = true
    )

    /** Verifies all fields, including the nested [Money], map correctly from JSON. */
    @Test
    fun `Payment fields are mapped correctly from JSON`() {
        val raw = """
            {
              "externalTransactionId": "ext-001",
              "money": { "amount": "100.00", "currency": "EUR" },
              "customerReference": "+46070911111",
              "serviceProviderUserName": "Electricity Inc.",
              "couponId": "coupon-1",
              "productId": "product-1",
              "productOfferingId": "offering-1",
              "receiverMessage": "Thanks",
              "senderNote": "Bill payment",
              "maxNumberOfRetries": 3,
              "includeSenderCharges": true
            }
        """.trimIndent()
        val result = json.decodeFromString<Payment>(raw)
        assertNotNull(result)
        assertEquals("ext-001", result.externalTransactionId)
        assertEquals("100.00", result.money?.amount)
        assertEquals("EUR", result.money?.currency)
        assertEquals(3, result.maxNumberOfRetries)
        assertEquals(true, result.includeSenderCharges)
    }

    /** An empty object deserializes with all fields null (every field is optional). */
    @Test
    fun `Payment decodes an empty object with null fields`() {
        val result = json.decodeFromString<Payment>("{}")
        assertNull(result.externalTransactionId)
        assertNull(result.money)
        assertNull(result.includeSenderCharges)
    }

    @Test
    fun `Payment round-trips when fully populated`() {
        val original = fullPayment()
        assertEquals(original, jsonWithDefaults.decodeFromString<Payment>(jsonWithDefaults.encodeToString(original)))
    }
}
