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
package io.rekast.sdk.sample.ui.components.accountdetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.Address
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [UserInfoWithConsentComponent], run on the JVM via Robolectric.
 *
 * Covers the null branch (only the card title renders, every optional row and every conditional
 * block is skipped), the fully-populated branch (all four conditional blocks — address, birthplace,
 * employment, identification — are true), and the `Address.readable()` `formatted` fallback used
 * when the structured address parts are all blank.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// A tall viewport so the whole (non-scrolling) profile card fits on screen and every row is
// reported as displayed rather than clipped below the fold.
@Config(sdk = [34], qualifiers = "w411dp-h3000dp")
class UserInfoWithConsentComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(info: UserInfoWithConsent?) {
        composeRule.setContent {
            AppTheme {
                UserInfoWithConsentComponent(userInfoWithConsent = MutableLiveData(info))
            }
        }
    }

    /** With null LiveData, only the card title renders; all optional rows/blocks are skipped. */
    @Test
    fun `renders only title when data is null`() {
        setContent(null)
        composeRule.onNodeWithText("Verified Profile").assertIsDisplayed()
        // None of the conditional blocks execute, so their labels are absent.
        composeRule.onNodeWithText("Address").assertDoesNotExist()
        composeRule.onNodeWithText("Occupation").assertDoesNotExist()
        composeRule.onNodeWithText("ID type").assertDoesNotExist()
        composeRule.onNodeWithText("Country of birth").assertDoesNotExist()
    }

    /** A fully-populated model renders every field and enters all four conditional blocks. */
    @Test
    fun `renders all sections when data is present`() {
        setContent(
            UserInfoWithConsent(
                sub = "sub-9",
                name = "Sand Box",
                givenName = "Sand",
                familyName = "Box",
                middleName = "Mid",
                birthDate = "1976-08-13",
                gender = "MALE",
                email = "email@domain.test",
                phonenumber = "46123456789",
                locale = "sv_SE",
                address = Address(
                    streetAddress = "Street 17",
                    postalCode = "123 45",
                    locality = "Karlskrona",
                    region = "Blekinge",
                    country = "Sweden"
                ),
                creditScore = 123,
                countryOfBirth = "Sweden",
                regionOfBirth = "Blekinge",
                cityOfBirth = "Karlskrona",
                occupation = "Manager",
                employerName = "Ericsson",
                identificationType = "PASS",
                identificationValue = "S1234567"
            )
        )
        composeRule.onNodeWithText("Verified Profile").assertIsDisplayed()

        // Basic identity / contact rows.
        composeRule.onNodeWithText("Sand").assertIsDisplayed()
        composeRule.onNodeWithText("Box").assertIsDisplayed()
        composeRule.onNodeWithText("Mid").assertIsDisplayed()
        composeRule.onNodeWithText("1976-08-13").assertIsDisplayed()
        composeRule.onNodeWithText("MALE").assertIsDisplayed()
        composeRule.onNodeWithText("email@domain.test").assertIsDisplayed()
        composeRule.onNodeWithText("46123456789").assertIsDisplayed()
        composeRule.onNodeWithText("sv_SE").assertIsDisplayed()

        // Address block (structured parts joined into a single line).
        composeRule.onNodeWithText("Address").assertIsDisplayed()
        composeRule.onNodeWithText("Street 17, Karlskrona, 123 45, Blekinge, Sweden").assertIsDisplayed()

        // Birthplace block.
        composeRule.onNodeWithText("Country of birth").assertIsDisplayed()
        composeRule.onNodeWithText("Sweden").assertIsDisplayed()

        // Employment block.
        composeRule.onNodeWithText("Occupation").assertIsDisplayed()
        composeRule.onNodeWithText("Manager").assertIsDisplayed()
        composeRule.onNodeWithText("Employer").assertIsDisplayed()
        composeRule.onNodeWithText("Ericsson").assertIsDisplayed()

        // Identification block.
        composeRule.onNodeWithText("ID type").assertIsDisplayed()
        composeRule.onNodeWithText("PASS").assertIsDisplayed()
        composeRule.onNodeWithText("ID number").assertIsDisplayed()
        composeRule.onNodeWithText("S1234567").assertIsDisplayed()
        composeRule.onNodeWithText("Credit score").assertIsDisplayed()
        composeRule.onNodeWithText("123").assertIsDisplayed()

        // Trailing subject-ID row.
        composeRule.onNodeWithText("Subject ID").assertIsDisplayed()
        composeRule.onNodeWithText("sub-9").assertIsDisplayed()
    }

    /**
     * When the structured address parts are all blank, `Address.readable()` falls back to the
     * `formatted` string, replacing newlines with commas.
     */
    @Test
    fun `renders formatted address fallback`() {
        setContent(
            UserInfoWithConsent(
                sub = "sub-1",
                name = "Sand Box",
                address = Address(formatted = "Line1\nLine2")
            )
        )
        composeRule.onNodeWithText("Address").assertIsDisplayed()
        composeRule.onNodeWithText("Line1, Line2").assertIsDisplayed()
    }

    /**
     * Birthplace block entered via the *second* operand: `countryOfBirth` is blank (so the first
     * `!isNullOrBlank()` is false and its blank-string arm is exercised) while `regionOfBirth` is
     * set, driving the middle term of the `hasBirthplace` OR chain true.
     */
    @Test
    fun `renders birthplace when only region present`() {
        setContent(
            UserInfoWithConsent(
                sub = "sub-2",
                name = "Sand Box",
                countryOfBirth = "",
                regionOfBirth = "Blekinge",
                cityOfBirth = null
            )
        )
        composeRule.onNodeWithText("Region of birth").assertIsDisplayed()
        composeRule.onNodeWithText("Blekinge").assertIsDisplayed()
        // First operand was blank, so the country row is skipped.
        composeRule.onNodeWithText("Country of birth").assertDoesNotExist()
    }

    /**
     * Birthplace block entered via the *third* operand: both `countryOfBirth` (null) and
     * `regionOfBirth` (blank) are false, so the final `cityOfBirth` term drives the OR chain true.
     */
    @Test
    fun `renders birthplace when only city present`() {
        setContent(
            UserInfoWithConsent(
                sub = "sub-3",
                name = "Sand Box",
                countryOfBirth = null,
                regionOfBirth = "",
                cityOfBirth = "Karlskrona"
            )
        )
        composeRule.onNodeWithText("City of birth").assertIsDisplayed()
        composeRule.onNodeWithText("Karlskrona").assertIsDisplayed()
        composeRule.onNodeWithText("Country of birth").assertDoesNotExist()
        composeRule.onNodeWithText("Region of birth").assertDoesNotExist()
    }

    /**
     * Employment block entered via the *second* operand: `occupation` is blank (first term false)
     * while `employerName` is set, driving the `hasEmployment` OR chain true through its tail.
     */
    @Test
    fun `renders employment when only employer present`() {
        setContent(
            UserInfoWithConsent(
                sub = "sub-4",
                name = "Sand Box",
                occupation = "",
                employerName = "Ericsson"
            )
        )
        composeRule.onNodeWithText("Employer").assertIsDisplayed()
        composeRule.onNodeWithText("Ericsson").assertIsDisplayed()
        composeRule.onNodeWithText("Occupation").assertDoesNotExist()
    }

    /**
     * Identification block entered via the *second* operand: `identificationType` is blank (first
     * term false), `identificationValue` is set, and `creditScore` stays null.
     */
    @Test
    fun `renders identification when only value present`() {
        setContent(
            UserInfoWithConsent(
                sub = "sub-5",
                name = "Sand Box",
                identificationType = "",
                identificationValue = "S1234567",
                creditScore = null
            )
        )
        composeRule.onNodeWithText("ID number").assertIsDisplayed()
        composeRule.onNodeWithText("S1234567").assertIsDisplayed()
        composeRule.onNodeWithText("ID type").assertDoesNotExist()
        composeRule.onNodeWithText("Credit score").assertDoesNotExist()
    }

    /**
     * Identification block entered via the *third* operand: both `identificationType` and
     * `identificationValue` are blank (first two terms false), so the `creditScore != null` term
     * drives the OR chain true.
     */
    @Test
    fun `renders identification when only credit score present`() {
        setContent(
            UserInfoWithConsent(
                sub = "sub-6",
                name = "Sand Box",
                identificationType = "",
                identificationValue = "",
                creditScore = 777
            )
        )
        composeRule.onNodeWithText("Credit score").assertIsDisplayed()
        composeRule.onNodeWithText("777").assertIsDisplayed()
        composeRule.onNodeWithText("ID type").assertDoesNotExist()
        composeRule.onNodeWithText("ID number").assertDoesNotExist()
    }

    /**
     * A non-null [Address] whose structured parts are all blank *and* whose `formatted` is null:
     * `Address.readable()` runs, the `joinToString` yields blank so `ifBlank` fires, and the
     * `formatted?.replace(...)` safe-call takes its null arm, collapsing to an empty string. The
     * address block is therefore skipped.
     */
    @Test
    fun `skips address when structured parts blank and formatted null`() {
        setContent(
            UserInfoWithConsent(
                sub = "sub-7",
                name = "Sand Box",
                address = Address(
                    streetAddress = "",
                    locality = "",
                    postalCode = "",
                    region = "",
                    country = "",
                    formatted = null
                )
            )
        )
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Verified Profile").assertIsDisplayed()
        composeRule.onNodeWithText("Address").assertDoesNotExist()
    }
}
