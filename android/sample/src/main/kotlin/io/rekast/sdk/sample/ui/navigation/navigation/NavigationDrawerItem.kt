/*
 * Copyright 2023-2024, Benjamin Mwalimu
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

sealed class NavigationDrawerItem(
    var route: Int,
    var icon: Int,
    var title: String
) {
    object Home : NavigationDrawerItem(
        R.id.homeScreenFragment,
        R.drawable.home,
        Constants.NavigationTitle.HOME
    )

    object Remittance : NavigationDrawerItem(
        R.id.remittanceScreenFragment,
        R.drawable.currency_exchange,
        Constants.NavigationTitle.REMITTANCE
    )

    object CollectionRequestToPay :
        NavigationDrawerItem(
            R.id.collectionPayScreenFragment,
            R.drawable.payments,
            Constants.NavigationTitle.COLLECTION_Service_REQUEST_TO_PAY
        )

    object CollectionRequestToWithdraw :
        NavigationDrawerItem(
            R.id.collectionWithDrawScreenFragment,
            R.drawable.local_mall,
            Constants.NavigationTitle.COLLECTION_Service_REQUEST_TO_WITHDRAW
        )

    object DisbursementDeposit :
        NavigationDrawerItem(
            R.id.disbursementDepositScreenFragment,
            R.drawable.paid,
            Constants.NavigationTitle.DISBURSEMENT_DEPOSIT
        )

    object DisbursementRefund :
        NavigationDrawerItem(
            R.id.disbursementRefundScreenFragment,
            R.drawable.savings,
            Constants.NavigationTitle.DISBURSEMENT_REFUND
        )
}
