/*
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package com.rekast.momoapi.repository.products

import com.rekast.momoapi.repository.MomoAPIRepository

class MomoDisbursementsAPIRepository(apiUserId: String, baseUrl: String) : MomoAPIRepository(apiUserId, baseUrl) {

    fun getAccountBalanceInSpecificCurrency() {
    }
    fun requestToPay() {
        TODO("Not yet implemented")
    }

    fun requestToPayDeliveryNotification() {
        TODO("Not yet implemented")
    }

    fun requestToPayTransactionStatus() {
        TODO("Not yet implemented")
    }

    fun requestToWithdrawTransactionStatus() {
        TODO("Not yet implemented")
    }

    fun requestToWithdrawV1() {
        TODO("Not yet implemented")
    }

    fun requestToWithdrawV2() {
        TODO("Not yet implemented")
    }

    fun validateAccountHolderStatus() {
        TODO("Not yet implemented")
    }
}
