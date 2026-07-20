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
package io.rekast.sdk.sample.views.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for the Home screen ([MainScreen]), run on the JVM via Robolectric.
 *
 * Covers the populated profile header, the empty-state placeholder, and the progress-bar branch.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val snackFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow()

    private fun setScreen(showProgressBar: Boolean = false, basic: BasicUserInfo? = null, consent: UserInfoWithConsent? = null, balance: AccountBalance? = null) {
        composeRule.setContent {
            AppTheme {
                MainScreen(
                    navController = null,
                    snackStateFlow = snackFlow,
                    showProgressBar = showProgressBar,
                    basicUserInfo = MutableLiveData(basic),
                    userInfoWithConsent = MutableLiveData(consent),
                    accountHolderStatus = MutableLiveData<AccountHolderStatus?>(null),
                    accountBalance = MutableLiveData(balance)
                )
            }
        }
    }

    /** The profile header shows the verified profile's name when consent data is present. */
    @Test
    fun `renders profile name when data present`() {
        setScreen(consent = UserInfoWithConsent(sub = "sub-1", name = "Sand Box"))
        composeRule.onNodeWithText("Sand Box").assertIsDisplayed()
        composeRule.onNodeWithText("Verified").assertIsDisplayed()
    }

    /** The profile header falls back to a placeholder when no data has loaded. */
    @Test
    fun `renders placeholder when empty`() {
        setScreen()
        // "—" appears in both the avatar initials and the name; assert at least one is shown.
        composeRule.onAllNodesWithText("—").onFirst().assertIsDisplayed()
    }

    /** When the progress bar is shown, the data cards are not composed. */
    @Test
    fun `hides data cards while loading`() {
        setScreen(showProgressBar = true, consent = UserInfoWithConsent(sub = "sub-1", name = "Sand Box"))
        composeRule.onNodeWithText("Sand Box").assertDoesNotExist()
    }
}
