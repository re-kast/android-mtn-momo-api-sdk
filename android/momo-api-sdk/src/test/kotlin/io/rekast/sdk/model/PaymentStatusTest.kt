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

import io.rekast.sdk.utils.StatusTypes
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PaymentStatusTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    /** Verifies all fields map, including the typed [StatusTypes] enum and reused [ErrorResponse]. */
    @Test
    fun `PaymentStatus maps all fields including the status enum and reason`() {
        val raw = """
            {
              "referenceId": "ref-1",
              "status": "SUCCESSFUL",
              "financialTransactionId": "ftx-1",
              "reason": { "code": "PAYEE_NOT_FOUND", "message": "payee missing" }
            }
        """.trimIndent()
        val result = json.decodeFromString<PaymentStatus>(raw)
        assertEquals("ref-1", result.referenceId)
        assertEquals(StatusTypes.SUCCESSFUL, result.status)
        assertEquals("ftx-1", result.financialTransactionId)
        assertEquals("PAYEE_NOT_FOUND", result.reason?.code)
        assertEquals("payee missing", result.reason?.message)
    }

    /** An empty object deserializes with all fields null (every field is optional). */
    @Test
    fun `PaymentStatus decodes an empty object with null fields`() {
        val result = json.decodeFromString<PaymentStatus>("{}")
        assertNull(result.referenceId)
        assertNull(result.status)
        assertNull(result.reason)
    }

    @Test
    fun `PaymentStatus round-trips`() {
        val original = PaymentStatus(
            referenceId = "ref-1",
            status = StatusTypes.PENDING,
            financialTransactionId = "ftx-1",
            reason = ErrorResponse(code = "PAYEE_NOT_FOUND", message = "x")
        )
        assertEquals(original, jsonWithDefaults.decodeFromString<PaymentStatus>(jsonWithDefaults.encodeToString(original)))
    }
}
