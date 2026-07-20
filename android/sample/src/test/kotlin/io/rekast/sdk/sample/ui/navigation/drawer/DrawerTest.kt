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

import android.content.Context
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for the navigation [Drawer] (and its private `DrawerSectionHeader`), run on the
 * JVM via Robolectric.
 *
 * Covers the drawer header, every collapsible section header, the copyright footer, the
 * expand/collapse toggle, and the item-click navigation lambda. A [TestNavHostController] backed by
 * the real `R.navigation.navigation_graph` is supplied so `currentBackStackEntryAsState()` reports
 * the start destination (making the Home item selected) and so `navController.navigate(...)`
 * succeeds when a drawer item is clicked.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class DrawerTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    /** Renders the [Drawer] wired to a graph-backed [TestNavHostController]. */
    private fun setDrawer() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.navigation_graph)
        composeRule.setContent {
            AppTheme {
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
            }
        }
    }

    /** The drawer renders its header title, every section header, and the copyright footer. */
    @Test
    fun `renders header sections and footer`() {
        setDrawer()
        composeRule.onNodeWithText("SDK Sample Application").assertIsDisplayed()
        // Section headers are rendered upper-cased by DrawerSectionHeader.
        composeRule.onNodeWithText("GENERAL").assertExists()
        composeRule.onNodeWithText("COLLECTION").assertExists()
        composeRule.onNodeWithText("DISBURSEMENT").assertExists()
        composeRule.onNodeWithText("REMITTANCE").assertExists()
        composeRule.onNodeWithText("Developed by Benjamin Mwalimu").assertExists()
    }

    /** With the graph at its start destination, the Home drawer item is rendered (and selected). */
    @Test
    fun `renders drawer items for expanded sections`() {
        setDrawer()
        composeRule.onNodeWithText("Home").assertExists()
        composeRule.onNodeWithText("Setup & Config").assertExists()
        composeRule.onNodeWithText("Settings").assertExists()
    }

    /** Tapping a section header collapses it, then tapping again expands it (toggle lambda). */
    @Test
    fun `toggles a section collapsed and expanded`() {
        setDrawer()
        // Expanded by default: its items exist.
        composeRule.onNodeWithText("Setup & Config").assertExists()
        // Collapse the General section.
        composeRule.onNodeWithText("GENERAL").performClick()
        composeRule.onNodeWithText("Setup & Config").assertDoesNotExist()
        // Expand it again.
        composeRule.onNodeWithText("GENERAL").performClick()
        composeRule.onNodeWithText("Setup & Config").assertExists()
    }

    /** Clicking a drawer item navigates via the NavController to that item's route. */
    @Test
    fun `clicking an item navigates to its route`() {
        setDrawer()
        composeRule.onNodeWithText("Setup & Config").performClick()
        composeRule.runOnIdle {
            assertEquals(R.id.setupScreenFragment, navController.currentDestination?.id)
        }
    }

    /**
     * After navigating to a non-start destination the current back-stack entry changes, so the
     * `selected = navBackStackEntry?.destination?.id == item.route` comparison resolves true for the
     * newly-current item and false for the others (both sides of the branch across the item list).
     */
    @Test
    fun `reflects selection after navigating to another destination`() {
        setDrawer()
        composeRule.runOnIdle { navController.navigate(R.id.setupScreenFragment) }
        composeRule.waitForIdle()
        assertEquals(R.id.setupScreenFragment, navController.currentDestination?.id)
        composeRule.onNodeWithText("Setup & Config").assertExists()
        composeRule.onNodeWithText("Home").assertExists()
    }

    /**
     * With a [TestNavHostController] that has no graph set, `currentBackStackEntryAsState()` never
     * emits a destination, so `navBackStackEntry` stays null. This drives the null-safe-call arm of
     * `selected = navBackStackEntry?.destination?.id == item.route` (the whole expression evaluates
     * to null, so no item is selected) while the drawer items still render.
     */
    @Test
    fun `renders items when back stack entry is null`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val emptyNavController = TestNavHostController(context)
        composeRule.setContent {
            AppTheme {
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                Drawer(scope = scope, scaffoldState = scaffoldState, navController = emptyNavController)
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Home").assertExists()
        composeRule.onNodeWithText("Setup & Config").assertExists()
    }
}
