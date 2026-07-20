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
package io.rekast.sdk.sample.ui.navigation.navigation

import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.utils.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the [NavigationDrawerItem] sealed hierarchy.
 *
 * Verifies each drawer destination exposes the expected route, icon, and title, exercising every
 * `object` subclass so the sealed hierarchy is fully instantiated.
 */
class NavigationDrawerItemTest {

    /** Every drawer item exposes the route, icon, and title wired in its declaration. */
    @Test
    fun `each navigation item exposes its route icon and title`() {
        val expected = listOf(
            Triple(NavigationDrawerItem.Home, R.id.homeScreenFragment, Constants.NavigationTitle.HOME),
            Triple(NavigationDrawerItem.Remittance, R.id.remittanceScreenFragment, Constants.NavigationTitle.REMITTANCE),
            Triple(NavigationDrawerItem.CollectionRequestToPay, R.id.collectionPayScreenFragment, Constants.NavigationTitle.COLLECTION_SERVICE_REQUEST_TO_PAY),
            Triple(NavigationDrawerItem.CollectionRequestToWithdraw, R.id.collectionWithDrawScreenFragment, Constants.NavigationTitle.COLLECTION_SERVICE_REQUEST_TO_WITHDRAW),
            Triple(NavigationDrawerItem.DisbursementDeposit, R.id.disbursementDepositScreenFragment, Constants.NavigationTitle.DISBURSEMENT_DEPOSIT),
            Triple(NavigationDrawerItem.DisbursementRefund, R.id.disbursementRefundScreenFragment, Constants.NavigationTitle.DISBURSEMENT_REFUND),
            Triple(NavigationDrawerItem.CollectionInvoice, R.id.invoiceScreenFragment, Constants.NavigationTitle.COLLECTION_INVOICE),
            Triple(NavigationDrawerItem.CollectionPreApproval, R.id.preApprovalScreenFragment, Constants.NavigationTitle.COLLECTION_PRE_APPROVAL),
            Triple(NavigationDrawerItem.RemittanceCashTransfer, R.id.cashTransferScreenFragment, Constants.NavigationTitle.REMITTANCE_CASH_TRANSFER),
            Triple(NavigationDrawerItem.Setup, R.id.setupScreenFragment, Constants.NavigationTitle.SETUP),
            Triple(NavigationDrawerItem.Settings, R.id.settingsScreenFragment, Constants.NavigationTitle.SETTINGS)
        )

        expected.forEach { (item, route, title) ->
            assertEquals(route, item.route)
            assertEquals(title, item.title)
            assertTrue("Icon resource id should be non-zero for $title", item.icon != 0)
        }
    }

    /** The mutable properties can be reassigned (sanity check for the `var` declarations). */
    @Test
    fun `navigation item properties are mutable`() {
        val item = NavigationDrawerItem.Home
        val originalRoute = item.route
        item.route = 12345
        assertEquals(12345, item.route)
        item.route = originalRoute
    }
}
