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
package io.rekast.sdk.sample.ui.components.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for the reusable operation building blocks in OperationComponents.kt, run on the
 * JVM via Robolectric.
 *
 * Covers [LabeledField] rendering, [OperationActionButton] in both enabled (click fires) and
 * disabled (click suppressed) states, and both [OperationConsole] branches (null result shows the
 * hint; a result string shows the payload).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class OperationComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    /** [LabeledField] renders its label. */
    @Test
    fun `labeled field renders label`() {
        composeRule.setContent {
            AppTheme {
                LabeledField(label = "Amount", value = "1000", onValueChange = {})
            }
        }
        composeRule.onNodeWithText("Amount").assertIsDisplayed()
    }

    /** An enabled [OperationActionButton] renders its label and fires the click callback. */
    @Test
    fun `action button enabled fires click`() {
        var clicked = false
        composeRule.setContent {
            AppTheme {
                OperationActionButton(text = "Create", onClick = { clicked = true }, enabled = true)
            }
        }
        composeRule.onNodeWithText("Create").assertIsEnabled().performClick()
        assertTrue(clicked)
    }

    /** A disabled [OperationActionButton] does not fire its click callback. */
    @Test
    fun `action button disabled suppresses click`() {
        var clicked = false
        composeRule.setContent {
            AppTheme {
                OperationActionButton(text = "Create", onClick = { clicked = true }, enabled = false)
            }
        }
        composeRule.onNodeWithText("Create").assertIsNotEnabled()
        assertFalse(clicked)
    }

    /** [OperationConsole] shows the hint when the result is null. */
    @Test
    fun `console shows hint when result null`() {
        composeRule.setContent {
            AppTheme {
                OperationConsole(result = null, hint = "Run an operation")
            }
        }
        composeRule.onNodeWithText("Run an operation").assertIsDisplayed()
    }

    /** [OperationConsole] shows the result payload and hides the hint when a result is present. */
    @Test
    fun `console shows result when present`() {
        composeRule.setContent {
            AppTheme {
                OperationConsole(result = "ref-12345", hint = "Run an operation")
            }
        }
        composeRule.onNodeWithText("ref-12345").assertIsDisplayed()
        composeRule.onNodeWithText("Run an operation").assertDoesNotExist()
    }
}
