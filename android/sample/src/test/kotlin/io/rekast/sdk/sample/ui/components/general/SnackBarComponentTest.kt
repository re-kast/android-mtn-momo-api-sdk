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

import androidx.compose.foundation.layout.Box
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.utils.SnackBarType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [SnackBarComponent], run on the JVM via Robolectric.
 *
 * Exercises each [SnackBarType] color branch (SUCCESS, ERROR, INFO) by hosting the component and
 * pushing a message through a [SnackbarHostState] so the snackbar content composes and is asserted.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class SnackBarComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun showSnackbar(type: SnackBarType, message: String) {
        composeRule.setContent {
            AppTheme {
                val hostState = remember { SnackbarHostState() }
                Box {
                    SnackBarComponent(snackBarHostState = hostState, type = type)
                    LaunchedEffect(Unit) { hostState.showSnackbar(message = message, duration = SnackbarDuration.Indefinite) }
                }
            }
        }
    }

    /** The SUCCESS branch composes a green snackbar showing the message. */
    @Test
    fun `renders success snackbar`() {
        showSnackbar(SnackBarType.SUCCESS, "Success message")
        composeRule.onNodeWithText("Success message").assertIsDisplayed()
    }

    /** The ERROR branch composes a red snackbar showing the message. */
    @Test
    fun `renders error snackbar`() {
        showSnackbar(SnackBarType.ERROR, "Error message")
        composeRule.onNodeWithText("Error message").assertIsDisplayed()
    }

    /** The INFO branch composes an MTN-blue snackbar showing the message. */
    @Test
    fun `renders info snackbar`() {
        showSnackbar(SnackBarType.INFO, "Info message")
        composeRule.onNodeWithText("Info message").assertIsDisplayed()
    }
}
