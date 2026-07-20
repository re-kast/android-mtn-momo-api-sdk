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
package io.rekast.sdk.model.authentication

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Deserialization and serialization tests for [ApiKey].
 */
class ApiKeyTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullApiKey() = ApiKey(apiKey = "f1db798c98df4bcf83b538175893bbf0")

    /** Verifies that [ApiKey.apiKey] is mapped correctly from the `apiKey` JSON field. */
    @Test
    fun `ApiKey apiKey field is mapped correctly from JSON`() {
        val raw = """{ "apiKey": "test-api-key-12345" }"""
        val result = json.decodeFromString<ApiKey>(raw)
        assertNotNull(result)
        assertEquals("test-api-key-12345", result.apiKey)
    }

    /** Verifies that [ApiKey.apiKey] defaults to `null` when the `apiKey` key is absent from the payload. */
    @Test
    fun `ApiKey apiKey defaults to null when absent from JSON`() {
        val raw = """{}"""
        val result = json.decodeFromString<ApiKey>(raw)
        assertNotNull(result)
        assertNull(result.apiKey)
    }

    @Test
    fun `ApiKey round-trips when fully populated`() {
        val original = fullApiKey()
        assertEquals(original, jsonSkipDefaults.decodeFromString<ApiKey>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<ApiKey>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `ApiKey round-trips a minimal instance`() {
        val original = ApiKey()
        assertEquals(original, jsonSkipDefaults.decodeFromString<ApiKey>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<ApiKey>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `ApiKey populated and default instances are not equal`() {
        assertNotEquals(fullApiKey(), ApiKey())
    }
}
