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
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Deserialization tests for [BasicUserInfo].
 *
 * The critical invariant: the MTN MOMO `getBasicUserInfo` endpoint returns JSON that
 * does NOT include a `display_updated_at` field. [BasicUserInfo.displayUpdatedAt] is a
 * locally-computed value annotated [@Transient] so it must be excluded from
 * deserialization entirely — a missing key must never cause a failure.
 */
class BasicUserInfoTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullBasicUserInfo() = BasicUserInfo(
        sub = "user-sub-123",
        name = "John Doe",
        givenName = "John",
        familyName = "Doe",
        birthDate = "1990-01-15",
        locale = "en_US",
        gender = "male",
        updatedAt = 1700000000
    )

    /** Realistic API payload — no `display_updated_at` key. */
    private val apiJson = """
        {
          "sub": "sub-123",
          "name": "Jane Doe",
          "given_name": "Jane",
          "family_name": "Doe",
          "birthdate": "1985-06-15",
          "locale": "en_UG",
          "gender": "F",
          "updated_at": 1580000000
        }
    """.trimIndent()

    /** Verifies the exact error that was fixed: JSON without display_updated_at must not throw. */
    @Test
    fun `deserializes API JSON that does not include display_updated_at`() {
        val result = json.decodeFromString<BasicUserInfo>(apiJson)
        assertNotNull(result)
    }

    /** Verifies displayUpdatedAt is excluded from deserialization and keeps its default value. */
    @Test
    fun `displayUpdatedAt defaults to empty string after deserialization`() {
        val result = json.decodeFromString<BasicUserInfo>(apiJson)
        assertEquals("", result.displayUpdatedAt)
    }

    /** Verifies displayUpdatedAt can be set after deserialization for display formatting. */
    @Test
    fun `displayUpdatedAt can be assigned after deserialization`() {
        val result = json.decodeFromString<BasicUserInfo>(apiJson)
        result.displayUpdatedAt = "09 Sep 2020"
        assertEquals("09 Sep 2020", result.displayUpdatedAt)
    }

    /** Verifies all API-mapped fields are read from the correct JSON keys. */
    @Test
    fun `all JSON fields are mapped to the correct properties`() {
        val result = json.decodeFromString<BasicUserInfo>(apiJson)
        assertEquals("sub-123", result.sub)
        assertEquals("Jane Doe", result.name)
        assertEquals("Jane", result.givenName)
        assertEquals("Doe", result.familyName)
        assertEquals("1985-06-15", result.birthDate)
        assertEquals("en_UG", result.locale)
        assertEquals("F", result.gender)
        assertEquals(1580000000, result.updatedAt)
    }

    /** Verifies updated_at is deserialized as an Int (not a String). */
    @Test
    fun `updated_at is deserialized as Int`() {
        val result = json.decodeFromString<BasicUserInfo>(apiJson)
        assertEquals(1580000000, result.updatedAt)
    }

    /** Verifies that extra unknown fields in the API response are safely ignored. */
    @Test
    fun `JSON with an unexpected extra field is tolerated`() {
        val jsonWithExtra = """
            {
              "sub": "s",
              "name": "n",
              "given_name": "g",
              "family_name": "f",
              "birthdate": "2000-01-01",
              "locale": "en",
              "gender": "M",
              "updated_at": 0,
              "unexpected_field": "ignored"
            }
        """.trimIndent()
        val result = json.decodeFromString<BasicUserInfo>(jsonWithExtra)
        assertNotNull(result)
        assertEquals("s", result.sub)
    }

    @Test
    fun `BasicUserInfo round-trips when fully populated`() {
        val original = fullBasicUserInfo()
        assertEquals(original, jsonSkipDefaults.decodeFromString<BasicUserInfo>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<BasicUserInfo>(jsonWithDefaults.encodeToString(original)))
    }
}
