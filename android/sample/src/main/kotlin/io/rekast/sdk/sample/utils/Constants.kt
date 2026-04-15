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
package io.rekast.sdk.sample.utils

/**
 * Contains constant values used throughout the MTN MOMO SDK sample application.
 */
object Constants {
    object NavigationTitle {
        const val HOME = "Home"
        const val REMITTANCE = "Remittance"
        const val COLLECTION_Service_REQUEST_TO_PAY = "CollectionService | Request To Pay"
        const val COLLECTION_Service_REQUEST_TO_WITHDRAW = "CollectionService | Request To Withdraw"
        const val DISBURSEMENT_DEPOSIT = "Disbursement | Deposit"
        const val DISBURSEMENT_REFUND = "Disbursement | Refund"
    }

    const val SANDBOX_CURRENCY = "EUR"
    const val STRING_LENGTH = 12
    const val EMPTY_STRING = ""
}
