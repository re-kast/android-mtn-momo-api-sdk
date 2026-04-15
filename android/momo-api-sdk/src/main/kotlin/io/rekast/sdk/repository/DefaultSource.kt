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
package io.rekast.sdk.repository

import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.MomoNotification
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.network.service.products.CommonService
import javax.inject.Inject

/**
 * DefaultSource is responsible for handling API calls related to user management
 * and authentication in the MTN MOMO SDK.
 *
 * This class acts as a bridge between the repository and the authentication service,
 * providing methods to create API users, retrieve user details, create API keys,
 * and obtain access tokens.
 *
 * @property authenticationService The service for handling authentication-related API calls.
 * @property commonService The service for handling common API calls.
 */
class DefaultSource @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val commonService: CommonService
) {

    /**
     * Creates a new API user.
     *
     * @param providerCallBackHost The callback host for the provider.
     * @param apiVersion The version of the API to use.
     * @param uuid A unique identifier for the request.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Response] containing the created [io.rekast.sdk.model.authentication.ApiUser].
     */
    suspend fun createApiUser(
        providerCallBackHost: ProviderCallBackHost,
        apiVersion: String,
        uuid: String,
        productSubscriptionKey: String
    ) = authenticationService.createApiUser(
        providerCallBackHost = providerCallBackHost,
        apiVersion = apiVersion,
        uuid = uuid,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves the details of an existing API user.
     *
     * @param apiVersion The version of the API to use.
     * @param userId The ID of the API user to retrieve.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Response] containing the requested [io.rekast.sdk.model.authentication.ApiUser].
     */
    suspend fun getApiUser(
        apiVersion: String,
        userId: String,
        productSubscriptionKey: String
    ) = authenticationService.getApiUser(
        apiVersion = apiVersion,
        apiUser = userId,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Creates a new API key for the specified API user.
     *
     * @param apiVersion The version of the API to use.
     * @param userId The ID of the API user for whom to create the key.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Response] containing the generated [io.rekast.sdk.model.authentication.ApiKey].
     */
    suspend fun createApiKey(
        apiVersion: String,
        userId: String,
        productSubscriptionKey: String
    ) = authenticationService.createApiKey(
        apiVersion = apiVersion,
        apiUser = userId,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Obtains an access token for the specified product type.
     *
     * @param productType The type of product for which to obtain the access token.
     * @param productSubscriptionKey The subscription key for the product.
     * @return A [Response] containing the obtained [io.rekast.sdk.model.authentication.AccessToken].
     */
    suspend fun getAccessToken(
        productType: String,
        productSubscriptionKey: String
    ) = authenticationService.getAccessToken(
        productType = productType,
        productSubscriptionKey = productSubscriptionKey
    )

    /**
     * Retrieves an OAuth2 access token for the specified product type.
     *
     * This function interacts with the authentication service to obtain an access token
     * that can be used for subsequent API calls. The access token is essential for
     * authenticating requests to the MTN MOMO API.
     *
     * @param productType The type of product for which to obtain the OAuth2 access token.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] containing the obtained [io.rekast.sdk.model.authentication.Oauth2AccessToken].
     */
    suspend fun getOauth2AccessToken(productType: String, productSubscriptionKey: String, environment: String) = authenticationService.getOauth2AccessToken(
        productType = productType,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )

    /**
     * Retrieves the basic user information for a specified MTN MOMO user.
     *
     * @param productType The type of product for which to retrieve the user information.
     * @param apiVersion The version of the API to use.
     * @param accountHolder The identifier for the account holder.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] containing the [io.rekast.sdk.model.BasicUserInfo] of the specified user.
     */
    suspend fun getBasicUserInfo(
        productType: String,
        apiVersion: String,
        accountHolder: String,
        productSubscriptionKey: String,
        environment: String
    ) = commonService.getBasicUserInfo(
        productType = productType,
        apiVersion = apiVersion,
        accountHolder = accountHolder,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )

    /**
     * Validates the status of an account holder.
     *
     * @param productType The type of product for which to validate the account holder.
     * @param apiVersion The version of the API to use.
     * @param accountHolder The account holder details.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] indicating the result of the account holder status validation.
     */
    suspend fun validateAccountHolderStatus(
        productType: String,
        apiVersion: String,
        accountHolder: AccountHolder,
        productSubscriptionKey: String,
        environment: String
    ) = commonService.validateAccountHolderStatus(
        productType = productType,
        apiVersion = apiVersion,
        accountHolderId = accountHolder.partyId,
        accountHolderType = accountHolder.partyIdType,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )

    /**
     * Retrieves the account balance for a specified product type. This only works with the [ProductType.COLLECTION]. It seems to break with the other API product type.
     *
     * @param productType The type of product for which to retrieve the account balance.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] containing the [io.rekast.sdk.model.AccountBalance].
     */
    suspend fun getAccountBalance(
        productType: String,
        apiVersion: String,
        productSubscriptionKey: String,
        environment: String
    ) = commonService.getAccountBalance(
        productType = productType,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )

    /**
     * Retrieves the account balance in a specific currency. This only works with the [ProductType.COLLECTION]. It seems to break with the other API product type.
     *
     * @param productType The type of product for which to retrieve the account balance.
     * @param apiVersion The version of the API to use.
     * @param currency The currency for which to retrieve the account balance.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] containing the [io.rekast.sdk.model.AccountBalance].
     */
    suspend fun getAccountBalanceInSpecificCurrency(
        productType: String,
        apiVersion: String,
        currency: String,
        productSubscriptionKey: String,
        environment: String
    ) = commonService.getAccountBalanceInSpecificCurrency(
        productType = productType,
        apiVersion = apiVersion,
        currency = currency,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )

    /**
     * Retrieves user information with consent for a specified product type.
     *
     * @param productType The type of product for which to retrieve the user information.
     * @param apiVersion The version of the API to use.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] containing the user information with consent.
     */
    suspend fun getUserInfoWithConsent(
        productType: String,
        apiVersion: String,
        productSubscriptionKey: String,
        environment: String
    ) = commonService.getUserInfoWithConsent(
        productType = productType,
        apiVersion = apiVersion,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )

    /**
     * Initiates a transfer for a specified product type.
     *
     * @param productType The type of product for which to initiate the transfer.
     * @param apiVersion The version of the API to use.
     * @param momoTransaction The transaction details.
     * @param uuid A unique identifier for the request.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] indicating the result of the transfer.
     */
    suspend fun transfer(
        productType: String,
        apiVersion: String,
        momoTransaction: MomoTransaction,
        uuid: String,
        productSubscriptionKey: String,
        environment: String
    ) = commonService.transfer(
        productType = productType,
        apiVersion = apiVersion,
        momoTransaction = momoTransaction,
        uuid = uuid,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )

    /**
     * Retrieves the status of a transfer for a specified product type.
     *
     * @param productType The type of product for which to retrieve the transfer status.
     * @param apiVersion The version of the API to use.
     * @param referenceId The reference ID of the transfer.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] containing the transfer status.
     */
    suspend fun getTransferStatus(
        productType: String,
        apiVersion: String,
        referenceId: String,
        productSubscriptionKey: String,
        environment: String
    ) = commonService.getTransferStatus(
        productType = productType,
        apiVersion = apiVersion,
        referenceId = referenceId,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )

    /**
     * Sends a delivery notification for a request to pay.
     *
     * @param productType The type of product for which to send the notification.
     * @param apiVersion The version of the API to use.
     * @param referenceId The reference ID of the request to pay.
     * @param momoNotification The notification details.
     * @param productSubscriptionKey The subscription key for the product.
     * @param environment The API environment (e.g., production, sandbox).
     * @return A [Response] indicating the result of the notification request.
     */
    suspend fun requestToPayDeliveryNotification(
        productType: String,
        apiVersion: String,
        referenceId: String,
        momoNotification: MomoNotification,
        productSubscriptionKey: String,
        environment: String
    ) = commonService.requestToPayDeliveryNotification(
        productType = productType,
        apiVersion = apiVersion,
        referenceId = referenceId,
        momoNotification = momoNotification,
        notificationMessage = momoNotification.notificationMessage,
        productSubscriptionKey = productSubscriptionKey,
        environment = environment
    )
}
