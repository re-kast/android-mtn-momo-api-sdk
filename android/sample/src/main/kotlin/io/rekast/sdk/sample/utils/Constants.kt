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
package io.rekast.sdk.sample.utils

/**
 * Contains constant values used throughout the MTN MOMO SDK sample application.
 */
object Constants {
    /** Navigation drawer title strings for each screen destination. */
    object NavigationTitle {
        const val HOME = "Home"
        const val REMITTANCE = "Remittance"
        const val COLLECTION_SERVICE_REQUEST_TO_PAY = "CollectionService | Request To Pay"
        const val COLLECTION_SERVICE_REQUEST_TO_WITHDRAW = "CollectionService | Request To Withdraw"
        const val DISBURSEMENT_DEPOSIT = "Disbursement | Deposit"
        const val DISBURSEMENT_REFUND = "Disbursement | Refund"
        const val COLLECTION_INVOICE = "Collection | Invoice"
        const val COLLECTION_PRE_APPROVAL = "Collection | Pre-Approval"
        const val COLLECTION_APPROVED_PRE_APPROVALS = "Collection | Approved Pre-Approvals"
        const val COLLECTION_PAYMENT = "Collection | Payment"
        const val REMITTANCE_CASH_TRANSFER = "Remittance | Cash Transfer"
        const val SETUP = "Setup & Config"
        const val SETTINGS = "Settings"
    }

    /** Default currency used for sandbox API transactions. */
    const val SANDBOX_CURRENCY = "EUR"

    /** Default length for randomly generated alphanumeric external ID strings. */
    const val STRING_LENGTH = 12

    /** Reusable empty string constant to avoid repeated string literals. */
    const val EMPTY_STRING = ""
}
