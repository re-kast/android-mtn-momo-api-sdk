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
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.network.service.products.CollectionService
import io.rekast.sdk.network.service.products.DisbursementsService
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.utils.MomoApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Unit tests for [DefaultRepository].
 *
 * Verifies that:
 * - Every flow-based method emits [NetworkResult.Loading] as its first item.
 * - Flow-based methods emit [NetworkResult.Success] when the underlying call succeeds.
 * - Flow-based methods emit [NetworkResult.Error] when the underlying call returns an HTTP error.
 * - [DefaultRepository.getAccountBalance] routes to the correct [DefaultSource] method based on
 *   whether the currency argument is non-null/non-blank or null/blank.
 * - Direct suspend delegation methods forward their calls to the correct service without wrapping
 *   them in a flow.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DefaultRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val defaultSource: DefaultSource = mockk(relaxed = true)
    private val disbursementsService: DisbursementsService = mockk(relaxed = true)
    private val collection: CollectionService = mockk(relaxed = true)
    private val config = MomoApiConfig(
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

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private val providerCallBackHost = ProviderCallBackHost(providerCallbackHost = "https://callback.example.com")
    private val apiUser = ApiUser(providerCallbackHost = "https://callback.example.com", targetEnvironment = "sandbox")

    private fun sampleTransaction() = MomoTransaction(
        amount = "100",
        currency = "EUR",
        externalId = "ext-001",
        payerMessage = "Payment",
        payeeNote = "Note"
    )

    // ---------------------------------------------------------------------------
    // Flow emission order — Loading first
    // ---------------------------------------------------------------------------

    /** Verifies that createApiUser emits [NetworkResult.Loading] as the very first item. */
    @Test
    fun `createApiUser emits Loading as first emission`() = runTest {
        coEvery {
            defaultSource.createApiUser(any(), any(), any(), any())
        } returns Response.success(apiUser)

        val results = repository.createApiUser(providerCallBackHost, "v1_0", "uuid-001", "sub-key").toList()

        assertTrue(results.first() is NetworkResult.Loading)
    }

    // ---------------------------------------------------------------------------
    // Flow emission — Success terminal item
    // ---------------------------------------------------------------------------

    /** Verifies that createApiUser emits [NetworkResult.Success] as the last item on a 2xx response. */
    @Test
    fun `createApiUser emits Success when source returns successful response`() = runTest {
        coEvery {
            defaultSource.createApiUser(any(), any(), any(), any())
        } returns Response.success(apiUser)

        val results = repository.createApiUser(providerCallBackHost, "v1_0", "uuid-001", "sub-key").toList()

        assertTrue(results.last() is NetworkResult.Success)
        assertEquals(apiUser, results.last().response)
    }

    // ---------------------------------------------------------------------------
    // Flow emission — Error terminal item on HTTP error
    // ---------------------------------------------------------------------------

    /** Verifies that createApiUser emits [NetworkResult.Error] when the source returns a 404. */
    @Test
    fun `createApiUser emits Error when source returns HTTP error`() = runTest {
        coEvery {
            defaultSource.createApiUser(any(), any(), any(), any())
        } returns Response.error(404, "error".toResponseBody("text/plain".toMediaType()))

        val results = repository.createApiUser(providerCallBackHost, "v1_0", "uuid-001", "sub-key").toList()

        assertTrue(results.last() is NetworkResult.Error)
        assertTrue(results.last().message!!.contains("404"))
    }

    // ---------------------------------------------------------------------------
    // getAccountBalance — routing by currency
    // ---------------------------------------------------------------------------

    /** Verifies that a non-null, non-blank currency routes to getAccountBalanceInSpecificCurrency. */
    @Test
    fun `getAccountBalance with non-null currency calls getAccountBalanceInSpecificCurrency`() = runTest {
        val balance = AccountBalance(availableBalance = "500", currency = "EUR")
        coEvery {
            defaultSource.getAccountBalanceInSpecificCurrency(any(), any(), any(), any(), any())
        } returns Response.success(balance)

        repository.getAccountBalance("collection", "v1_0", "EUR", "sub-key", "sandbox").toList()

        coVerify(exactly = 1) {
            defaultSource.getAccountBalanceInSpecificCurrency(any(), any(), any(), any(), any())
        }
        coVerify(exactly = 0) {
            defaultSource.getAccountBalance(any(), any(), any(), any())
        }
    }

    /** Verifies that a null currency routes to getAccountBalance (no currency variant). */
    @Test
    fun `getAccountBalance with null currency calls getAccountBalance without currency`() = runTest {
        val balance = AccountBalance(availableBalance = "500", currency = "EUR")
        coEvery {
            defaultSource.getAccountBalance(any(), any(), any(), any())
        } returns Response.success(balance)

        repository.getAccountBalance("collection", "v1_0", null, "sub-key", "sandbox").toList()

        coVerify(exactly = 1) {
            defaultSource.getAccountBalance(any(), any(), any(), any())
        }
        coVerify(exactly = 0) {
            defaultSource.getAccountBalanceInSpecificCurrency(any(), any(), any(), any(), any())
        }
    }

    /** Verifies that a blank (whitespace-only) currency routes to getAccountBalance (no currency variant). */
    @Test
    fun `getAccountBalance with blank currency calls getAccountBalance without currency`() = runTest {
        val balance = AccountBalance(availableBalance = "500", currency = "EUR")
        coEvery {
            defaultSource.getAccountBalance(any(), any(), any(), any())
        } returns Response.success(balance)

        repository.getAccountBalance("collection", "v1_0", "   ", "sub-key", "sandbox").toList()

        coVerify(exactly = 1) {
            defaultSource.getAccountBalance(any(), any(), any(), any())
        }
        coVerify(exactly = 0) {
            defaultSource.getAccountBalanceInSpecificCurrency(any(), any(), any(), any(), any())
        }
    }

    // ---------------------------------------------------------------------------
    // Direct suspend delegation — CollectionService
    // ---------------------------------------------------------------------------

    /** Verifies that requestToPay delegates directly to [CollectionService.requestToPay]. */
    @Test
    fun `requestToPay delegates to collection service`() = runTest {
        coEvery {
            collection.requestToPay(any(), any(), any(), any(), any())
        } returns Response.success(Unit)

        repository.requestToPay(
            accessToken = "token",
            momoTransaction = sampleTransaction(),
            apiVersion = "v1_0",
            productSubscriptionKey = "sub-key",
            uuid = "uuid-rtp-001"
        )

        coVerify(exactly = 1) {
            collection.requestToPay(any(), any(), any(), any(), any())
        }
    }

    // ---------------------------------------------------------------------------
    // Direct suspend delegation — DisbursementsService
    // ---------------------------------------------------------------------------

    /** Verifies that deposit delegates directly to [DisbursementsService.deposit]. */
    @Test
    fun `deposit delegates to disbursementsService`() = runTest {
        coEvery {
            disbursementsService.deposit(any(), any(), any(), any(), any())
        } returns Response.success(Unit)

        repository.deposit(
            accessToken = "token",
            momoTransaction = sampleTransaction(),
            apiVersion = "v1_0",
            productSubscriptionKey = "sub-key",
            uuid = "uuid-dep-001"
        )

        coVerify(exactly = 1) {
            disbursementsService.deposit(any(), any(), any(), any(), any())
        }
    }

    /** Verifies that refund delegates directly to [DisbursementsService.refund]. */
    @Test
    fun `refund delegates to disbursementsService`() = runTest {
        coEvery {
            disbursementsService.refund(any(), any(), any(), any(), any())
        } returns Response.success(Unit)

        repository.refund(
            accessToken = "token",
            momoTransaction = sampleTransaction(),
            apiVersion = "v1_0",
            productSubscriptionKey = "sub-key",
            uuid = "uuid-ref-001"
        )

        coVerify(exactly = 1) {
            disbursementsService.refund(any(), any(), any(), any(), any())
        }
    }
}
