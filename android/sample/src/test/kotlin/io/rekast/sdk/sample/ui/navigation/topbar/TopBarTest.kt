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
package io.rekast.sdk.sample.ui.navigation.topbar

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for the [TopBar], run on the JVM via Robolectric.
 *
 * Covers the rendered title and hamburger icon plus the navigation-icon click that opens the
 * drawer. For the open behaviour the bar is hosted inside a `Scaffold` so the drawer state has
 * real anchors and `drawerState.open()` can settle.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class TopBarTest {

    @get:Rule
    val composeRule = createComposeRule()

    /** The top bar shows its title and an accessible navigation (menu) icon. */
    @Test
    fun `renders title and menu icon`() {
        composeRule.setContent {
            AppTheme {
                val scope = rememberCoroutineScope()
                val scaffoldState = rememberScaffoldState()
                TopBar(scope = scope, scaffoldState = scaffoldState, title = R.string.home_screen)
            }
        }
        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Open navigation drawer").assertIsDisplayed()
    }

    /** Tapping the navigation icon opens the drawer via the launched coroutine. */
    @Test
    fun `clicking menu icon opens the drawer`() {
        lateinit var scaffoldState: ScaffoldState
        composeRule.setContent {
            AppTheme {
                val scope = rememberCoroutineScope()
                scaffoldState = rememberScaffoldState()
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerContent = { Text(text = "Drawer body") },
                    topBar = {
                        TopBar(scope = scope, scaffoldState = scaffoldState, title = R.string.home_screen)
                    },
                    content = { Text(text = "Screen body") }
                )
            }
        }
        composeRule.onNodeWithContentDescription("Open navigation drawer").performClick()
        composeRule.runOnIdle {
            assertTrue(scaffoldState.drawerState.isOpen)
        }
    }
}
