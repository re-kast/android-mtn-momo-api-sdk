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

class BackChannelAuthorizeTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullBackChannelAuthorize() = BackChannelAuthorize(
        authReqId = "auth-req-12345",
        interval = 5,
        expiresIn = 3600
    )

    /** Verifies that [BackChannelAuthorize] fields are mapped correctly from the CIBA response keys. */
    @Test
    fun `BackChannelAuthorize fields are mapped correctly from JSON`() {
        val raw = """
            {
              "auth_req_id": "auth-req-abc123",
              "interval": 5,
              "expires_in": 120
            }
        """.trimIndent()
        val result = json.decodeFromString<BackChannelAuthorize>(raw)
        assertNotNull(result)
        assertEquals("auth-req-abc123", result.authReqId)
        assertEquals(5, result.interval)
        assertEquals(120, result.expiresIn)
    }

    @Test
    fun `BackChannelAuthorize round-trips when fully populated`() {
        val original = fullBackChannelAuthorize()
        assertEquals(original, jsonSkipDefaults.decodeFromString<BackChannelAuthorize>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<BackChannelAuthorize>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `BackChannelAuthorize decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<BackChannelAuthorize>("{}")
    }
}
