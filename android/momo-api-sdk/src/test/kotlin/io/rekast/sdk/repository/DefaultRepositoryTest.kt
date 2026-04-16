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
import io.mockk.mockk
import io.rekast.sdk.model.AccountBalance
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
}
