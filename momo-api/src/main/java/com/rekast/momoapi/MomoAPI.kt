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
package com.rekast.momoapi

import com.rekast.momoapi.callback.APICallback
import com.rekast.momoapi.callback.APIResult
import com.rekast.momoapi.callback.NotificationCallback
import com.rekast.momoapi.callback.TransactionCallback
import com.rekast.momoapi.model.api.AccountBalance
import com.rekast.momoapi.model.api.AccountHolder
import com.rekast.momoapi.model.api.BasicUserInfo
import com.rekast.momoapi.model.api.Notification
import com.rekast.momoapi.model.api.Transaction
import com.rekast.momoapi.model.api.UserInfoWithConsent
import com.rekast.momoapi.model.authentication.AccessToken
import com.rekast.momoapi.model.authentication.ApiUser
import com.rekast.momoapi.model.authentication.ApiUserKey
import com.rekast.momoapi.repository.MomoAPIRepository
import com.rekast.momoapi.utils.Constants
import okhttp3.ResponseBody

/**
 * Prepares the different MOMO API requests
 */
object MomoAPI {
    lateinit var momoAPIRepository: MomoAPIRepository
    fun builder(apiUserId: String): MomoAPIBuilder = MomoAPIBuilder(apiUserId)

    /**
     * Prepares to fetch the API User
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[callback] -- The request callback. Returns @see [ApiUser]
     */
    fun checkApiUser(
        productSubscriptionKey: String,
        apiVersion: String,
        callback: ((APIResult: APIResult<ApiUser>) -> Unit),
    ) {
        momoAPIRepository.checkApiUser(productSubscriptionKey, apiVersion)
            .enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the API Key
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[callback] -- The request callback. Returns @see [ApiUserKey]
     */
    fun getUserApiKey(
        productSubscriptionKey: String,
        apiVersion: String,
        callback: ((APIResult: APIResult<ApiUserKey>) -> Unit),
    ) {
        momoAPIRepository.getUserApiKey(productSubscriptionKey, apiVersion)
            .enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the Access Token
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiKey] -- The API Key fetched here [MomoAPI.getUserApiKey]
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getAccessToken(
        productSubscriptionKey: String,
        apiKey: String,
        productType: String,
        callback: ((APIResult: APIResult<AccessToken>) -> Unit),
    ) {
        momoAPIRepository.getAccessToken(productSubscriptionKey, apiKey, productType)
            .enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the Balance
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getBalance(
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        callback: ((APIResult: APIResult<AccountBalance>) -> Unit),
    ) {
        momoAPIRepository.getAccountBalance(productSubscriptionKey, accessToken, apiVersion, productType)
            .enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the Basic User Information
     * @param[accountHolder]  -- The account holder's msisdn
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getBasicUserInfo(
        accountHolder: String,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        callback: ((APIResult: APIResult<BasicUserInfo>) -> Unit),
    ) {
        momoAPIRepository.getBasicUserInfo(
            accountHolder,
            productSubscriptionKey,
            accessToken,
            apiVersion,
            productType,
        ).enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the User Information with consent
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    // API Doesn't seem to work
    fun getUserInfoWithConsent(
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        callback: ((APIResult: APIResult<UserInfoWithConsent>) -> Unit),
    ) {
        momoAPIRepository.getUserInfoWithConsent(
            productSubscriptionKey,
            accessToken,
            apiVersion,
            productType,
        ).enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the User Information without consent
     * @param[transaction] -- The transfer payload from the [Transaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun transfer(
        transaction: Transaction,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        uuid: String,
        callback: ((APIResult: APIResult<Unit>) -> Unit),
    ) {
        momoAPIRepository.transfer(
            transaction,
            apiVersion,
            productSubscriptionKey,
            accessToken,
            uuid,
        ).enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the User Information without consent
     * @param[referenceId] -- This is new resource Id used here [MomoAPI.transfer]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getTransferStatus(
        referenceId: String,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((APIResult: APIResult<ResponseBody?>) -> Unit),
    ) {
        momoAPIRepository.getTransferStatus(
            referenceId,
            apiVersion,
            productType,
            productSubscriptionKey,
            accessToken,
        ).enqueue(TransactionCallback(callback))
    }

    /**
     * Prepares to send the delivery notification
     * @param[notification] -- The notification message. This is by default capped 1t 160 characters.
     * Use the [Setting.checkNotificationMessageLength] to check the length
     * @param[referenceId] -- The Transfer Reference ID. This is a UUID V4. It should be the customer Id.
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[ResponseBody] -- Returns the Transfer Status
     */

    // TODO Review to see whether it works well with a known delivery reference Id
    fun requestToPayDeliveryNotification(
        notification: Notification,
        referenceId: String,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((APIResult: APIResult<ResponseBody?>) -> Unit),
    ) {
        momoAPIRepository.requestToPayDeliveryNotification(
            notification,
            referenceId,
            apiVersion,
            productType,
            productSubscriptionKey,
            accessToken,
        ).enqueue(NotificationCallback(callback))
    }

    /**
     * Makes a request to check the account holder status
     * @param[accountHolder] -- This is the account holder which contains
     * [AccountHolder.partyId] which is the account holder unique Id e.g the phone number
     * and [AccountHolder.partyIdType] which is the account holder type. e.g MSISDN
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[ResponseBody] -- Returns the Transfer Status
     */
    fun validateAccountHolderStatus(
        accountHolder: AccountHolder,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((APIResult: APIResult<ResponseBody?>) -> Unit),
    ) {
        momoAPIRepository.validateAccountHolderStatus(
            accountHolder,
            apiVersion,
            productType,
            productSubscriptionKey,
            accessToken,
        ).enqueue(APICallback(callback))
    }
}
