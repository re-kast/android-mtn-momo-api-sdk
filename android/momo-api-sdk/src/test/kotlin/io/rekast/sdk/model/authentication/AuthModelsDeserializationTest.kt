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

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Deserialization tests for authentication model classes.
 *
 * Verifies that each model's `@SerialName` annotations map the MTN MOMO auth endpoint
 * JSON keys to the correct Kotlin properties, that numeric fields such as `expires_in`
 * are decoded as [Int] rather than [String], and that nullable fields default to null
 * when their keys are absent from the payload.
 */
class AuthModelsDeserializationTest {

    private val json = Json { ignoreUnknownKeys = true }

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
        assertEquals(43200::class, result.refreshTokenExpiredIn::class)
    }

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
}
