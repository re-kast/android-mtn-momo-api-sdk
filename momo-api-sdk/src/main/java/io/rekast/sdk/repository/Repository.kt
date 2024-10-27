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
package io.rekast.sdk.repository

import io.rekast.sdk.model.api.AccountBalance
import io.rekast.sdk.model.api.AccountHolder
import io.rekast.sdk.model.api.BasicUserInfo
import io.rekast.sdk.model.api.MomoNotification
import io.rekast.sdk.model.api.MomoTransaction
import io.rekast.sdk.model.api.UserInfoWithConsent
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.User
import io.rekast.sdk.model.authentication.UserKey
import io.rekast.sdk.network.api.client.Clients
import io.rekast.sdk.network.okhttp.AccessTokenInterceptor
import io.rekast.sdk.network.okhttp.BasicAuthInterceptor
import okhttp3.ResponseBody
import org.apache.commons.lang3.StringUtils
import retrofit2.Call

/**
 * The BaseRepository Class. Responsible for making the actual network call to the MOMO APIs.
 * This calls holds all the commons API methods for all the MTN MOMO Products
 */
class Repository(
    var apiUserId: String,
    var baseUrl: String,
    var environment: String
) {
    /**
     * Check whether the supplied user exists
     */
    fun checkApiUser(
        productSubscriptionKey: String,
        apiVersion: String
    ): Call<User> {
        return Clients().getAuthentication(baseUrl, null)
            .getApiUser(apiVersion, apiUserId, productSubscriptionKey)
    }

    /**
     * Get the API Key based on the User Id and OCP Subscription Id
     */
    fun getUserApiKey(
        productSubscriptionKey: String,
        apiVersion: String
    ): Call<UserKey> {
        return Clients().getAuthentication(baseUrl, null)
            .getApiUserKey(apiVersion, apiUserId, productSubscriptionKey)
    }

    /**
     * Get the Access Token based on the User ID, OCP Subscription Id and the API Key
     */
    fun getAccessToken(
        productSubscriptionKey: String,
        apiKey: String,
        productType: String
    ): Call<AccessToken> {
        return Clients().getAuthentication(
            baseUrl,
            BasicAuthInterceptor(apiUserId, apiKey)
        ).getAccessToken(productType, productSubscriptionKey)
    }

    /**
     * Gets the account balance of the entity/user initiating the transaction.
     */
    fun getAccountBalance(
        currency: String?,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String
    ): Call<AccountBalance> {
        return if (StringUtils.isNotBlank(currency)) {
            Clients().getCommon(
                baseUrl,
                AccessTokenInterceptor(accessToken)
            ).getAccountBalanceInSpecificCurrency(
                currency.toString(),
                productType,
                apiVersion,
                productSubscriptionKey,
                environment
            )
        } else {
            Clients().getCommon(
                baseUrl,
                AccessTokenInterceptor(accessToken)
            ).getAccountBalance(productType, apiVersion, productSubscriptionKey, environment)
        }
    }

    /**
     * Get the basic user information for a certain MTN MOMO User
     */
    fun getBasicUserInfo(
        accountHolder: String,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String
    ): Call<BasicUserInfo> {
        return Clients().getCommon(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).getBasicUserInfo(accountHolder, productType, apiVersion, productSubscriptionKey, environment)
    }

    /**
     * Get the user information for a certain MTN MOMO User with consent
     */
    fun getUserInfoWithConsent(
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String
    ): Call<UserInfoWithConsent> {
        return Clients().getCommon(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).getUserInfoWithConsent(productType, apiVersion, productSubscriptionKey, environment)
    }

    /**
     * Sends a request to transfer to an account
     */
    fun transfer(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        uuid: String
    ): Call<Unit> {
        return Clients().getCommon(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).transfer(momoTransaction, apiVersion, productType, productSubscriptionKey, environment, uuid)
    }

    /**
     * Get the transfer status based on the transfer Id [referenceId]
     */
    fun getTransferStatus(
        referenceId: String,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Call<ResponseBody> {
        return Clients().getCommon(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).getTransferStatus(referenceId, apiVersion, productType, productSubscriptionKey, environment)
    }

    /**
     * Sends a request to pay a user. The user is identified by the [referenceId]
     */
    fun requestToPayDeliveryNotification(
        momoNotification: MomoNotification,
        referenceId: String,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Call<ResponseBody> {
        return Clients().getCommon(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).requestToPayDeliveryNotification(
            momoNotification,
            referenceId,
            apiVersion,
            productType,
            productSubscriptionKey,
            environment,
            momoNotification.notificationMessage
        )
    }

    /**
     * Validate an account status.
     */
    fun validateAccountHolderStatus(
        accountHolder: AccountHolder,
        apiVersion: String,
        productType: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Call<ResponseBody> {
        return Clients().getCommon(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).validateAccountHolderStatus(
            accountHolder.partyId,
            accountHolder.partyIdType,
            apiVersion,
            productType,
            productSubscriptionKey,
            environment
        )
    }

    fun requestToPay(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String
    ): Call<Unit> {
        return Clients().getCollection(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).requestToPay(momoTransaction, apiVersion, productSubscriptionKey, environment, uuid)
    }

    fun requestToPayTransactionStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Call<ResponseBody> {
        return Clients().getCollection(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).requestToPayTransactionStatus(referenceId, apiVersion, productSubscriptionKey, environment)
    }

    fun requestToWithdraw(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String
    ): Call<Unit> {
        return Clients().getCollection(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).requestToWithdraw(momoTransaction, apiVersion, productSubscriptionKey, environment, uuid)
    }

    fun requestToWithdrawTransactionStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Call<ResponseBody> {
        return Clients().getCollection(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).requestToWithdrawTransactionStatus(referenceId, apiVersion, productSubscriptionKey, environment)
    }

    fun deposit(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String
    ): Call<Unit> {
        return Clients().getDisbursement(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).deposit(momoTransaction, apiVersion, productSubscriptionKey, environment, uuid)
    }

    fun getDepositStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Call<ResponseBody> {
        return Clients().getDisbursement(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).getDepositStatus(referenceId, apiVersion, productSubscriptionKey, environment)
    }

    fun refund(
        accessToken: String,
        momoTransaction: MomoTransaction,
        apiVersion: String,
        productSubscriptionKey: String,
        uuid: String
    ): Call<Unit> {
        return Clients().getDisbursement(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).refund(momoTransaction, apiVersion, productSubscriptionKey, environment, uuid)
    }

    fun getRefundStatus(
        referenceId: String,
        apiVersion: String,
        productSubscriptionKey: String,
        accessToken: String
    ): Call<ResponseBody> {
        return Clients().getDisbursement(
            baseUrl,
            AccessTokenInterceptor(accessToken)
        ).getRefundStatus(referenceId, apiVersion, productSubscriptionKey, environment)
    }
}
