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
 * Deserialization and serialization tests for [Oauth2AccessToken].
 */
class Oauth2AccessTokenTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullOauth2AccessToken() = Oauth2AccessToken(
        accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9",
        tokenType = "Bearer",
        expiresIn = 3600,
        scope = "profile",
        refreshToken = "def50200a1b2c3d4e5f6",
        refreshTokenExpiredIn = 86400
    )

    /** Verifies that all [Oauth2AccessToken] fields are mapped correctly from their JSON equivalents. */
    @Test
    fun `Oauth2AccessToken fields are mapped correctly from JSON`() {
        val raw = """
            {
              "access_token": "oauth2-access-token-xyz",
              "token_type": "Bearer",
              "expires_in": 3600,
              "scope": "collection disbursement",
              "refresh_token": "oauth2-refresh-token-xyz",
              "refresh_token_expired_in": 86400
            }
        """.trimIndent()
        val result = json.decodeFromString<Oauth2AccessToken>(raw)
        assertNotNull(result)
        assertEquals("oauth2-access-token-xyz", result.accessToken)
        assertEquals("Bearer", result.tokenType)
        assertEquals(3600, result.expiresIn)
        assertEquals("collection disbursement", result.scope)
        assertEquals("oauth2-refresh-token-xyz", result.refreshToken)
        assertEquals(86400, result.refreshTokenExpiredIn)
    }

    /**
     * Verifies that an [Oauth2AccessToken] deserializes when the optional `scope`,
     * `refresh_token` and `refresh_token_expired_in` fields are absent from the response.
     *
     * Regression test for a [kotlinx.serialization.MissingFieldException] thrown during OAuth2
     * token refresh when the MTN MoMo token endpoint omits these fields.
     */
    @Test
    fun `Oauth2AccessToken deserializes when optional fields are missing`() {
        val raw = """
            {
              "access_token": "oauth2-access-token-only",
              "token_type": "Bearer",
              "expires_in": 3600
            }
        """.trimIndent()
        val result = json.decodeFromString<Oauth2AccessToken>(raw)
        assertNotNull(result)
        assertEquals("oauth2-access-token-only", result.accessToken)
        assertEquals("Bearer", result.tokenType)
        assertEquals(3600, result.expiresIn)
        assertNull(result.scope)
        assertNull(result.refreshToken)
        assertNull(result.refreshTokenExpiredIn)
    }

    /**
     * Verifies that [Oauth2AccessToken.expiresIn] is deserialized as [Int],
     * confirming numeric mapping of `expires_in`.
     */
    @Test
    fun `Oauth2AccessToken expires_in is deserialized as Int`() {
        val raw = """
            {
              "access_token": "token-def",
              "token_type": "Bearer",
              "expires_in": 1800,
              "scope": "collection",
              "refresh_token": "refresh-def",
              "refresh_token_expired_in": 43200
            }
        """.trimIndent()
        val result = json.decodeFromString<Oauth2AccessToken>(raw)
        assertEquals(1800, result.expiresIn)
        assertEquals(1800::class, result.expiresIn::class)
    }

    /**
     * Verifies that [Oauth2AccessToken.refreshTokenExpiredIn] is deserialized as [Int],
     * confirming numeric mapping of `refresh_token_expired_in`.
     */
    @Test
    fun `Oauth2AccessToken refresh_token_expired_in is deserialized as Int`() {
        val raw = """
            {
              "access_token": "token-ghi",
              "token_type": "Bearer",
              "expires_in": 3600,
              "scope": "disbursement",
              "refresh_token": "refresh-ghi",
              "refresh_token_expired_in": 43200
            }
        """.trimIndent()
        val result = json.decodeFromString<Oauth2AccessToken>(raw)
        assertEquals(43200, result.refreshTokenExpiredIn)
        assertEquals(43200::class, result.refreshTokenExpiredIn!!::class)
    }

    @Test
    fun `Oauth2AccessToken round-trips when fully populated`() {
        val original = fullOauth2AccessToken()
        assertEquals(original, jsonSkipDefaults.decodeFromString<Oauth2AccessToken>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<Oauth2AccessToken>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `Oauth2AccessToken round-trips a minimal instance`() {
        val original = Oauth2AccessToken(
            accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9",
            tokenType = "Bearer",
            expiresIn = 3600
        )
        assertEquals(original, jsonSkipDefaults.decodeFromString<Oauth2AccessToken>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<Oauth2AccessToken>(jsonWithDefaults.encodeToString(original)))
    }
}
