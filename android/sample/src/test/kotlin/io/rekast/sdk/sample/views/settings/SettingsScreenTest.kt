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
package io.rekast.sdk.sample.views.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import io.mockk.every
import io.mockk.mockk
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [SettingsScreen], run on the JVM via Robolectric.
 *
 * Covers the environment card rendering, the masked-keys show/hide toggle, and the destructive
 * clear-credentials action's callback.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class SettingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val config = SampleConfig(
        apiVersionV1 = "v1_0",
        apiVersionV2 = "v2_0",
        environment = "sandbox",
        providerCallbackHost = "localhost",
        apiUserId = "user-123",
        collectionPrimaryKey = "col-primary-1234",
        collectionSecondaryKey = "col-secondary",
        remittancePrimaryKey = "rem-primary",
        remittanceSecondaryKey = "rem-secondary",
        disbursementsPrimaryKey = "dis-primary",
        disbursementsSecondaryKey = "dis-secondary"
    )

    private val snackFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow()

    private fun viewModel(): SettingsScreenViewModel = mockk(relaxed = true) {
        every { config } returns this@SettingsScreenTest.config
        every { appInfo } returns SettingsScreenViewModel.AppInfo("1.2.3", "42", "io.rekast.sdk.sample")
        every { snackBarStateFlow } returns snackFlow
    }

    private fun setScreen(onClear: () -> Unit = {}) {
        val vm = viewModel()
        composeRule.setContent {
            AppTheme {
                SettingsScreen(navController = null, snackStateFlow = snackFlow, viewModel = vm, onClearCredentials = onClear)
            }
        }
    }

    /** The environment card renders the configured values. */
    @Test
    fun `renders environment values`() {
        setScreen()
        composeRule.onNodeWithText("sandbox").assertIsDisplayed()
        composeRule.onNodeWithText("user-123").assertIsDisplayed()
    }

    /** Product keys are masked until the Show toggle is tapped, then rendered in full. */
    @Test
    fun `product keys are masked until Show is tapped`() {
        setScreen()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Show"))
        composeRule.onNodeWithText("col-primary-1234").assertDoesNotExist()

        composeRule.onNodeWithText("Show").performClick()

        composeRule.onNodeWithText("col-primary-1234").assertIsDisplayed()
    }

    /** Tapping the danger-zone button invokes the clear-credentials callback. */
    @Test
    fun `clear credentials button invokes callback`() {
        var cleared = false
        setScreen(onClear = { cleared = true })

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Clear stored credentials"))
        composeRule.onNodeWithText("Clear stored credentials").performClick()

        assertTrue(cleared)
    }

    /**
     * With a null viewModel the screen takes the `?: return@MomoScaffold` branch (L69) and renders
     * only the scaffold chrome, without crashing. The non-null-config side is covered by the other
     * tests in this class.
     */
    @Test
    fun `renders with null viewModel`() {
        composeRule.setContent {
            AppTheme {
                SettingsScreen(navController = null, snackStateFlow = snackFlow, viewModel = null)
            }
        }
        composeRule.waitForIdle()
    }
}
