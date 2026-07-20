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

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Deserialization and serialization tests for [AccessToken].
 */
class AccessTokenTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullAccessToken() = AccessToken(
        accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9",
        tokenType = "Bearer",
        expiresIn = 3600
    )

    /** Verifies that all [AccessToken] fields are mapped correctly from their JSON equivalents. */
    @Test
    fun `AccessToken fields are mapped correctly from JSON`() {
        val raw = """
            {
              "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.payload.sig",
              "token_type": "Bearer",
              "expires_in": 3600
            }
        """.trimIndent()
        val result = json.decodeFromString<AccessToken>(raw)
        assertNotNull(result)
        assertEquals("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.payload.sig", result.accessToken)
        assertEquals("Bearer", result.tokenType)
        assertEquals(3600, result.expiresIn)
    }

    /**
     * Verifies that [AccessToken.expiresIn] is deserialized as [Int], not [String],
     * confirming the `@SerialName("expires_in")` annotation targets the numeric JSON field.
     */
    @Test
    fun `AccessToken expires_in is deserialized as Int`() {
        val raw = """
            {
              "access_token": "token-abc",
              "token_type": "Bearer",
              "expires_in": 7200
            }
        """.trimIndent()
        val result = json.decodeFromString<AccessToken>(raw)
        assertEquals(7200, result.expiresIn)
        assertEquals(7200::class, result.expiresIn::class)
    }

    @Test
    fun `AccessToken round-trips when fully populated`() {
        val original = fullAccessToken()
        assertEquals(original, jsonSkipDefaults.decodeFromString<AccessToken>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<AccessToken>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `AccessToken decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<AccessToken>("{}")
    }
}
