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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Deserialization tests for [UserInfoWithConsent].
 *
 * The MTN MOMO `getUserInfoWithConsent` endpoint returns a **flat** JSON object — all
 * fields at the top level, not nested under a `userBasicInfo` key. [UserInfoWithConsent]
 * was restructured to match this shape and all previously-required fields were made
 * nullable so that a sparse API response never causes a deserialization failure.
 */
class UserInfoWithConsentTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullUserInfoWithConsent() = UserInfoWithConsent(
        sub = "user-sub-1",
        name = "Jane Doe",
        givenName = "Jane",
        familyName = "Doe",
        birthDate = "1990-01-01",
        locale = "en-US",
        gender = "female",
        updatedAt = 1620000000,
        status = "ACTIVE",
        middleName = "Marie",
        email = "jane.doe@example.com",
        emailVerified = true,
        phonenumber = "256770000000",
        phoneNumberVerified = true,
        address = Address(
            formatted = "12 Main St\nNairobi",
            streetAddress = "12 Main St",
            postalCode = "00100",
            locality = "Nairobi",
            region = "Nairobi County",
            country = "Kenya"
        ),
        creditScore = 750,
        active = true,
        countryOfBirth = "Kenya",
        regionOfBirth = "Nairobi",
        cityOfBirth = "Nairobi",
        occupation = "Engineer",
        employerName = "Acme Corp",
        identificationType = "PASS",
        identificationValue = "A1234567"
    )

    private val fullJson = """
        {
          "sub": "sub-456",
          "name": "John Doe",
          "given_name": "John",
          "family_name": "Doe",
          "birthdate": "1990-01-01",
          "locale": "en_UG",
          "gender": "M",
          "updated_at": 1580000000,
          "status": "ACTIVE",
          "middle_name": "Middle",
          "email": "john@example.com",
          "email_verified": true,
          "phone_number": "256700000000",
          "phone_number_verified": true,
          "address": {
            "formatted": "Street 17\n123 45 Karlskrona\nBlekinge\nSweden",
            "street_address": "Street 17",
            "postal_code": "123 45",
            "locality": "Karlskrona",
            "region": "Blekinge",
            "country": "Sweden"
          },
          "credit_score": 700,
          "active": true,
          "country_of_birth": "UG",
          "region_of_birth": "Central",
          "city_of_birth": "Kampala",
          "occupation": "Engineer",
          "employer_name": "ACME",
          "identification_type": "PASSPORT",
          "identification_value": "A1234567"
        }
    """.trimIndent()

    /** Only sub and name are truly required by the endpoint. */
    private val minimalJson = """
        {
          "sub": "sub-789",
          "name": "Alice"
        }
    """.trimIndent()

    /** Verifies the full payload deserializes without throwing. */
    @Test
    fun `deserializes full JSON with all fields`() {
        val result = json.decodeFromString<UserInfoWithConsent>(fullJson)
        assertNotNull(result)
    }

    /** Verifies every field in the full payload is mapped to the correct property. */
    @Test
    fun `all fields in full JSON are mapped correctly`() {
        val result = json.decodeFromString<UserInfoWithConsent>(fullJson)
        assertEquals("sub-456", result.sub)
        assertEquals("John Doe", result.name)
        assertEquals("John", result.givenName)
        assertEquals("Doe", result.familyName)
        assertEquals("1990-01-01", result.birthDate)
        assertEquals("en_UG", result.locale)
        assertEquals("M", result.gender)
        assertEquals(1580000000, result.updatedAt)
        assertEquals("ACTIVE", result.status)
        assertEquals("Middle", result.middleName)
        assertEquals("john@example.com", result.email)
        assertEquals(true, result.emailVerified)
        assertEquals("256700000000", result.phonenumber)
        assertEquals(true, result.phoneNumberVerified)
        assertEquals(
            Address(
                formatted = "Street 17\n123 45 Karlskrona\nBlekinge\nSweden",
                streetAddress = "Street 17",
                postalCode = "123 45",
                locality = "Karlskrona",
                region = "Blekinge",
                country = "Sweden"
            ),
            result.address
        )
        assertEquals(700, result.creditScore)
        assertEquals(true, result.active)
        assertEquals("UG", result.countryOfBirth)
        assertEquals("Central", result.regionOfBirth)
        assertEquals("Kampala", result.cityOfBirth)
        assertEquals("Engineer", result.occupation)
        assertEquals("ACME", result.employerName)
        assertEquals("PASSPORT", result.identificationType)
        assertEquals("A1234567", result.identificationValue)
    }

    /** Verifies a sparse response (only required fields) deserializes successfully. */
    @Test
    fun `deserializes minimal JSON with only required fields`() {
        val result = json.decodeFromString<UserInfoWithConsent>(minimalJson)
        assertNotNull(result)
        assertEquals("sub-789", result.sub)
        assertEquals("Alice", result.name)
    }

    /** Verifies every optional field defaults to null when absent from the JSON. */
    @Test
    fun `optional fields default to null when absent`() {
        val result = json.decodeFromString<UserInfoWithConsent>(minimalJson)
        assertNull(result.givenName)
        assertNull(result.familyName)
        assertNull(result.birthDate)
        assertNull(result.locale)
        assertNull(result.gender)
        assertNull(result.updatedAt)
        assertNull(result.status)
        assertNull(result.middleName)
        assertNull(result.email)
        assertNull(result.emailVerified)
        assertNull(result.phonenumber)
        assertNull(result.phoneNumberVerified)
        assertNull(result.address)
        assertNull(result.creditScore)
        assertNull(result.active)
        assertNull(result.countryOfBirth)
        assertNull(result.regionOfBirth)
        assertNull(result.cityOfBirth)
        assertNull(result.occupation)
        assertNull(result.employerName)
        assertNull(result.identificationType)
        assertNull(result.identificationValue)
    }

    /** Verifies updated_at is deserialized as Int (not String). */
    @Test
    fun `updated_at is deserialized as Int`() {
        val result = json.decodeFromString<UserInfoWithConsent>(fullJson)
        assertTrue(result.updatedAt is Int)
        assertEquals(1580000000, result.updatedAt)
    }

    /**
     * Regression test: the API returns `address` as a nested JSON object, `credit_score` as a
     * number, and `active` as a boolean. Deserializing these into their correct types must not throw.
     */
    @Test
    fun `address is deserialized as a nested object and credit_score and active as their JSON types`() {
        val result = json.decodeFromString<UserInfoWithConsent>(fullJson)
        assertNotNull(result.address)
        assertEquals("Street 17", result.address?.streetAddress)
        assertEquals("Karlskrona", result.address?.locality)
        assertEquals("Sweden", result.address?.country)
        assertTrue(result.creditScore is Int)
        assertTrue(result.active is Boolean)
    }

    @Test
    fun `UserInfoWithConsent round-trips when fully populated`() {
        val original = fullUserInfoWithConsent()
        assertEquals(original, jsonSkipDefaults.decodeFromString<UserInfoWithConsent>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<UserInfoWithConsent>(jsonWithDefaults.encodeToString(original)))
    }

    @Test
    fun `UserInfoWithConsent round-trips a minimal instance`() {
        val original = UserInfoWithConsent(
            sub = "user-sub-1",
            name = "Jane Doe"
        )
        assertEquals(original, jsonSkipDefaults.decodeFromString<UserInfoWithConsent>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<UserInfoWithConsent>(jsonWithDefaults.encodeToString(original)))
    }
}
