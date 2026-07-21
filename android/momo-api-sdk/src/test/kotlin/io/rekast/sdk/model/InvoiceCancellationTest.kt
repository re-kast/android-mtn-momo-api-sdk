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
import org.junit.Test

class InvoiceCancellationTest {

    private val json = Json { ignoreUnknownKeys = true }

    /** Verifies [InvoiceCancellation.externalId] is mapped correctly from JSON. */
    @Test
    fun `InvoiceCancellation externalId is mapped correctly from JSON`() {
        val raw = """{ "externalId": "cancel-ref-001" }"""
        val result = json.decodeFromString<InvoiceCancellation>(raw)
        assertNotNull(result)
        assertEquals("cancel-ref-001", result.externalId)
    }

    /** Verifies the model serializes to the exact `{ "externalId": ... }` body MTN expects. */
    @Test
    fun `InvoiceCancellation serializes to the expected request body`() {
        val encoded = json.encodeToString(InvoiceCancellation(externalId = "cancel-ref-001"))
        assertEquals("""{"externalId":"cancel-ref-001"}""", encoded)
    }

    /** Verifies a fully-populated instance round-trips through JSON unchanged. */
    @Test
    fun `InvoiceCancellation round-trips through JSON`() {
        val original = InvoiceCancellation(externalId = "cancel-ref-001")
        assertEquals(original, json.decodeFromString<InvoiceCancellation>(json.encodeToString(original)))
    }

    /** Verifies decoding an object missing the required externalId throws. */
    @Test(expected = SerializationException::class)
    fun `InvoiceCancellation decoding an empty object throws`() {
        json.decodeFromString<InvoiceCancellation>("{}")
    }
}
