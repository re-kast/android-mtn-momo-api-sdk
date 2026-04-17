/*
 * Copyright 2023-2026, Benjamin Mwalimu
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
import io.rekast.sdk.model.BackChannelAuthorize
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.BcAuthorizeRequest
import io.rekast.sdk.model.CashTransfer
import io.rekast.sdk.model.Invoice
import io.rekast.sdk.model.MomoNotification
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.model.PreApproval
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.ApiKey
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.DisbursementsService
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.utils.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Unit tests for [DefaultRepository].
 *
 * Verifies that:
 * - Every flow-based method emits [NetworkResult.Loading] as its first item.
 * - Flow-based methods emit [NetworkResult.Success] when the underlying source call succeeds.
 * - Flow-based methods emit [NetworkResult.Error] when the source call returns an HTTP error.
 * - [DefaultRepository.getAccountBalance] routes to the correct [DefaultSource] method depending
 *   on whether the currency argument is non-null/non-blank or null/blank.
 *
 * [CollectionService] and [DisbursementsService] are sealed interfaces that cannot be mocked by
 * MockK. Real Retrofit stubs are used to satisfy the constructor; only [DefaultSource] (a plain
 * class) is mocked for the flow-based assertions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DefaultRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val defaultSource: DefaultSource = mockk(relaxed = true)

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
        .build()
    private val disbursementsService: DisbursementsService = retrofit.create(DisbursementsService::class.java)
    private val collection: CollectionService = retrofit.create(CollectionService::class.java)

    private val config = ApiConfig(
        baseUrl = "https://sandbox.momodeveloper.mtn.com/",
        apiUserId = "test-user-id",
        environment = "sandbox"
    )

    private lateinit var repository: DefaultRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = DefaultRepository(defaultSource, disbursementsService, collection, config)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val providerCallBackHost = ProviderCallBackHost(providerCallbackHost = "https://callback.example.com")
    private val apiUser = ApiUser(providerCallbackHost = "https://callback.example.com", targetEnvironment = "sandbox")

    private fun sampleTransaction() = MomoTransaction(
        amount = "100",
        currency = "EUR",
        externalId = "ext-001",
        payerMessage = "Payment",
        payeeNote = "Note"
    )

    /**
     * Verifies that [DefaultRepository.createApiUser] emits [NetworkResult.Loading] as the very
     * first item in the flow, ensuring callers can show a loading indicator immediately.
     */
    @Test
    fun `createApiUser emits Loading as first emission`() = runTest {
        coEvery {
            defaultSource.createApiUser(any(), any(), any(), any())
        } returns Response.success(apiUser)

        val results = repository.createApiUser(providerCallBackHost, "v1_0", "uuid-001", "sub-key").toList()

        assertTrue(results.first() is NetworkResult.Loading)
    }

    /**
     * Verifies that [DefaultRepository.createApiUser] emits [NetworkResult.Success] as the last
     * item when the underlying source call returns a 2xx response, and that the response body is
     * accessible via [NetworkResult.response].
     */
    @Test
    fun `createApiUser emits Success when source returns successful response`() = runTest {
        coEvery {
            defaultSource.createApiUser(any(), any(), any(), any())
        } returns Response.success(apiUser)

        val results = repository.createApiUser(providerCallBackHost, "v1_0", "uuid-001", "sub-key").toList()

        assertTrue(results.last() is NetworkResult.Success)
        assertEquals(apiUser, results.last().response)
    }

    /**
     * Verifies that [DefaultRepository.createApiUser] emits [NetworkResult.Error] as the last
     * item when the source returns an HTTP 404, and that the error message contains the status code.
     */
    @Test
    fun `createApiUser emits Error when source returns HTTP error`() = runTest {
        coEvery {
            defaultSource.createApiUser(any(), any(), any(), any())
        } returns Response.error(404, "error".toResponseBody("text/plain".toMediaType()))

        val results = repository.createApiUser(providerCallBackHost, "v1_0", "uuid-001", "sub-key").toList()

        assertTrue(results.last() is NetworkResult.Error)
        assertTrue(results.last().message!!.contains("404"))
    }

    /**
     * Verifies that [DefaultRepository.getAccountBalance] routes to
     * [DefaultSource.getAccountBalanceInSpecificCurrency] when the currency argument is
     * non-null and non-blank, and that the flow emits [NetworkResult.Success].
     */
    @Test
    fun `getAccountBalance with non-null currency calls getAccountBalanceInSpecificCurrency`() = runTest {
        val balance = AccountBalance(availableBalance = "500", currency = "EUR")
        coEvery {
            defaultSource.getAccountBalanceInSpecificCurrency(any(), any(), any(), any(), any())
        } returns Response.success(balance)

        val results = repository.getAccountBalance("collection", "v1_0", "EUR", "sub-key", "sandbox").toList()

        assertTrue(results.any { it is NetworkResult.Success })
    }

    /**
     * Verifies that [DefaultRepository.getAccountBalance] routes to [DefaultSource.getAccountBalance]
     * (the no-currency variant) when the currency argument is `null`, and the flow emits
     * [NetworkResult.Success].
     */
    @Test
    fun `getAccountBalance with null currency emits Success from getAccountBalance`() = runTest {
        val balance = AccountBalance(availableBalance = "500", currency = "EUR")
        coEvery {
            defaultSource.getAccountBalance(any(), any(), any(), any())
        } returns Response.success(balance)

        val results = repository.getAccountBalance("collection", "v1_0", null, "sub-key", "sandbox").toList()

        assertTrue(results.any { it is NetworkResult.Success })
    }

    /**
     * Verifies that [DefaultRepository.getAccountBalance] routes to [DefaultSource.getAccountBalance]
     * when the currency argument is a blank string, treating whitespace the same as null.
     */
    @Test
    fun `getAccountBalance with blank currency emits Success from getAccountBalance`() = runTest {
        val balance = AccountBalance(availableBalance = "500", currency = "EUR")
        coEvery {
            defaultSource.getAccountBalance(any(), any(), any(), any())
        } returns Response.success(balance)

        val results = repository.getAccountBalance("collection", "v1_0", "   ", "sub-key", "sandbox").toList()

        assertTrue(results.any { it is NetworkResult.Success })
    }

    /**
     * Verifies that [DefaultRepository.checkApiUser] emits [NetworkResult.Loading] as its first
     * item, then [NetworkResult.Success] when the source returns a successful response.
     */
    @Test
    fun `checkApiUser emits Loading then Success`() = runTest {
        coEvery {
            defaultSource.getApiUser(any(), any(), any())
        } returns Response.success(apiUser)

        val results = repository.checkApiUser("v1_0", "sub-key").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.checkApiUser] emits [NetworkResult.Error] when the source
     * returns a 404 response.
     */
    @Test
    fun `checkApiUser emits Error on 404`() = runTest {
        coEvery {
            defaultSource.getApiUser(any(), any(), any())
        } returns Response.error(404, "not found".toResponseBody("text/plain".toMediaType()))

        val results = repository.checkApiUser("v1_0", "sub-key").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /**
     * Verifies that [DefaultRepository.createApiKey] emits [NetworkResult.Loading] as its first
     * item, then [NetworkResult.Success] containing the [ApiKey] on a successful response.
     */
    @Test
    fun `createApiKey emits Loading then Success`() = runTest {
        val apiKey = ApiKey(apiKey = "test-key-value")
        coEvery {
            defaultSource.createApiKey(any(), any(), any())
        } returns Response.success(apiKey)

        val results = repository.createApiKey("v1_0", "sub-key").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
        assertEquals(apiKey, results.last().response)
    }

    /**
     * Verifies that [DefaultRepository.createApiKey] emits [NetworkResult.Error] when the source
     * returns a non-2xx response.
     */
    @Test
    fun `createApiKey emits Error on failure`() = runTest {
        coEvery {
            defaultSource.createApiKey(any(), any(), any())
        } returns Response.error(401, "unauthorized".toResponseBody("text/plain".toMediaType()))

        val results = repository.createApiKey("v1_0", "sub-key").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /**
     * Verifies that [DefaultRepository.getAccessToken] emits [NetworkResult.Loading] as its first
     * item, then [NetworkResult.Success] containing the [AccessToken] on a successful response.
     */
    @Test
    fun `getAccessToken emits Loading then Success`() = runTest {
        val accessToken = AccessToken(accessToken = "tok", tokenType = "Bearer", expiresIn = 3600)
        coEvery {
            defaultSource.getAccessToken(any(), any())
        } returns Response.success(accessToken)

        val results = repository.getAccessToken("sub-key", "remittance").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
        assertEquals(accessToken, results.last().response)
    }

    /**
     * Verifies that [DefaultRepository.getAccessToken] emits [NetworkResult.Error] when the source
     * returns a non-2xx response.
     */
    @Test
    fun `getAccessToken emits Error on failure`() = runTest {
        coEvery {
            defaultSource.getAccessToken(any(), any())
        } returns Response.error(403, "forbidden".toResponseBody("text/plain".toMediaType()))

        val results = repository.getAccessToken("sub-key", "remittance").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /**
     * Verifies that [DefaultRepository.getOauthAccessToken] emits [NetworkResult.Loading] as its
     * first item, then [NetworkResult.Success] containing the [Oauth2AccessToken].
     */
    @Test
    fun `getOauthAccessToken emits Loading then Success`() = runTest {
        val oauth = Oauth2AccessToken(
            accessToken = "oauth-tok",
            tokenType = "Bearer",
            expiresIn = 3600,
            scope = "profile",
            refreshToken = "refresh-tok",
            refreshTokenExpiredIn = 7200
        )
        coEvery {
            defaultSource.getOauth2AccessToken(any(), any(), any(), any())
        } returns Response.success(oauth)

        val results = repository.getOauthAccessToken("remittance", "sub-key", "sandbox", "auth-req-id-001").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
        assertEquals(oauth, results.last().response)
    }

    /**
     * Verifies that [DefaultRepository.getOauthAccessToken] emits [NetworkResult.Error] immediately
     * when [backChannelAuthorizationRequestId] is blank, without touching the network.
     */
    @Test
    fun `getOauthAccessToken emits Error immediately when authReqId is blank`() = runTest {
        val results = repository.getOauthAccessToken("remittance", "sub-key", "sandbox", "").toList()

        assertTrue(results.size == 1)
        assertTrue(results.first() is NetworkResult.Error)
        coVerify(exactly = 0) { defaultSource.getOauth2AccessToken(any(), any(), any(), any()) }
    }

    /**
     * Verifies that [DefaultRepository.getBasicUserInfo] emits [NetworkResult.Loading] then
     * [NetworkResult.Success] containing the [BasicUserInfo].
     */
    @Test
    fun `getBasicUserInfo emits Loading then Success`() = runTest {
        val info = BasicUserInfo(
            sub = "sub-001",
            name = "Test User",
            givenName = "Test",
            familyName = "User",
            birthDate = "1990-01-01",
            locale = "en",
            gender = "male",
            updatedAt = 0
        )
        coEvery {
            defaultSource.getBasicUserInfo(any(), any(), any(), any(), any())
        } returns Response.success(info)

        val results = repository.getBasicUserInfo("collection", "v1_0", "256700000000", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.getUserInfoWithConsent] emits [NetworkResult.Loading] then
     * [NetworkResult.Success] containing the [UserInfoWithConsent].
     */
    @Test
    fun `getUserInfoWithConsent emits Loading then Success`() = runTest {
        val info = UserInfoWithConsent(sub = "sub-001", name = "Test User")
        coEvery {
            defaultSource.getUserInfoWithConsent(any(), any(), any(), any())
        } returns Response.success(info)

        val results = repository.getUserInfoWithConsent("collection", "v1_0", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.validateAccountHolderStatus] emits [NetworkResult.Loading]
     * then [NetworkResult.Success] containing a [ResponseBody].
     */
    @Test
    fun `validateAccountHolderStatus emits Loading then Success`() = runTest {
        val body = mockk<ResponseBody>(relaxed = true)
        val accountHolder = AccountHolder(partyIdType = "msisdn", partyId = "256700000000")
        coEvery {
            defaultSource.validateAccountHolderStatus(any(), any(), any(), any(), any())
        } returns Response.success(body)

        val results = repository.validateAccountHolderStatus("collection", "v1_0", accountHolder, "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.validateAccountHolderStatus] emits [NetworkResult.Error]
     * when the source returns a non-2xx response.
     */
    @Test
    fun `validateAccountHolderStatus emits Error on failure`() = runTest {
        val accountHolder = AccountHolder(partyIdType = "msisdn", partyId = "256700000000")
        coEvery {
            defaultSource.validateAccountHolderStatus(any(), any(), any(), any(), any())
        } returns Response.error(404, "not found".toResponseBody("text/plain".toMediaType()))

        val results = repository.validateAccountHolderStatus("collection", "v1_0", accountHolder, "sub-key", "sandbox").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /**
     * Verifies that [DefaultRepository.transfer] emits [NetworkResult.Loading] then
     * [NetworkResult.Success] when the source returns HTTP 202.
     */
    @Test
    fun `transfer emits Loading then Success`() = runTest {
        coEvery {
            defaultSource.transfer(any(), any(), any(), any(), any(), any())
        } returns Response.success(Unit)

        val results = repository.transfer("remittance", "v1_0", sampleTransaction(), "uuid-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.transfer] emits [NetworkResult.Error] when the source
     * returns a non-2xx response.
     */
    @Test
    fun `transfer emits Error on failure`() = runTest {
        coEvery {
            defaultSource.transfer(any(), any(), any(), any(), any(), any())
        } returns Response.error(500, "server error".toResponseBody("text/plain".toMediaType()))

        val results = repository.transfer("remittance", "v1_0", sampleTransaction(), "uuid-001", "sub-key", "sandbox").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /**
     * Verifies that [DefaultRepository.getTransferStatus] emits [NetworkResult.Loading] then
     * [NetworkResult.Success] containing a [ResponseBody].
     */
    @Test
    fun `getTransferStatus emits Loading then Success`() = runTest {
        val body = mockk<ResponseBody>(relaxed = true)
        coEvery {
            defaultSource.getTransferStatus(any(), any(), any(), any(), any())
        } returns Response.success(body)

        val results = repository.getTransferStatus("remittance", "v1_0", "ref-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.requestToPayDeliveryNotification] emits
     * [NetworkResult.Loading] then [NetworkResult.Success] containing a [ResponseBody].
     */
    @Test
    fun `requestToPayDeliveryNotification emits Loading then Success`() = runTest {
        val body = mockk<ResponseBody>(relaxed = true)
        val notification = MomoNotification(notificationMessage = "Your payment was received")
        coEvery {
            defaultSource.requestToPayDeliveryNotification(any(), any(), any(), any(), any(), any())
        } returns Response.success(body)

        val results = repository.requestToPayDeliveryNotification(
            "collection",
            "v1_0",
            "ref-001",
            notification,
            "sub-key",
            "sandbox"
        ).toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.requestToPayDeliveryNotification] emits [NetworkResult.Error]
     * when the source returns a non-2xx response.
     */
    @Test
    fun `requestToPayDeliveryNotification emits Error on failure`() = runTest {
        val notification = MomoNotification(notificationMessage = "Payment received")
        coEvery {
            defaultSource.requestToPayDeliveryNotification(any(), any(), any(), any(), any(), any())
        } returns Response.error(404, "not found".toResponseBody("text/plain".toMediaType()))

        val results = repository.requestToPayDeliveryNotification(
            "collection",
            "v1_0",
            "ref-001",
            notification,
            "sub-key",
            "sandbox"
        ).toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /**
     * Verifies that [DefaultRepository.bcAuthorize] emits [NetworkResult.Loading] then
     * [NetworkResult.Success] containing the [BackChannelAuthorize] response.
     */
    @Test
    fun `bcAuthorize emits Loading then Success`() = runTest {
        val bcAuth = BackChannelAuthorize(authReqId = "auth-req-001", interval = 5, expiresIn = 120)
        val request = BcAuthorizeRequest(loginHint = "MSISDN:256700000000", scope = "profile openid", accessType = "online")
        coEvery {
            defaultSource.bcAuthorize(any(), any(), any(), any(), any())
        } returns Response.success(bcAuth)

        val results = repository.bcAuthorize("collection", "v1_0", request, "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
        assertEquals(bcAuth, results.last().response)
    }

    /**
     * Verifies that [DefaultRepository.bcAuthorize] emits [NetworkResult.Error] when the source
     * returns a non-2xx response.
     */
    @Test
    fun `bcAuthorize emits Error on failure`() = runTest {
        val request = BcAuthorizeRequest(loginHint = "MSISDN:256700000000", scope = "profile openid", accessType = "online")
        coEvery {
            defaultSource.bcAuthorize(any(), any(), any(), any(), any())
        } returns Response.error(403, "forbidden".toResponseBody("text/plain".toMediaType()))

        val results = repository.bcAuthorize("collection", "v1_0", request, "sub-key", "sandbox").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    private fun sampleInvoice() = Invoice(externalId = "inv-001", amount = "100", currency = "EUR")
    private fun samplePreApproval() = PreApproval(
        payer = AccountHolder(partyIdType = "MSISDN", partyId = "256700000000"),
        payerCurrency = "EUR",
        validityTime = 3600
    )
    private fun sampleCashTransfer() = CashTransfer(
        amount = "200",
        currency = "EUR",
        externalId = "ct-001",
        payee = AccountHolder(partyIdType = "MSISDN", partyId = "256700000001"),
        payerMessage = "Transfer",
        payeeNote = "Received"
    )

    /** Verifies that [DefaultRepository.createInvoice] emits Loading then Success on HTTP 202. */
    @Test
    fun `createInvoice emits Loading then Success`() = runTest {
        coEvery { defaultSource.createInvoice(any(), any(), any(), any(), any()) } returns Response.success(Unit)

        val results = repository.createInvoice("v1_0", sampleInvoice(), "uuid-inv-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /** Verifies that [DefaultRepository.createInvoice] emits Error on a non-2xx response. */
    @Test
    fun `createInvoice emits Error on failure`() = runTest {
        coEvery { defaultSource.createInvoice(any(), any(), any(), any(), any()) } returns
            Response.error(400, "bad request".toResponseBody("text/plain".toMediaType()))

        val results = repository.createInvoice("v1_0", sampleInvoice(), "uuid-inv-001", "sub-key", "sandbox").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /** Verifies that [DefaultRepository.getInvoiceStatus] emits Loading then Success. */
    @Test
    fun `getInvoiceStatus emits Loading then Success`() = runTest {
        val body = mockk<ResponseBody>(relaxed = true)
        coEvery { defaultSource.getInvoiceStatus(any(), any(), any(), any()) } returns Response.success(body)

        val results = repository.getInvoiceStatus("v1_0", "inv-ref-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /** Verifies that [DefaultRepository.cancelInvoice] emits Loading then Success. */
    @Test
    fun `cancelInvoice emits Loading then Success`() = runTest {
        coEvery { defaultSource.cancelInvoice(any(), any(), any(), any()) } returns Response.success(Unit)

        val results = repository.cancelInvoice("v1_0", "inv-ref-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /** Verifies that [DefaultRepository.cancelInvoice] emits Error on a non-2xx response. */
    @Test
    fun `cancelInvoice emits Error on failure`() = runTest {
        coEvery { defaultSource.cancelInvoice(any(), any(), any(), any()) } returns
            Response.error(404, "not found".toResponseBody("text/plain".toMediaType()))

        val results = repository.cancelInvoice("v1_0", "inv-ref-001", "sub-key", "sandbox").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /** Verifies that [DefaultRepository.createPreApproval] emits Loading then Success on HTTP 202. */
    @Test
    fun `createPreApproval emits Loading then Success`() = runTest {
        coEvery { defaultSource.createPreApproval(any(), any(), any(), any(), any()) } returns Response.success(Unit)

        val results = repository.createPreApproval("v1_0", samplePreApproval(), "uuid-pa-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /** Verifies that [DefaultRepository.createPreApproval] emits Error on failure. */
    @Test
    fun `createPreApproval emits Error on failure`() = runTest {
        coEvery { defaultSource.createPreApproval(any(), any(), any(), any(), any()) } returns
            Response.error(500, "server error".toResponseBody("text/plain".toMediaType()))

        val results = repository.createPreApproval("v1_0", samplePreApproval(), "uuid-pa-001", "sub-key", "sandbox").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /** Verifies that [DefaultRepository.getPreApprovalStatus] emits Loading then Success. */
    @Test
    fun `getPreApprovalStatus emits Loading then Success`() = runTest {
        val body = mockk<ResponseBody>(relaxed = true)
        coEvery { defaultSource.getPreApprovalStatus(any(), any(), any(), any()) } returns Response.success(body)

        val results = repository.getPreApprovalStatus("v1_0", "pa-ref-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /** Verifies that [DefaultRepository.cancelPreApproval] emits Loading then Success on HTTP 200. */
    @Test
    fun `cancelPreApproval emits Loading then Success`() = runTest {
        coEvery { defaultSource.cancelPreApproval(any(), any(), any(), any()) } returns Response.success(Unit)

        val results = repository.cancelPreApproval("v1_0", "pa-ref-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /** Verifies that [DefaultRepository.cancelPreApproval] emits Error on a non-2xx response. */
    @Test
    fun `cancelPreApproval emits Error on failure`() = runTest {
        coEvery { defaultSource.cancelPreApproval(any(), any(), any(), any()) } returns
            Response.error(404, "not found".toResponseBody("text/plain".toMediaType()))

        val results = repository.cancelPreApproval("v1_0", "pa-ref-001", "sub-key", "sandbox").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /** Verifies that [DefaultRepository.cashTransfer] emits Loading then Success on HTTP 202. */
    @Test
    fun `cashTransfer emits Loading then Success`() = runTest {
        coEvery { defaultSource.cashTransfer(any(), any(), any(), any(), any()) } returns Response.success(Unit)

        val results = repository.cashTransfer("v2_0", sampleCashTransfer(), "uuid-ct-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /** Verifies that [DefaultRepository.cashTransfer] emits Error on a non-2xx response. */
    @Test
    fun `cashTransfer emits Error on failure`() = runTest {
        coEvery { defaultSource.cashTransfer(any(), any(), any(), any(), any()) } returns
            Response.error(400, "bad request".toResponseBody("text/plain".toMediaType()))

        val results = repository.cashTransfer("v2_0", sampleCashTransfer(), "uuid-ct-001", "sub-key", "sandbox").toList()

        assertTrue(results.last() is NetworkResult.Error)
    }

    /** Verifies that [DefaultRepository.getCashTransferStatus] emits Loading then Success. */
    @Test
    fun `getCashTransferStatus emits Loading then Success`() = runTest {
        val body = mockk<ResponseBody>(relaxed = true)
        coEvery { defaultSource.getCashTransferStatus(any(), any(), any(), any()) } returns Response.success(body)

        val results = repository.getCashTransferStatus("v2_0", "ct-ref-001", "sub-key", "sandbox").toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.requestToWithdrawDeliveryNotification] emits
     * [NetworkResult.Loading] then [NetworkResult.Success].
     */
    @Test
    fun `requestToWithdrawDeliveryNotification emits Loading then Success`() = runTest {
        val body = mockk<ResponseBody>(relaxed = true)
        val notification = MomoNotification(notificationMessage = "Withdrawal approved")
        coEvery {
            defaultSource.requestToWithdrawDeliveryNotification(any(), any(), any(), any(), any())
        } returns Response.success(body)

        val results = repository.requestToWithdrawDeliveryNotification(
            "v1_0",
            "ref-001",
            notification,
            "sub-key",
            "sandbox"
        ).toList()

        assertTrue(results.first() is NetworkResult.Loading)
        assertTrue(results.last() is NetworkResult.Success)
    }

    /**
     * Verifies that [DefaultRepository.requestToWithdrawDeliveryNotification] emits
     * [NetworkResult.Error] when the source returns a non-2xx response.
     */
    @Test
    fun `requestToWithdrawDeliveryNotification emits Error on failure`() = runTest {
        val notification = MomoNotification(notificationMessage = "Withdrawal approved")
        coEvery {
            defaultSource.requestToWithdrawDeliveryNotification(any(), any(), any(), any(), any())
        } returns Response.error(404, "not found".toResponseBody("text/plain".toMediaType()))

        val results = repository.requestToWithdrawDeliveryNotification(
            "v1_0",
            "ref-001",
            notification,
            "sub-key",
            "sandbox"
        ).toList()

        assertTrue(results.last() is NetworkResult.Error)
    }
}
