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
}
