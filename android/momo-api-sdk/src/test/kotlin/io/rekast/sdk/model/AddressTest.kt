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
import org.junit.Test

class AddressTest {

    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullAddress() = Address(
        formatted = "12 Main St\nNairobi",
        streetAddress = "12 Main St",
        postalCode = "00100",
        locality = "Nairobi",
        region = "Nairobi County",
        country = "Kenya"
    )

    @Test
    fun `Address round-trips when fully populated`() {
        val original = fullAddress()
        assertEquals(original, jsonSkipDefaults.decodeFromString<Address>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<Address>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `Address round-trips an all-default instance`() {
        val original = Address()
        assertEquals(original, jsonSkipDefaults.decodeFromString<Address>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<Address>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `Address decodes a partial payload leaving absent fields null`() {
        val result = jsonSkipDefaults.decodeFromString<Address>("""{"locality":"Nairobi"}""")
        assertEquals("Nairobi", result.locality)
        assertEquals(null, result.country)
    }
}
