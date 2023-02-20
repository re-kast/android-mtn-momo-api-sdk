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
package com.rekast.momoapi.repository

abstract class MomoAPIRepository(
    var apiUserId: String,
    var baseUrl: String,
) {
    /**
     * Get the API Key based on the User Id and OCP Subscription Id
     */
    abstract fun getUserApiKey()

    /**
     * Get the Access Token based on the User ID, OCP Subscription Id and the API Key
     */
    abstract fun getAccessToken()

    /**
     * Gets the account balance of the entity/user initiating the transaction.
     */
    abstract fun getAccountBalance()

    /**
     * Get the basic user information for a certain MTN MOMO User
     */
    abstract fun getBasicUserInfo()
}
