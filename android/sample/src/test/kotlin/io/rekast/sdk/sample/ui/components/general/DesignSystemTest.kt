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

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for the shared design-system building blocks in DesignSystem.kt, run on the JVM
 * via Robolectric.
 *
 * Covers [MomoCard] content hosting, [CardTitle], both [InfoRow] branches (skipped when the value is
 * null/blank, shown otherwise), [RowDivider], and [StatusPill].
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class DesignSystemTest {

    @get:Rule
    val composeRule = createComposeRule()

    /** [MomoCard] hosts its column content and [CardTitle] renders the supplied title. */
    @Test
    fun `card hosts content and renders title`() {
        composeRule.setContent {
            AppTheme {
                MomoCard {
                    CardTitle(title = "Section Title")
                    Text(text = "Card Body")
                }
            }
        }
        composeRule.onNodeWithText("Section Title").assertIsDisplayed()
        composeRule.onNodeWithText("Card Body").assertIsDisplayed()
    }

    /** [InfoRow] renders both the label and the value when the value is present. */
    @Test
    fun `info row shows label and value when present`() {
        composeRule.setContent {
            AppTheme {
                Column { InfoRow(label = "Email", value = "user@example.com") }
            }
        }
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("user@example.com").assertIsDisplayed()
    }

    /** [InfoRow] renders nothing when the value is null (the early-return branch). */
    @Test
    fun `info row skipped when value null`() {
        composeRule.setContent {
            AppTheme {
                Column { InfoRow(label = "Email", value = null) }
            }
        }
        composeRule.onNodeWithText("Email").assertDoesNotExist()
    }

    /** [RowDivider] composes without error alongside sibling content. */
    @Test
    fun `row divider composes`() {
        composeRule.setContent {
            AppTheme {
                Column {
                    RowDivider()
                    Text(text = "After Divider")
                }
            }
        }
        composeRule.onNodeWithText("After Divider").assertIsDisplayed()
    }

    /** [StatusPill] renders its label text. */
    @Test
    fun `status pill renders text`() {
        composeRule.setContent {
            AppTheme {
                StatusPill(text = "Present", color = Color.Green)
            }
        }
        composeRule.onNodeWithText("Present").assertIsDisplayed()
    }
}
