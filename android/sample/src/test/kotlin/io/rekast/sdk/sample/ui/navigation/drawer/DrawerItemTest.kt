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
package io.rekast.sdk.sample.ui.navigation.drawer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.rekast.sdk.sample.ui.navigation.navigation.NavigationDrawerItem
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for a single [DrawerItem] row, run on the JVM via Robolectric.
 *
 * Covers both the selected (highlighted) and unselected content-color branches and the
 * `onItemClick` callback.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class DrawerItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    /** A selected item renders its title (exercises the selected content-color branch). */
    @Test
    fun `renders selected item title`() {
        composeRule.setContent {
            AppTheme {
                DrawerItem(item = NavigationDrawerItem.Home, selected = true, onItemClick = {})
            }
        }
        composeRule.onNodeWithText("Home").assertIsDisplayed()
    }

    /** An unselected item renders its title (exercises the unselected content-color branch). */
    @Test
    fun `renders unselected item title`() {
        composeRule.setContent {
            AppTheme {
                DrawerItem(item = NavigationDrawerItem.Settings, selected = false, onItemClick = {})
            }
        }
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    /** Tapping the row invokes onItemClick with the item it was rendered with. */
    @Test
    fun `invokes onItemClick when tapped`() {
        var clicked: NavigationDrawerItem? = null
        composeRule.setContent {
            AppTheme {
                DrawerItem(
                    item = NavigationDrawerItem.Home,
                    selected = false,
                    onItemClick = { clicked = it }
                )
            }
        }
        composeRule.onNodeWithText("Home").performClick()
        composeRule.runOnIdle {
            assertEquals(NavigationDrawerItem.Home, clicked)
        }
    }
}
