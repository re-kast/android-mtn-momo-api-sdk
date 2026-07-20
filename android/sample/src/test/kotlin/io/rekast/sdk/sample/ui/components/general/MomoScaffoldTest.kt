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
package io.rekast.sdk.sample.ui.components.general

import android.content.Context
import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import io.rekast.sdk.sample.R
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
 * Compose UI tests for the shared [MomoScaffold] shell, run on the JVM via Robolectric.
 *
 * Covers both sides of the `navController?.let { Drawer(...) }` branch in the drawer content:
 * the null case (drawer omitted, previews path) and the non-null case (the navigation [Drawer] is
 * composed). A graph-backed [TestNavHostController] is supplied so the drawer's
 * `currentBackStackEntryAsState()` resolves against the real navigation graph.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class MomoScaffoldTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow()

    /** With a null NavController the drawer is omitted; the top bar title and body still render. */
    @Test
    fun `renders without drawer when navController is null`() {
        composeRule.setContent {
            AppTheme {
                MomoScaffold(
                    titleRes = R.string.app_name,
                    navController = null,
                    snackStateFlow = snackStateFlow
                ) {
                    Text(text = "Body Content")
                }
            }
        }
        composeRule.onNodeWithText("MTN Momo API SDK Sample App").assertIsDisplayed()
        composeRule.onNodeWithText("Body Content").assertIsDisplayed()
    }

    /** With a non-null NavController the drawer content composes (the `navController?.let` branch). */
    @Test
    fun `renders with drawer when navController is present`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val navController = TestNavHostController(context)
        navController.setGraph(R.navigation.navigation_graph)
        composeRule.setContent {
            AppTheme {
                MomoScaffold(
                    titleRes = R.string.app_name,
                    navController = navController,
                    snackStateFlow = snackStateFlow
                ) {
                    Text(text = "Body Content")
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("MTN Momo API SDK Sample App").assertIsDisplayed()
        composeRule.onNodeWithText("Body Content").assertIsDisplayed()
        // The drawer content is composed off-screen; its header title confirms the Drawer branch ran.
        composeRule.onNodeWithText("SDK Sample Application").assertExists()
    }
}
