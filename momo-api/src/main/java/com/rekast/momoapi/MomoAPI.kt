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

import com.rekast.momoapi.callback.MomoAPICallback
import com.rekast.momoapi.callback.MomoAPIResult
import com.rekast.momoapi.model.api.AccountBalance
import com.rekast.momoapi.model.api.BasicUserInfo
import com.rekast.momoapi.model.api.UserInfoWithConsent
import com.rekast.momoapi.model.authentication.AccessToken
import com.rekast.momoapi.model.authentication.ApiUser
import com.rekast.momoapi.model.authentication.ApiUserKey
import com.rekast.momoapi.repository.MomoAPIRepository
import com.rekast.momoapi.utils.Constants
import com.rekast.momoapi.utils.ProductType

/**
 * Prepares the different MOMO API requests
 */
object MomoAPI {
    lateinit var apiUserId: String
    lateinit var productType: ProductType
    lateinit var baseURL: String
    lateinit var environment: String
    lateinit var momoAPIRepository: MomoAPIRepository
    fun builder(apiUserId: String): MomoAPIBuilder = MomoAPIBuilder(apiUserId)

    /**
     * Prepares to fetch the API User
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiUserId] -- The API User ID (X-Reference-Id)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[callback] -- The request callback. Returns @see [ApiUser]
     */
    fun checkApiUser(
        productSubscriptionKey: String,
        apiUserId: String,
        apiVersion: String,
        callback: ((momoAPIResult: MomoAPIResult<ApiUser>) -> Unit),
    ) {
        momoAPIRepository.checkApiUser(productSubscriptionKey, apiUserId, apiVersion)
            .enqueue(MomoAPICallback(callback))
    }

    /**
     * Prepares to fetch the API Key
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiUserId] -- The API User ID (X-Reference-Id)
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[callback] -- The request callback. Returns @see [ApiUserKey]
     */
    fun getUserApiKey(
        productSubscriptionKey: String,
        apiUserId: String,
        apiVersion: String,
        callback: ((momoAPIResult: MomoAPIResult<ApiUserKey>) -> Unit),
    ) {
        momoAPIRepository.getUserApiKey(productSubscriptionKey, apiUserId, apiVersion)
            .enqueue(MomoAPICallback(callback))
    }

    /**
     * Prepares to fetch the Access Token
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[apiUserId] -- The API User ID (X-Reference-Id)
     * @param[apiKey] -- The API Key fetched here [MomoAPI.getUserApiKey]
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getAccessToken(
        productSubscriptionKey: String,
        apiUserId: String,
        apiKey: String,
        productType: String,
        callback: ((momoAPIResult: MomoAPIResult<AccessToken>) -> Unit),
    ) {
        momoAPIRepository.getAccessToken(productSubscriptionKey, apiUserId, apiKey, productType)
            .enqueue(MomoAPICallback(callback))
    }

    /**
     * Prepares to fetch the Balance
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[environment] -- The API environment (X-Target-Environment)
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getBalance(
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        environment: String,
        callback: ((momoAPIResult: MomoAPIResult<AccountBalance>) -> Unit),
    ) {
        momoAPIRepository.getAccountBalance(productSubscriptionKey, accessToken, apiVersion, productType, environment)
            .enqueue(MomoAPICallback(callback))
    }

    /**
     * Prepares to fetch the Basic User Information
     * @param[accountHolder]  -- The account holder's msisdn
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[environment] -- The API environment (X-Target-Environment)
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    fun getBasicUserInfo(
        accountHolder: String,
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        environment: String,
        callback: ((momoAPIResult: MomoAPIResult<BasicUserInfo>) -> Unit),
    ) {
        momoAPIRepository.getBasicUserInfo(
            accountHolder,
            productSubscriptionKey,
            accessToken,
            apiVersion,
            productType,
            environment,
        ).enqueue(MomoAPICallback(callback))
    }

    /**
     * Prepares to fetch the User Information without consent
     * @param[productSubscriptionKey]  -- The Product subscription Key (Ocp-Apim-Subscription-Key)
     * @param[accessToken] -- The Access Token fetched here [MomoAPI.getAccessToken]
     * @param[apiVersion] -- The app Version (v1_0 or v2_0)
     * @param[productType] -- The API Products ([Constants.ProductTypes])
     * @param[environment] -- The API environment (X-Target-Environment)
     * @param[callback] -- The request callback. Returns @see [AccessToken]
     */
    // API Doesn't seem to work
    fun getUserInfoWithoutConsent(
        productSubscriptionKey: String,
        accessToken: String,
        apiVersion: String,
        productType: String,
        environment: String,
        callback: ((momoAPIResult: MomoAPIResult<UserInfoWithConsent>) -> Unit),
    ) {
        momoAPIRepository.getUserInfoWithoutConsent(
            productSubscriptionKey,
            accessToken,
            apiVersion,
            productType,
            environment,
        ).enqueue(MomoAPICallback(callback))
    }
}
