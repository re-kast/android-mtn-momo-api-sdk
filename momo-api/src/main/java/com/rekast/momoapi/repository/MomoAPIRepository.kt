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

import com.rekast.momoapi.model.api.AccountBalance
import com.rekast.momoapi.model.api.BasicUserInfo
import com.rekast.momoapi.model.api.UserInfoWithConsent
import com.rekast.momoapi.model.authentication.AccessToken
import com.rekast.momoapi.model.authentication.ApiUser
import com.rekast.momoapi.model.authentication.ApiUserKey
import com.rekast.momoapi.network.MomoApiClient
import com.rekast.momoapi.network.okhttp.AccessTokenInterceptor
import com.rekast.momoapi.network.okhttp.BasicAuthInterceptor
import retrofit2.Call

abstract class MomoAPIRepository(
    var apiUserId: String,
    var baseUrl: String,
) {
    /**
     * Check whether the supplied user exists
     */
    fun checkApiUser(
        productSubscriptionKey: String,
        apiUserId: String,
        apiVersion: String,
    ): Call<ApiUser> {
        return MomoApiClient().checkApiUser(baseUrl)
            .getApiUser(apiVersion, apiUserId, productSubscriptionKey)
    }

    /**
     * Get the API Key based on the User Id and OCP Subscription Id
     */
    fun getUserApiKey(
        productSubscriptionKey: String,
        apiUserId: String,
        apiVersion: String,
    ): Call<ApiUserKey> {
        return MomoApiClient().getApiUserKey(baseUrl)
            .getApiUserKey(apiVersion, apiUserId, productSubscriptionKey)
    }

    /**
     * Get the Access Token based on the User ID, OCP Subscription Id and the API Key
     */
    fun getAccessToken(
        productSubscriptionKey: String,
        apiUserId: String,
        apiKey: String,
        productType: String,
    ): Call<AccessToken> {
        return MomoApiClient().getAccessToken(
            baseUrl,
            BasicAuthInterceptor(apiUserId, apiKey),
        ).getAccessToken(productType, productSubscriptionKey)
    }

    /**
     * Gets the account balance of the entity/user initiating the transaction.
     */
    fun getAccountBalance(
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        environment: String,
    ): Call<AccountBalance> {
        return MomoApiClient().getAccountBalance(
            baseUrl,
            AccessTokenInterceptor(accessToken),
        ).getAccountBalance(productType, apiVersion, productSubscriptionKey, environment)
    }

    /**
     * Get the basic user information for a certain MTN MOMO User
     */
    fun getBasicUserInfo(
        accountHolder: String,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        environment: String,
    ): Call<BasicUserInfo> {
        return MomoApiClient().getBasicUserInfo(
            baseUrl,
            AccessTokenInterceptor(accessToken),
        ).getBasicUserInfo(accountHolder, productType, apiVersion, productSubscriptionKey, environment)
    }

    /**
     * Get the user information for a certain MTN MOMO User without consent
     */
    fun getUserInfoWithoutConsent(
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        environment: String,
    ): Call<UserInfoWithConsent> {
        return MomoApiClient().getUserInfoWithoutConsent(
            baseUrl,
            AccessTokenInterceptor(accessToken),
        ).getUserInfoWithoutConsent(productType, apiVersion, productSubscriptionKey, environment)
    }
}
