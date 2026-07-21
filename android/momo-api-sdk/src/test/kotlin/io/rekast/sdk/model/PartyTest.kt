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
import org.junit.Test

class PartyTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullAccountHolder() = Party(
        partyIdType = PartyTypes.MSISDN,
        partyId = "256774290781"
    )

    /** Verifies that [Party.partyIdType] and [Party.partyId] are mapped correctly. */
    @Test
    fun `AccountHolder fields are mapped correctly from JSON`() {
        val raw = """
            {
              "partyIdType": "MSISDN",
              "partyId": "256700000000"
            }
        """.trimIndent()
        val result = json.decodeFromString<Party>(raw)
        assertNotNull(result)
        assertEquals(PartyTypes.MSISDN, result.partyIdType)
        assertEquals("256700000000", result.partyId)
    }

    @Test
    fun `AccountHolder round-trips when fully populated`() {
        val original = fullAccountHolder()
        assertEquals(original, jsonSkipDefaults.decodeFromString<Party>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<Party>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `AccountHolder decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<Party>("{}")
    }
}
