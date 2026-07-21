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
package io.rekast.sdk.sample.views.main

import android.content.Context
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import io.rekast.sdk.sample.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Host-side unit tests for the navigation graph installed by [MainActivity], run on the JVM via
 * Robolectric.
 *
 * [MainActivity] is a Hilt `@AndroidEntryPoint` whose dependency graph is assembled in the `:app`
 * module, so it cannot be launched directly from a `:sample` unit test. Instead these tests inflate
 * the same `R.navigation.navigation_graph` resource the Activity installs (via
 * [TestNavHostController], which registers test navigators for every destination type) and assert
 * its structure — the start destination and the presence of each drawer destination.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityTest {

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.navigation_graph)
    }

    /** The navigation graph points at the Home screen as its start destination. */
    @Test
    fun `graph starts at home destination`() {
        assertEquals(R.id.homeScreenFragment, navController.graph.startDestinationId)
    }

    /** The navigation graph exposes the drawer destinations, including the newer screens. */
    @Test
    fun `navigation graph contains the expected destinations`() {
        val graph = navController.graph

        assertNotNull(graph.findNode(R.id.homeScreenFragment))
        assertNotNull(graph.findNode(R.id.setupScreenFragment))
        assertNotNull(graph.findNode(R.id.settingsScreenFragment))
        assertNotNull(graph.findNode(R.id.collectionPayScreenFragment))
        assertNotNull(graph.findNode(R.id.collectionWithDrawScreenFragment))
        assertNotNull(graph.findNode(R.id.disbursementDepositScreenFragment))
        assertNotNull(graph.findNode(R.id.disbursementRefundScreenFragment))
        assertNotNull(graph.findNode(R.id.remittanceScreenFragment))
        assertNotNull(graph.findNode(R.id.invoiceScreenFragment))
        assertNotNull(graph.findNode(R.id.preApprovalScreenFragment))
        assertNotNull(graph.findNode(R.id.cashTransferScreenFragment))
    }
}
