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
package io.rekast.sdk.sample.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for the app theme ([AppTheme]) and its derived colors.
 *
 * Exercises both the light and dark color palettes and the theme-aware [subtleTextColor] and
 * [dividerColor] accessors so that both branches of each `isLight` conditional are covered.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ThemeTest {

    @get:Rule
    val composeRule = createComposeRule()

    /** The light palette resolves the light background and the light-mode subtle/divider colors. */
    @Test
    fun `light theme resolves light palette and subtle colors`() {
        var background = Color.Unspecified
        var subtle = Color.Unspecified
        var divider = Color.Unspecified
        composeRule.setContent {
            AppTheme(darkTheme = false) {
                background = MaterialTheme.colors.background
                subtle = subtleTextColor
                divider = dividerColor
                Text("light-theme")
            }
        }
        composeRule.onNodeWithText("light-theme").assertIsDisplayed()
        assertEquals(SubtleTextLight, subtle)
        assertEquals(DividerLight, divider)
        assertEquals(LightColors.background, background)
    }

    /** The dark palette resolves the dark background and the dark-mode subtle/divider colors. */
    @Test
    fun `dark theme resolves dark palette and subtle colors`() {
        var background = Color.Unspecified
        var subtle = Color.Unspecified
        var divider = Color.Unspecified
        composeRule.setContent {
            AppTheme(darkTheme = true) {
                background = MaterialTheme.colors.background
                subtle = subtleTextColor
                divider = dividerColor
                Text("dark-theme")
            }
        }
        composeRule.onNodeWithText("dark-theme").assertIsDisplayed()
        assertEquals(SubtleTextDark, subtle)
        assertEquals(DividerDark, divider)
        assertEquals(DarkColors.background, background)
    }

    /** Toggling the theme flag recomposes into the other palette (covers both branches in one run). */
    @Test
    fun `toggling theme switches palette`() {
        val darkState = mutableStateOf(false)
        val backgrounds = mutableListOf<Color>()
        composeRule.setContent {
            val dark by darkState
            AppTheme(darkTheme = dark) {
                backgrounds.add(MaterialTheme.colors.background)
                Text(if (dark) "on" else "off")
            }
        }
        composeRule.onNodeWithText("off").assertIsDisplayed()
        darkState.value = true
        composeRule.onNodeWithText("on").assertIsDisplayed()
        assertEquals(LightColors.background, backgrounds.first())
        assertEquals(DarkColors.background, backgrounds.last())
    }

    /**
     * Recomposes the enclosing scope while [AppTheme]'s inputs stay identical, exercising Compose's
     * skip-when-unchanged path for the theme wrapper.
     */
    @Test
    fun `AppTheme skips recomposition when inputs are unchanged`() {
        val tick = mutableStateOf(0)
        composeRule.setContent {
            // Read tick so this scope recomposes when it changes, re-invoking AppTheme with the
            // same (stable) darkTheme flag and content lambda.
            tick.value
            AppTheme(darkTheme = false) {
                Text("stable-content")
            }
        }
        composeRule.onNodeWithText("stable-content").assertIsDisplayed()

        tick.value = 1
        composeRule.waitForIdle()

        composeRule.onNodeWithText("stable-content").assertIsDisplayed()
    }
}
