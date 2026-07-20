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
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [ProfileHeaderComponent], run on the JVM via Robolectric.
 *
 * Exercises every branch of the name / secondary-line / verified-pill resolution:
 * - both LiveData null (placeholder name, "—" initials, no pill),
 * - consent present with name + email (verified pill, "SB" initials),
 * - consent present with email absent but phone number present (phone-number fallback),
 * - consent absent but basic info present (name and subject-ID fallback, no pill),
 * - consent with a blank name (initials fall back to "?").
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ProfileHeaderComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(basic: BasicUserInfo? = null, consent: UserInfoWithConsent? = null) {
        composeRule.setContent {
            AppTheme {
                ProfileHeaderComponent(
                    basicUserInfo = MutableLiveData(basic),
                    userInfoWithConsent = MutableLiveData(consent)
                )
            }
        }
    }

    private fun basicInfo(sub: String) = BasicUserInfo(
        sub = sub,
        name = "Sand Box",
        givenName = "Sand",
        familyName = "Box",
        birthDate = "1976-08-13",
        locale = "sv_SE",
        gender = "MALE",
        updatedAt = 0
    )

    /** With no data at all, the name and initials fall back to "—" and no verified pill shows. */
    @Test
    fun `renders placeholder when both sources are null`() {
        setContent()
        // "—" appears both as the name and as the avatar initials; assert at least one is shown.
        composeRule.onAllNodesWithText("—").onFirst().assertIsDisplayed()
        composeRule.onNodeWithText("Verified").assertDoesNotExist()
    }

    /** Consent data drives the name, the email secondary line, the "SB" initials, and the pill. */
    @Test
    fun `renders consent name email and verified pill`() {
        setContent(
            consent = UserInfoWithConsent(sub = "0", name = "Sand Box", email = "email@domain.test")
        )
        composeRule.onNodeWithText("Sand Box").assertIsDisplayed()
        composeRule.onNodeWithText("email@domain.test").assertIsDisplayed()
        composeRule.onNodeWithText("SB").assertIsDisplayed()
        composeRule.onNodeWithText("Verified").assertIsDisplayed()
    }

    /** When email is absent the secondary line falls back to the phone number. */
    @Test
    fun `renders phone number when email is absent`() {
        setContent(
            consent = UserInfoWithConsent(
                sub = "0",
                name = "Phone Only",
                email = null,
                phonenumber = "46700000000"
            )
        )
        composeRule.onNodeWithText("Phone Only").assertIsDisplayed()
        composeRule.onNodeWithText("46700000000").assertIsDisplayed()
        composeRule.onNodeWithText("Verified").assertIsDisplayed()
    }

    /** Without consent, the basic name and subject ID render and no verified pill shows. */
    @Test
    fun `renders basic info name and sub without pill`() {
        setContent(basic = basicInfo(sub = "basic-sub-1"))
        composeRule.onNodeWithText("Sand Box").assertIsDisplayed()
        composeRule.onNodeWithText("basic-sub-1").assertIsDisplayed()
        composeRule.onNodeWithText("Verified").assertDoesNotExist()
    }

    /** A blank consent name yields the "?" initials fallback. */
    @Test
    fun `renders question mark initials for blank name`() {
        setContent(
            consent = UserInfoWithConsent(sub = "0", name = "", email = "q@e.test")
        )
        composeRule.onNodeWithText("?").assertIsDisplayed()
        composeRule.onNodeWithText("q@e.test").assertIsDisplayed()
        composeRule.onNodeWithText("Verified").assertIsDisplayed()
    }

    /**
     * A blank (but non-null) secondary value takes the `takeIf { it.isNotBlank() }` false path, so
     * the secondary line is omitted while the name still renders.
     */
    @Test
    fun `omits secondary line when secondary is blank`() {
        setContent(
            consent = UserInfoWithConsent(sub = "0", name = "Blank Secondary", email = "", phonenumber = null)
        )
        composeRule.onNodeWithText("Blank Secondary").assertIsDisplayed()
        composeRule.onNodeWithText("Verified").assertIsDisplayed()
    }
}
