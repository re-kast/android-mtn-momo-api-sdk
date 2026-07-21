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

class PartyStatusTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullAccountHolderStatus() = AccountHolderStatus(result = true)

    /** Verifies that [AccountHolderStatus.result] is `true` when the JSON contains `true`. */
    @Test
    fun `AccountHolderStatus result is true when JSON contains true`() {
        val raw = """{ "result": true }"""
        val result = json.decodeFromString<AccountHolderStatus>(raw)
        assertNotNull(result)
        assertEquals(true, result.result)
    }

    /** Verifies that [AccountHolderStatus.result] is `false` when the JSON contains `false`. */
    @Test
    fun `AccountHolderStatus result is false when JSON contains false`() {
        val raw = """{ "result": false }"""
        val result = json.decodeFromString<AccountHolderStatus>(raw)
        assertNotNull(result)
        assertEquals(false, result.result)
    }

    @Test
    fun `AccountHolderStatus round-trips when fully populated`() {
        val original = fullAccountHolderStatus()
        assertEquals(original, jsonSkipDefaults.decodeFromString<AccountHolderStatus>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<AccountHolderStatus>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `AccountHolderStatus decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<AccountHolderStatus>("{}")
    }
}
