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

class MomoRemittanceAPIRepository(apiUserId: String, baseUrl: String) : MomoAPIRepository(apiUserId, baseUrl) {
    override fun getUserApiKey() {
    }

    override fun getAccessToken() {
        TODO("Not yet implemented")
    }

    override fun getAccountBalance() {
        TODO("Not yet implemented")
    }

    override fun getBasicUserInfo() {
        TODO("Not yet implemented")
    }

    /**
     * Not required for the remittance API
     */
    override fun getAccountBalanceInSpecificCurrency() {
        TODO("Not yet implemented")
    }

    /**
     * Not required for the remittance API
     */
    override fun getUserInfoWithoutConsent() {
        TODO("Not yet implemented")
    }

    fun getTransferStatus() {
    }

    fun getUserInfoWithUserConsent() {
    }

    fun requestToPayDeliveryNotification() {
    }

    fun transfer() {
    }

    fun validateAccountHolderStatus() {
    }
}
