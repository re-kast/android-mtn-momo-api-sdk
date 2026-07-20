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
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ProviderCallBackHostTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullProviderCallBackHost() = ProviderCallBackHost(
        providerCallbackHost = "https://callback.example.com/momo"
    )

    /** Verifies that [ProviderCallBackHost.providerCallbackHost] is mapped correctly from JSON. */
    @Test
    fun `ProviderCallBackHost fields are mapped correctly from JSON`() {
        val raw = """{ "providerCallbackHost": "https://example.com/callback" }"""
        val result = json.decodeFromString<ProviderCallBackHost>(raw)
        assertNotNull(result)
        assertEquals("https://example.com/callback", result.providerCallbackHost)
    }

    /**
     * Verifies that [ProviderCallBackHost.providerCallbackHost] defaults to `null` when the key
     * is absent, confirming the field is genuinely nullable.
     */
    @Test
    fun `ProviderCallBackHost providerCallbackHost defaults to null when absent`() {
        val raw = """{}"""
        val result = json.decodeFromString<ProviderCallBackHost>(raw)
        assertNotNull(result)
        assertNull(result.providerCallbackHost)
    }

    @Test
    fun `ProviderCallBackHost round-trips when fully populated`() {
        val original = fullProviderCallBackHost()
        assertEquals(original, jsonSkipDefaults.decodeFromString<ProviderCallBackHost>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<ProviderCallBackHost>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `ProviderCallBackHost round-trips a minimal instance`() {
        val original = ProviderCallBackHost()
        assertEquals(original, jsonSkipDefaults.decodeFromString<ProviderCallBackHost>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<ProviderCallBackHost>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `ProviderCallBackHost populated and default instances are not equal`() {
        assertNotEquals(fullProviderCallBackHost(), ProviderCallBackHost())
    }
}
