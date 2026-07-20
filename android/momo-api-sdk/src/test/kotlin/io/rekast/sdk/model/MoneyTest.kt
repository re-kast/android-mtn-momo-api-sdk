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
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    @Test
    fun `Money fields are mapped correctly from JSON`() {
        val raw = """{ "amount": "250.00", "currency": "UGX" }"""
        val result = json.decodeFromString<Money>(raw)
        assertEquals("250.00", result.amount)
        assertEquals("UGX", result.currency)
    }

    @Test
    fun `Money decodes an empty object with null fields`() {
        val result = json.decodeFromString<Money>("{}")
        assertNull(result.amount)
        assertNull(result.currency)
    }

    @Test
    fun `Money round-trips when fully populated`() {
        val original = Money(amount = "100.00", currency = "EUR")
        assertEquals(original, jsonWithDefaults.decodeFromString<Money>(jsonWithDefaults.encodeToString(original)))
    }
}
