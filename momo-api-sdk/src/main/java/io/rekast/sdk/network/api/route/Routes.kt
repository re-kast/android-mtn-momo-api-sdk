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
package io.rekast.sdk.network.api.route

import io.rekast.sdk.Builder
import io.rekast.sdk.callback.MomoCallback
import io.rekast.sdk.callback.MomoResponse
import io.rekast.sdk.callback.TransactionCallback
import io.rekast.sdk.model.api.AccountBalance
import io.rekast.sdk.model.api.AccountHolder
import io.rekast.sdk.model.api.BasicUserInfo
import io.rekast.sdk.model.api.MomoNotification
import io.rekast.sdk.model.api.MomoTransaction
import io.rekast.sdk.model.api.UserInfoWithConsent
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.User
import io.rekast.sdk.model.authentication.UserKey
import io.rekast.sdk.repository.Repository
import okhttp3.ResponseBody

/**
 * Prepares the different MOMO API requests
 */
object Routes {
    lateinit var repository: Repository
    fun builder(apiUserId: String): Builder = Builder(apiUserId)

    /**
     * Prepares to fetch the API User
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[callback] -- The request callback. Returns @see [User]
     */
    fun checkApiUser(
        productSubscriptionKey: String,
        apiVersion: String,
        callback: ((momoResponse: MomoResponse<User>) -> Unit)
    ) {
        repository.checkApiUser(productSubscriptionKey, apiVersion)
            .enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to fetch the API Key
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[callback] -- The request callback. Returns @see [UserKey]
     */
    fun getUserApiKey(
        productSubscriptionKey: String,
        apiVersion: String,
        callback: ((momoResponse: MomoResponse<UserKey>) -> Unit)
    ) {
        repository.getUserApiKey(productSubscriptionKey, apiVersion)
            .enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to fetch the Access Token
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiKey] -- The API Key fetched here [Routes.getUserApiKey]
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getAccessToken(
        productSubscriptionKey: String,
        apiKey: String,
        productType: String,
        callback: ((momoResponse: MomoResponse<AccessToken>) -> Unit)
    ) {
        repository.getAccessToken(productSubscriptionKey, apiKey, productType)
            .enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to fetch the Balance with and without specific currency
     * @param[currency] -- This is the Currency based on the ISO standard. Read more here https://www.iban.com/currency-codes
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */

    // TODO Review this to see if the get balance with specific currency works.
    fun getBalance(
        currency: String? = null,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        callback: ((momoResponse: MomoResponse<AccountBalance>) -> Unit)
    ) {
        repository.getAccountBalance(currency, productSubscriptionKey, accessToken, apiVersion, productType)
            .enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to fetch the Basic User Information
     * @param[accountHolder]  -- The account holder's msisdn
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getBasicUserInfo(
        accountHolder: String,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        callback: ((momoResponse: MomoResponse<BasicUserInfo>) -> Unit)
    ) {
        repository.getBasicUserInfo(
            accountHolder,
            productSubscriptionKey,
            accessToken,
            apiVersion,
            productType
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to fetch the User Information with consent
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    // TODO the API doesn't seem to work as expected.
    fun getUserInfoWithConsent(
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        callback: ((momoResponse: MomoResponse<UserInfoWithConsent>) -> Unit)
    ) {
        repository.getUserInfoWithConsent(
            productSubscriptionKey,
            accessToken,
            apiVersion,
            productType
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to transfer to a payee
     * @param[momoTransaction] -- The transfer payload from the [MomoTransaction] class
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun transfer(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((momoResponse: MomoResponse<Unit>) -> Unit)
    ) {
        repository.transfer(
            accessToken,
            momoTransaction,
            apiVersion,
            productType,
            productSubscriptionKey,
            uuid
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to fetch the transfer status
     * @param[referenceId] -- This is new resource Id used here [Routes.transfer]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getTransferStatus(
        referenceId: String,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((momoResponse: MomoResponse<ResponseBody?>) -> Unit)
    ) {
        repository.getTransferStatus(
            referenceId,
            apiVersion,
            productType,
            productSubscriptionKey,
            accessToken
        ).enqueue(TransactionCallback(callback))
    }

    /**
     * Prepares to send the delivery notification
     * @param[momoNotification] -- The notification message. This is by default capped 1t 160 characters.
     * Use the [Setting.checkNotificationMessageLength] to check the length
     * @param[referenceId] -- The Transfer Reference ID. This is a UUID V4. It should be the [requestToPay] UUID.
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[ResponseBody] -- Returns the Transfer Status
     */
    fun requestToPayDeliveryNotification(
        momoNotification: MomoNotification,
        referenceId: String,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((momoResponse: MomoResponse<ResponseBody?>) -> Unit)
    ) {
        repository.requestToPayDeliveryNotification(
            momoNotification,
            referenceId,
            apiVersion,
            productType,
            productSubscriptionKey,
            accessToken
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Makes a request to check the account holder status
     * @param[accountHolder] -- This is the account holder which contains
     * [AccountHolder.partyId] which is the account holder unique Id e.g the phone number
     * and [AccountHolder.partyIdType] which is the account holder type. e.g MSISDN
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([MomoConstants.ProductTypes])
     * @param[productSubscriptionKey] -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @return[ResponseBody] -- Returns the Transfer Status
     */
    fun validateAccountHolderStatus(
        accountHolder: AccountHolder,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((momoResponse: MomoResponse<ResponseBody?>) -> Unit)
    ) {
        repository.validateAccountHolderStatus(
            accountHolder,
            apiVersion,
            productType,
            productSubscriptionKey,
            accessToken
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to send a payment request
     * @param[momoTransaction] -- The transfer payload from the [MomoTransaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun requestToPay(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((momoResponse: MomoResponse<Unit>) -> Unit)
    ) {
        repository.requestToPay(
            accessToken,
            momoTransaction,
            apiVersion,
            productSubscriptionKey,
            uuid
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to fetch the request to pay transaction status
     * @param[referenceId] -- This is new resource Id used here [Routes.requestToPay]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun requestToPayTransactionStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((momoResponse: MomoResponse<ResponseBody?>) -> Unit)
    ) {
        repository.requestToPayTransactionStatus(
            referenceId,
            apiVersion,
            productSubscriptionKey,
            accessToken
        ).enqueue(TransactionCallback(callback))
    }

    /**
     * Prepares to withdraw from an account
     * @param[momoTransaction] -- The transfer payload from the [MomoTransaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun requestToWithdraw(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((momoResponse: MomoResponse<Unit>) -> Unit)
    ) {
        repository.requestToWithdraw(
            accessToken,
            momoTransaction,
            apiVersion,
            productSubscriptionKey,
            uuid
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Prepares to fetch the request to withdraw transaction status
     * @param[referenceId] -- This is new resource Id used here [Routes.requestToPay]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun requestToWithdrawTransactionStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((momoResponse: MomoResponse<ResponseBody?>) -> Unit)
    ) {
        repository.requestToWithdrawTransactionStatus(
            referenceId,
            apiVersion,
            productSubscriptionKey,
            accessToken
        ).enqueue(TransactionCallback(callback))
    }

    /**
     * Makes a request to deposit to a specific user
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[transaction] -- The transfer payload from the [Transaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun deposit(
        accessToken: String,
        transaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((momoResponse: MomoResponse<Unit>) -> Unit)
    ) {
        repository.deposit(
            accessToken,
            transaction,
            apiVersion,
            productSubscriptionKey,
            uuid
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Makes a request to check the status of the deposit request
     * @param[referenceId] -- This is new resource Id used here [Routes.requestToPay]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getDepositStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((momoResponse: MomoResponse<ResponseBody?>) -> Unit)
    ) {
        repository.getDepositStatus(
            referenceId,
            apiVersion,
            productSubscriptionKey,
            accessToken
        ).enqueue(TransactionCallback(callback))
    }

    /**
     * Makes a request to refund a specific user
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[transaction] -- The transfer payload from the [Transaction] class
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[uuid] -- The new resource UUID. It's a UUID V4.
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun refund(
        accessToken: String,
        transaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String,
        callback: ((momoResponse: MomoResponse<Unit>) -> Unit)
    ) {
        repository.refund(
            accessToken,
            transaction,
            apiVersion,
            productSubscriptionKey,
            uuid
        ).enqueue(MomoCallback(callback))
    }

    /**
     * Makes a request to check the status of the refund
     * @param[referenceId] -- This is new resource Id used here [Routes.requestToPay]. It is a UUID V4
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [Routes.getAccessToken]
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getRefundStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String,
        callback: ((momoResponse: MomoResponse<ResponseBody?>) -> Unit)
    ) {
        repository.getRefundStatus(
            referenceId,
            apiVersion,
            productSubscriptionKey,
            accessToken
        ).enqueue(TransactionCallback(callback))
    }
}
