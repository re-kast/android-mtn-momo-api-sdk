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
package io.rekast.sdk.sample.views.setup

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.MutableLiveData
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
 * Compose UI tests for [SetupScreen], run on the JVM via Robolectric.
 *
 * Covers the configuration card rendering, the credential-status pills, and the re-run action.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class SetupScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val config = SampleConfig(
        apiVersionV1 = "v1_0",
        apiVersionV2 = "v2_0",
        environment = "sandbox",
        providerCallbackHost = "localhost",
        apiUserId = "user-123",
        collectionPrimaryKey = "col",
        collectionSecondaryKey = "col2",
        remittancePrimaryKey = "rem",
        remittanceSecondaryKey = "rem2",
        disbursementsPrimaryKey = "dis",
        disbursementsSecondaryKey = "dis2"
    )

    private val snackFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow()

    private fun viewModel(): SetupScreenViewModel = mockk(relaxed = true) {
        every { config } returns this@SetupScreenTest.config
        every { status } returns MutableLiveData(
            SetupScreenViewModel.CredentialStatus(
                apiKeyPresent = true,
                accessTokenPresent = false,
                oauthTokenPresent = false,
                authReqIdPresent = false,
                loginHintPresent = false
            )
        )
        every { snackBarStateFlow } returns snackFlow
    }

    private fun setScreen(onRerun: () -> Unit = {}) {
        val vm = viewModel()
        composeRule.setContent {
            AppTheme {
                SetupScreen(navController = null, snackStateFlow = snackFlow, viewModel = vm, onRerunSetup = onRerun)
            }
        }
    }

    /** The configuration card renders the environment value. */
    @Test
    fun `renders configuration values`() {
        setScreen()
        composeRule.onNodeWithText("sandbox").assertIsDisplayed()
    }

    /** The credential-status card shows Present/Missing pills reflecting the status. */
    @Test
    fun `renders credential status pills`() {
        setScreen()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Present"))
        // apiKeyPresent = true -> exactly one "Present"; the other four credentials are "Missing".
        composeRule.onNodeWithText("Present").assertIsDisplayed()
        composeRule.onAllNodesWithText("Missing").assertCountEquals(4)
    }

    /** Tapping the re-run button invokes the callback. */
    @Test
    fun `rerun setup button invokes callback`() {
        var rerun = false
        setScreen(onRerun = { rerun = true })

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Re-run Setup"))
        composeRule.onNodeWithText("Re-run Setup").performClick()

        assertTrue(rerun)
    }
}
