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
import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [AccountBalanceComponent], run on the JVM via Robolectric.
 *
 * Covers the null branch (the amount falls back to the "—" placeholder and the currency text is
 * omitted) and the populated branch (amount and currency both render via the `currency?.let { }`
 * block).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class AccountBalanceComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(balance: AccountBalance?) {
        composeRule.setContent {
            AppTheme {
                AccountBalanceComponent(accountBalance = MutableLiveData(balance))
            }
        }
    }

    /** With null LiveData, the amount shows the em-dash placeholder and no currency is drawn. */
    @Test
    fun `renders placeholder amount when balance is null`() {
        setContent(null)
        composeRule.onNodeWithText("Account Balance").assertIsDisplayed()
        composeRule.onNodeWithText("Available balance").assertIsDisplayed()
        composeRule.onNodeWithText("—").assertIsDisplayed()
        composeRule.onNodeWithText("EUR").assertDoesNotExist()
    }

    /** With a populated balance, the amount and the currency label both render. */
    @Test
    fun `renders amount and currency when balance is present`() {
        setContent(AccountBalance(availableBalance = "1500.00", currency = "EUR"))
        composeRule.onNodeWithText("Account Balance").assertIsDisplayed()
        composeRule.onNodeWithText("Available balance").assertIsDisplayed()
        composeRule.onNodeWithText("1500.00").assertIsDisplayed()
        composeRule.onNodeWithText("EUR").assertIsDisplayed()
    }
}
