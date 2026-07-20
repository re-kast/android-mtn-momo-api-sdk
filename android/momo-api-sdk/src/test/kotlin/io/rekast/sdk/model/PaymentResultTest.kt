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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PaymentResultTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullPaymentResult() = PaymentResult(
        merchantRequestID = "merchant-req-001",
        checkoutRequestID = "checkout-req-002",
        responseCode = "0",
        responseDescription = "The service request is processed successfully.",
        customerMessage = "Success. Request accepted for processing"
    )

    /** Verifies that all [PaymentResult] fields are mapped correctly from the MTN MoMo response keys. */
    @Test
    fun `PaymentResult fields are mapped correctly from JSON`() {
        val raw = """
            {
              "MerchantRequestID": "mrq-001",
              "CheckoutRequestID": "crq-001",
              "ResponseCode": "0",
              "ResponseDescription": "Success",
              "CustomerMessage": "Payment accepted"
            }
        """.trimIndent()
        val result = json.decodeFromString<PaymentResult>(raw)
        assertNotNull(result)
        assertEquals("mrq-001", result.merchantRequestID)
        assertEquals("crq-001", result.checkoutRequestID)
        assertEquals("0", result.responseCode)
        assertEquals("Success", result.responseDescription)
        assertEquals("Payment accepted", result.customerMessage)
    }

    @Test
    fun `PaymentResult round-trips when fully populated`() {
        val original = fullPaymentResult()
        assertEquals(original, jsonSkipDefaults.decodeFromString<PaymentResult>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<PaymentResult>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `PaymentResult decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<PaymentResult>("{}")
    }
}
