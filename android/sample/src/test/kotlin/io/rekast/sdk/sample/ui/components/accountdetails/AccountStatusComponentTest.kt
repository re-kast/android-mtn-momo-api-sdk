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
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [AccountStatusComponent], run on the JVM via Robolectric.
 *
 * Covers all three states of the `isActive = status?.result == true` decision: null status,
 * `result = false`, and `result = true` — exercising both the active and inactive pill branches.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class AccountStatusComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(status: AccountHolderStatus?) {
        composeRule.setContent {
            AppTheme {
                AccountStatusComponent(accountHolderStatus = MutableLiveData(status))
            }
        }
    }

    /** With null LiveData, the status is treated as inactive and shows the "In Active" pill. */
    @Test
    fun `renders inactive pill when status is null`() {
        setContent(null)
        composeRule.onNodeWithText("Account Status").assertIsDisplayed()
        composeRule.onNodeWithText("Status").assertIsDisplayed()
        composeRule.onNodeWithText("In Active").assertIsDisplayed()
    }

    /** With `result = false`, the inactive pill renders (non-null status, false branch). */
    @Test
    fun `renders inactive pill when result is false`() {
        setContent(AccountHolderStatus(result = false))
        composeRule.onNodeWithText("In Active").assertIsDisplayed()
    }

    /** With `result = true`, the active pill renders. */
    @Test
    fun `renders active pill when result is true`() {
        setContent(AccountHolderStatus(result = true))
        composeRule.onNodeWithText("Account Status").assertIsDisplayed()
        composeRule.onNodeWithText("Status").assertIsDisplayed()
        composeRule.onNodeWithText("Active").assertIsDisplayed()
    }
}
