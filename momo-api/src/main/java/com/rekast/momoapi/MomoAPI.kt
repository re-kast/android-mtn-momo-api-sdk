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
     * Prepares to fetch the Balance with and without specific currency
     * @param[currency] -- This is the Currency based on the ISO standard. Read more here https://www.iban.com/currency-codes
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */

    // TODO Review this to see if the get balance with specific currency works.
    fun getBalance(
        currency: String? = null,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        callback: ((APIResult: APIResult<AccountBalance>) -> Unit),
    ) {
        momoAPIRepository.getAccountBalance(currency, productSubscriptionKey, accessToken, apiVersion, productType)
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
    // TODO the API doesn't seem to work as expected.
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
     * Prepares to transfer to a payee
     * @param[transaction] -- The transfer payload from the [Transaction] class
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun transfer(
        accessToken: String,
        transaction: Transaction,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((APIResult: APIResult<Unit>) -> Unit),
    ) {
        momoAPIRepository.transfer(
            accessToken,
            transaction,
            apiVersion,
            productType,
            productSubscriptionKey,
            uuid,
        ).enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the transfer status
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
     * @param[referenceId] -- The Transfer Reference ID. This is a UUID V4. It should be the [requestToPay] UUID.
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[ResponseBody] -- Returns the Transfer Status
     */
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
        ).enqueue(APICallback(callback))
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

    /**
     * Prepares to send a payment request
     * @param[transaction] -- The transfer payload from the [Transaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun requestToPay(
        accessToken: String,
        transaction: Transaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((APIResult: APIResult<Unit>) -> Unit),
    ) {
        momoAPIRepository.requestToPay(
            accessToken,
            transaction,
            apiVersion,
            productSubscriptionKey,
            uuid,
        ).enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the request to pay transaction status
     * @param[referenceId] -- This is new resource Id used here [MomoAPI.requestToPay]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun requestToPayTransactionStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((APIResult: APIResult<ResponseBody?>) -> Unit),
    ) {
        momoAPIRepository.requestToPayTransactionStatus(
            referenceId,
            apiVersion,
            productSubscriptionKey,
            accessToken,
        ).enqueue(TransactionCallback(callback))
    }

    /**
     * Prepares to withdraw from an account
     * @param[transaction] -- The transfer payload from the [Transaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun requestToWithdraw(
        accessToken: String,
        transaction: Transaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((APIResult: APIResult<Unit>) -> Unit),
    ) {
        momoAPIRepository.requestToWithdraw(
            accessToken,
            transaction,
            apiVersion,
            productSubscriptionKey,
            uuid,
        ).enqueue(APICallback(callback))
    }

    /**
     * Prepares to fetch the request to withdraw transaction status
     * @param[referenceId] -- This is new resource Id used here [MomoAPI.requestToPay]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun requestToWithdrawTransactionStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((APIResult: APIResult<ResponseBody?>) -> Unit),
    ) {
        momoAPIRepository.requestToWithdrawTransactionStatus(
            referenceId,
            apiVersion,
            productSubscriptionKey,
            accessToken,
        ).enqueue(TransactionCallback(callback))
    }

    /**
     * Makes a request to deposit to a specific user
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[transaction] -- The transfer payload from the [Transaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun deposit(
        accessToken: String,
        transaction: Transaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((APIResult: APIResult<Unit>) -> Unit),
    ) {
        momoAPIRepository.deposit(
            accessToken,
            transaction,
            apiVersion,
            productSubscriptionKey,
            uuid,
        ).enqueue(APICallback(callback))
    }

    /**
     * Makes a request to check the status of the deposit request
     * @param[referenceId] -- This is new resource Id used here [MomoAPI.requestToPay]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getDepositStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((APIResult: APIResult<ResponseBody?>) -> Unit),
    ) {
        momoAPIRepository.getDepositStatus(
            referenceId,
            apiVersion,
            productSubscriptionKey,
            accessToken,
        ).enqueue(TransactionCallback(callback))
    }

    /**
     * Makes a request to refund a specific user
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[transaction] -- The transfer payload from the [Transaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun refund(
        accessToken: String,
        transaction: Transaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((APIResult: APIResult<Unit>) -> Unit),
    ) {
        momoAPIRepository.refund(
            accessToken,
            transaction,
            apiVersion,
            productSubscriptionKey,
            uuid,
        ).enqueue(APICallback(callback))
    }

    /**
     * Makes a request to check the status of the refund
     * @param[referenceId] -- This is new resource Id used here [MomoAPI.requestToPay]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getRefundStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((APIResult: APIResult<ResponseBody?>) -> Unit),
    ) {
        momoAPIRepository.getRefundStatus(
            referenceId,
            apiVersion,
            productSubscriptionKey,
            accessToken,
        ).enqueue(TransactionCallback(callback))
    }
}
