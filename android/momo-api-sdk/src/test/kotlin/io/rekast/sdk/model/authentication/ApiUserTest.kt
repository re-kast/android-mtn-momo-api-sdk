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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Deserialization and serialization tests for [ApiUser].
 */
class ApiUserTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullApiUser() = ApiUser(
        providerCallbackHost = "https://webhook.site/callback",
        targetEnvironment = "sandbox"
    )

    /** Verifies that both [ApiUser] fields are mapped correctly when both are present in the JSON. */
    @Test
    fun `ApiUser fields are mapped correctly from JSON`() {
        val raw = """
            {
              "providerCallbackHost": "https://example.com/momo/callback",
              "targetEnvironment": "sandbox"
            }
        """.trimIndent()
        val result = json.decodeFromString<ApiUser>(raw)
        assertNotNull(result)
        assertEquals("https://example.com/momo/callback", result.providerCallbackHost)
        assertEquals("sandbox", result.targetEnvironment)
    }

    /**
     * Verifies that [ApiUser.providerCallbackHost] defaults to `null` when absent,
     * confirming the field is independently nullable.
     */
    @Test
    fun `ApiUser providerCallbackHost defaults to null when absent from JSON`() {
        val raw = """{ "targetEnvironment": "production" }"""
        val result = json.decodeFromString<ApiUser>(raw)
        assertNotNull(result)
        assertNull(result.providerCallbackHost)
        assertEquals("production", result.targetEnvironment)
    }

    /**
     * Verifies that [ApiUser.targetEnvironment] defaults to `null` when absent,
     * confirming the field is independently nullable.
     */
    @Test
    fun `ApiUser targetEnvironment defaults to null when absent from JSON`() {
        val raw = """{ "providerCallbackHost": "https://example.com/callback" }"""
        val result = json.decodeFromString<ApiUser>(raw)
        assertNotNull(result)
        assertEquals("https://example.com/callback", result.providerCallbackHost)
        assertNull(result.targetEnvironment)
    }

    /**
     * Verifies that all nullable [ApiUser] fields default to `null` when the JSON payload
     * is empty, confirming there are no accidental non-null defaults.
     */
    @Test
    fun `ApiUser all nullable fields default to null when JSON is empty`() {
        val raw = """{}"""
        val result = json.decodeFromString<ApiUser>(raw)
        assertNotNull(result)
        assertNull(result.providerCallbackHost)
        assertNull(result.targetEnvironment)
    }

    @Test
    fun `ApiUser round-trips when fully populated`() {
        val original = fullApiUser()
        assertEquals(original, jsonSkipDefaults.decodeFromString<ApiUser>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<ApiUser>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `ApiUser round-trips a minimal instance`() {
        val original = ApiUser()
        assertEquals(original, jsonSkipDefaults.decodeFromString<ApiUser>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<ApiUser>(jsonWithDefaults.encodeToString(original)))
    }
}
