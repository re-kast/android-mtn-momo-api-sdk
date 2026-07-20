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
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [BasicUserInfoComponent], run on the JVM via Robolectric.
 *
 * Covers the empty-state branch (all info rows skipped because the LiveData is null) and the
 * fully-populated branch where every field — including the transient `displayUpdatedAt` — is
 * rendered.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class BasicUserInfoComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(info: BasicUserInfo?) {
        composeRule.setContent {
            AppTheme {
                BasicUserInfoComponent(basicUserInfo = MutableLiveData(info))
            }
        }
    }

    /** With null LiveData, only the card title renders; every value row is omitted. */
    @Test
    fun `renders only title when data is null`() {
        setContent(null)
        composeRule.onNodeWithText("Basic User Info").assertIsDisplayed()
        // No value rows are drawn, so their labels never appear.
        composeRule.onNodeWithText("Subject ID").assertDoesNotExist()
    }

    /** With a fully-populated model, every label and its value render. */
    @Test
    fun `renders all fields when data is present`() {
        setContent(
            BasicUserInfo(
                sub = "0",
                name = "Sand Box",
                givenName = "Sand",
                familyName = "Box",
                birthDate = "1976-08-13",
                locale = "sv_SE",
                gender = "MALE",
                updatedAt = 1784414651
            ).apply { displayUpdatedAt = "2026-07-18" }
        )
        composeRule.onNodeWithText("Basic User Info").assertIsDisplayed()
        composeRule.onNodeWithText("Name").assertIsDisplayed()
        composeRule.onNodeWithText("Sand Box").assertIsDisplayed()
        composeRule.onNodeWithText("Date of birth").assertIsDisplayed()
        composeRule.onNodeWithText("1976-08-13").assertIsDisplayed()
        composeRule.onNodeWithText("Gender").assertIsDisplayed()
        composeRule.onNodeWithText("MALE").assertIsDisplayed()
        composeRule.onNodeWithText("Updated at").assertIsDisplayed()
        composeRule.onNodeWithText("2026-07-18").assertIsDisplayed()
        composeRule.onNodeWithText("Locale").assertIsDisplayed()
        composeRule.onNodeWithText("sv_SE").assertIsDisplayed()
        composeRule.onNodeWithText("Subject ID").assertIsDisplayed()
        composeRule.onNodeWithText("0").assertIsDisplayed()
    }
}
