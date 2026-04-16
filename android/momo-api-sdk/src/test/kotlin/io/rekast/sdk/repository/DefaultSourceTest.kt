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

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.MomoNotification
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.ApiKey
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import io.rekast.sdk.network.service.AuthenticationService
import io.rekast.sdk.network.service.products.CommonService
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Unit tests for [DefaultSource].
 *
 * Each test verifies that [DefaultSource] delegates its call to the correct underlying service
 * ([AuthenticationService] or [CommonService]) and passes the expected parameters through.
 *
 * Both services are mocked with `relaxed = true` so that only the calls under test need to be
 * stubbed explicitly; all other invocations return safe defaults automatically.
 */
class DefaultSourceTest {

    private val authenticationService: AuthenticationService = mockk(relaxed = true)
    private val commonService: CommonService = mockk(relaxed = true)

    private lateinit var defaultSource: DefaultSource

    @Before
    fun setUp() {
        defaultSource = DefaultSource(authenticationService, commonService)
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private val apiUser = ApiUser(providerCallbackHost = "https://callback.example.com", targetEnvironment = "sandbox")
    private val apiKey = ApiKey(apiKey = "test-api-key")
    private val accessToken = AccessToken(accessToken = "access-token-value", tokenType = "Bearer", expiresIn = 3600)
    private val oauth2AccessToken = Oauth2AccessToken(
        accessToken = "oauth2-token-value",
        tokenType = "Bearer",
        expiresIn = 3600,
        scope = "profile",
        refreshToken = "refresh-token-value",
        refreshTokenExpiredIn = 7200
    )
    private val basicUserInfo = BasicUserInfo(
        sub = "sub-001",
        name = "Test User",
        givenName = "Test",
        familyName = "User",
        birthDate = "1990-01-01",
        locale = "en_US",
        gender = "male",
        updatedAt = 1700000000
    )
    private val userInfoWithConsent = UserInfoWithConsent(sub = "sub-001", name = "Test User")
    private val accountBalance = AccountBalance(availableBalance = "1000", currency = "EUR")
    private val responseBody: ResponseBody = "ok".toResponseBody("text/plain".toMediaType())
    private val providerCallBackHost = ProviderCallBackHost(providerCallbackHost = "https://callback.example.com")
    private val accountHolder = AccountHolder(partyIdType = "msisdn", partyId = "256700000000")
    private val momoTransaction = MomoTransaction(
        amount = "200",
        currency = "EUR",
        externalId = "ext-100",
        payerMessage = "Test payment",
        payeeNote = "Test note"
    )
    private val momoNotification = MomoNotification(notificationMessage = "Your payment was received")

    // ---------------------------------------------------------------------------
    // AuthenticationService delegation
    // ---------------------------------------------------------------------------

    /** Verifies createApiUser delegates to [AuthenticationService.createApiUser]. */
    @Test
    fun `createApiUser delegates to authenticationService`() = runTest {
        coEvery {
            authenticationService.createApiUser(any(), any(), any(), any())
        } returns Response.success(apiUser)

        defaultSource.createApiUser(providerCallBackHost, "v1_0", "uuid-001", "sub-key")

        coVerify(exactly = 1) {
            authenticationService.createApiUser(any(), any(), any(), any())
        }
    }

    /** Verifies getApiUser delegates to [AuthenticationService.getApiUser]. */
    @Test
    fun `getApiUser delegates to authenticationService`() = runTest {
        coEvery {
            authenticationService.getApiUser(any(), any(), any())
        } returns Response.success(apiUser)

        defaultSource.getApiUser("v1_0", "user-id-001", "sub-key")

        coVerify(exactly = 1) {
            authenticationService.getApiUser(any(), any(), any())
        }
    }

    /** Verifies createApiKey delegates to [AuthenticationService.createApiKey]. */
    @Test
    fun `createApiKey delegates to authenticationService`() = runTest {
        coEvery {
            authenticationService.createApiKey(any(), any(), any())
        } returns Response.success(apiKey)

        defaultSource.createApiKey("v1_0", "user-id-001", "sub-key")

        coVerify(exactly = 1) {
            authenticationService.createApiKey(any(), any(), any())
        }
    }

    /** Verifies getAccessToken delegates to [AuthenticationService.getAccessToken]. */
    @Test
    fun `getAccessToken delegates to authenticationService`() = runTest {
        coEvery {
            authenticationService.getAccessToken(any(), any())
        } returns Response.success(accessToken)

        defaultSource.getAccessToken("collection", "sub-key")

        coVerify(exactly = 1) {
            authenticationService.getAccessToken(any(), any())
        }
    }

    /** Verifies getOauth2AccessToken delegates to [AuthenticationService.getOauth2AccessToken]. */
    @Test
    fun `getOauth2AccessToken delegates to authenticationService`() = runTest {
        coEvery {
            authenticationService.getOauth2AccessToken(any(), any(), any())
        } returns Response.success(oauth2AccessToken)

        defaultSource.getOauth2AccessToken("collection", "sub-key", "sandbox")

        coVerify(exactly = 1) {
            authenticationService.getOauth2AccessToken(any(), any(), any())
        }
    }

    // ---------------------------------------------------------------------------
    // CommonService delegation
    // ---------------------------------------------------------------------------

    /** Verifies getBasicUserInfo delegates to [CommonService.getBasicUserInfo]. */
    @Test
    fun `getBasicUserInfo delegates to commonService`() = runTest {
        coEvery {
            commonService.getBasicUserInfo(any(), any(), any(), any(), any())
        } returns Response.success(basicUserInfo)

        defaultSource.getBasicUserInfo("collection", "v1_0", "256700000000", "sub-key", "sandbox")

        coVerify(exactly = 1) {
            commonService.getBasicUserInfo(any(), any(), any(), any(), any())
        }
    }

    /**
     * Verifies validateAccountHolderStatus delegates to [CommonService.validateAccountHolderStatus]
     * and that accountHolderId and accountHolderType are mapped from the [AccountHolder] fields.
     */
    @Test
    fun `validateAccountHolderStatus delegates to commonService with correct accountHolderId and type`() = runTest {
        coEvery {
            commonService.validateAccountHolderStatus(any(), any(), any(), any(), any(), any())
        } returns Response.success(responseBody)

        defaultSource.validateAccountHolderStatus("collection", "v1_0", accountHolder, "sub-key", "sandbox")

        coVerify(exactly = 1) {
            commonService.validateAccountHolderStatus(
                any(),
                any(),
                accountHolderId = accountHolder.partyId,
                accountHolderType = accountHolder.partyIdType,
                any(),
                any()
            )
        }
    }

    /** Verifies getAccountBalance delegates to [CommonService.getAccountBalance]. */
    @Test
    fun `getAccountBalance delegates to commonService`() = runTest {
        coEvery {
            commonService.getAccountBalance(any(), any(), any(), any())
        } returns Response.success(accountBalance)

        defaultSource.getAccountBalance("collection", "v1_0", "sub-key", "sandbox")

        coVerify(exactly = 1) {
            commonService.getAccountBalance(any(), any(), any(), any())
        }
    }

    /** Verifies getAccountBalanceInSpecificCurrency delegates to [CommonService.getAccountBalanceInSpecificCurrency]. */
    @Test
    fun `getAccountBalanceInSpecificCurrency delegates to commonService`() = runTest {
        coEvery {
            commonService.getAccountBalanceInSpecificCurrency(any(), any(), any(), any(), any())
        } returns Response.success(accountBalance)

        defaultSource.getAccountBalanceInSpecificCurrency("collection", "v1_0", "EUR", "sub-key", "sandbox")

        coVerify(exactly = 1) {
            commonService.getAccountBalanceInSpecificCurrency(any(), any(), any(), any(), any())
        }
    }

    /** Verifies getUserInfoWithConsent delegates to [CommonService.getUserInfoWithConsent]. */
    @Test
    fun `getUserInfoWithConsent delegates to commonService`() = runTest {
        coEvery {
            commonService.getUserInfoWithConsent(any(), any(), any(), any())
        } returns Response.success(userInfoWithConsent)

        defaultSource.getUserInfoWithConsent("collection", "v1_0", "sub-key", "sandbox")

        coVerify(exactly = 1) {
            commonService.getUserInfoWithConsent(any(), any(), any(), any())
        }
    }

    /** Verifies transfer delegates to [CommonService.transfer]. */
    @Test
    fun `transfer delegates to commonService`() = runTest {
        coEvery {
            commonService.transfer(any(), any(), any(), any(), any(), any())
        } returns Response.success(Unit)

        defaultSource.transfer("collection", "v1_0", momoTransaction, "uuid-t-001", "sub-key", "sandbox")

        coVerify(exactly = 1) {
            commonService.transfer(any(), any(), any(), any(), any(), any())
        }
    }

    /** Verifies getTransferStatus delegates to [CommonService.getTransferStatus]. */
    @Test
    fun `getTransferStatus delegates to commonService`() = runTest {
        coEvery {
            commonService.getTransferStatus(any(), any(), any(), any(), any())
        } returns Response.success(responseBody)

        defaultSource.getTransferStatus("collection", "v1_0", "ref-001", "sub-key", "sandbox")

        coVerify(exactly = 1) {
            commonService.getTransferStatus(any(), any(), any(), any(), any())
        }
    }

    /**
     * Verifies requestToPayDeliveryNotification delegates to
     * [CommonService.requestToPayDeliveryNotification] and passes notificationMessage from
     * the [MomoNotification] body.
     */
    @Test
    fun `requestToPayDeliveryNotification delegates to commonService with correct notificationMessage`() = runTest {
        coEvery {
            commonService.requestToPayDeliveryNotification(any(), any(), any(), any(), any(), any(), any())
        } returns Response.success(responseBody)

        defaultSource.requestToPayDeliveryNotification(
            "collection", "v1_0", "ref-001", momoNotification, "sub-key", "sandbox"
        )

        coVerify(exactly = 1) {
            commonService.requestToPayDeliveryNotification(
                any(),
                any(),
                any(),
                any(),
                notificationMessage = momoNotification.notificationMessage,
                any(),
                any()
            )
        }
    }
}
