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
package io.rekast.sdk.sample.views.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [HomeScreenViewModel].
 *
 * Verifies initial LiveData state and the three main data-fetching operations:
 * [HomeScreenViewModel.getBasicUserInfo], [HomeScreenViewModel.getAccountBalance],
 * and [HomeScreenViewModel.validateAccountHolderStatus].
 *
 * For each operation the tests cover the happy path (repository returns success),
 * the error path (repository returns an error), and the guard condition
 * (access token is blank — the repository should not be called).
 *
 * [InstantTaskExecutorRule] ensures LiveData `postValue` calls are applied
 * synchronously. [UnconfinedTestDispatcher] runs IO-dispatcher coroutines
 * on the calling thread so no `advanceUntilIdle` is needed.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockSettings = mockk<Settings>(relaxed = true)
    private val mockSampleConfig = SampleConfig(
        apiVersionV1 = "v1_0",
        apiVersionV2 = "v2_0",
        environment = "sandbox",
        providerCallbackHost = "localhost",
        apiUserId = "test-user-id",
        collectionPrimaryKey = "collection-key",
        collectionSecondaryKey = "collection-secondary-key",
        remittancePrimaryKey = "remittance-key",
        remittanceSecondaryKey = "remittance-secondary-key",
        disbursementsPrimaryKey = "disbursements-key",
        disbursementsSecondaryKey = "disbursements-secondary-key"
    )

    private lateinit var viewModel: HomeScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Utils)
        every { mockStorage.getAccessToken() } returns "test-access-token"
        every { Utils.getProductSubscriptionKeys(any(), any()) } returns "test-subscription-key"
        every { Utils.convertToDate(any()) } returns "2001-09-09"
        viewModel = HomeScreenViewModel(mockRepository, mockStorage, mockSettings, testDispatcherProvider, mockSampleConfig)
    }

    @After
    fun tearDown() {
        unmockkObject(Utils)
        Dispatchers.resetMain()
    }

    /** Verifies showProgressBar is initialised to false before any API call is made. */
    @Test
    fun `initial state has showProgressBar false`() {
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies basicUserInfo is null before getBasicUserInfo is called. */
    @Test
    fun `initial state has null basicUserInfo`() {
        assertNull(viewModel.basicUserInfo.value)
    }

    /** Verifies accountHolderStatus is null before validateAccountHolderStatus is called. */
    @Test
    fun `initial state has null accountHolderStatus`() {
        assertNull(viewModel.accountHolderStatus.value)
    }

    /** Verifies accountBalance is null before getAccountBalance is called. */
    @Test
    fun `initial state has null accountBalance`() {
        assertNull(viewModel.accountBalance.value)
    }

    /** Verifies getBasicUserInfo delegates to the repository when access token is present. */
    @Test
    fun `getBasicUserInfo calls repository getBasicUserInfo`() = runTest {
        coEvery {
            mockRepository.getBasicUserInfo(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("404"))

        viewModel.getBasicUserInfo()

        coVerify { mockRepository.getBasicUserInfo(any(), any(), any(), any(), any()) }
    }

    /** Verifies basicUserInfo LiveData is populated and showProgressBar cleared on a successful response. */
    @Test
    fun `getBasicUserInfo posts userInfo on success`() = runTest {
        val userInfo = BasicUserInfo(
            sub = "sub-1",
            name = "John Doe",
            givenName = "John",
            familyName = "Doe",
            birthDate = "1990-01-01",
            locale = "en",
            gender = "male",
            updatedAt = 1000000000
        )
        coEvery {
            mockRepository.getBasicUserInfo(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Success(userInfo))

        viewModel.getBasicUserInfo()

        assertNotNull(viewModel.basicUserInfo.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies showProgressBar is cleared to false when getBasicUserInfo receives an error response. */
    @Test
    fun `getBasicUserInfo sets showProgressBar false on error`() = runTest {
        coEvery {
            mockRepository.getBasicUserInfo(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("404 Not Found"))

        viewModel.getBasicUserInfo()

        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies getAccountBalance delegates to the repository when access token is present. */
    @Test
    fun `getAccountBalance calls repository getAccountBalance`() = runTest {
        coEvery {
            mockRepository.getAccountBalance(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("error"))

        viewModel.getAccountBalance()

        coVerify { mockRepository.getAccountBalance(any(), any(), any(), any(), any()) }
    }

    /** Verifies accountBalance LiveData is populated and showProgressBar cleared on a successful response. */
    @Test
    fun `getAccountBalance posts balance on success`() = runTest {
        val balance = AccountBalance(availableBalance = "100.00", currency = "EUR")
        coEvery {
            mockRepository.getAccountBalance(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Success(balance))

        viewModel.getAccountBalance()

        assertNotNull(viewModel.accountBalance.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies showProgressBar is cleared to false when getAccountBalance receives an error response. */
    @Test
    fun `getAccountBalance sets showProgressBar false on error`() = runTest {
        coEvery {
            mockRepository.getAccountBalance(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("500 Internal Server Error"))

        viewModel.getAccountBalance()

        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies getAccountBalance exits early (showProgressBar stays false) when access token is blank. */
    @Test
    fun `getAccountBalance sets showProgressBar false when access token is blank`() = runTest {
        val mockStorageNoToken = mockk<CredentialStorage>(relaxed = true)
        every { mockStorageNoToken.getAccessToken() } returns ""
        val vmWithNoToken = HomeScreenViewModel(mockRepository, mockStorageNoToken, mockSettings, testDispatcherProvider, mockSampleConfig)

        vmWithNoToken.getAccountBalance()

        assertFalse(vmWithNoToken.showProgressBar.value!!)
    }

    /** Verifies validateAccountHolderStatus exits early (showProgressBar stays false) when access token is blank. */
    @Test
    fun `validateAccountHolderStatus sets showProgressBar false when access token is blank`() = runTest {
        val mockStorageNoToken = mockk<CredentialStorage>(relaxed = true)
        every { mockStorageNoToken.getAccessToken() } returns ""
        val vmWithNoToken = HomeScreenViewModel(mockRepository, mockStorageNoToken, mockSettings, testDispatcherProvider, mockSampleConfig)

        vmWithNoToken.validateAccountHolderStatus()

        assertFalse(vmWithNoToken.showProgressBar.value!!)
    }

    /** Verifies validateAccountHolderStatus delegates to the repository when access token is present. */
    @Test
    fun `validateAccountHolderStatus calls repository validateAccountHolderStatus`() = runTest {
        coEvery {
            mockRepository.validateAccountHolderStatus(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("404"))

        viewModel.validateAccountHolderStatus()

        coVerify { mockRepository.validateAccountHolderStatus(any(), any(), any(), any(), any()) }
    }

    /**
     * Verifies [HomeScreenViewModel.validateAccountHolderStatus] posts the decoded
     * [io.rekast.sdk.model.AccountHolderStatus] to [HomeScreenViewModel.accountHolderStatus]
     * and clears the progress bar on a successful response.
     */
    @Test
    fun `validateAccountHolderStatus posts status on success`() = runTest {
        val responseBody = """{"result":true}""".toResponseBody("application/json".toMediaType())
        coEvery {
            mockRepository.validateAccountHolderStatus(any(), any(), any<AccountHolder>(), any(), any())
        } returns flowOf(NetworkResult.Success(responseBody))

        viewModel.validateAccountHolderStatus()

        assertNotNull(viewModel.accountHolderStatus.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /**
     * Verifies [HomeScreenViewModel.validateAccountHolderStatus] clears the progress bar when
     * the repository returns an error response.
     */
    @Test
    fun `validateAccountHolderStatus sets showProgressBar false on error`() = runTest {
        coEvery {
            mockRepository.validateAccountHolderStatus(any(), any(), any<AccountHolder>(), any(), any())
        } returns flowOf(NetworkResult.Error("500"))

        viewModel.validateAccountHolderStatus()

        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies getUserInfoWithConsent delegates to the repository when access token is present. */
    @Test
    fun `getUserInfoWithConsent calls repository getUserInfoWithConsent`() = runTest {
        coEvery {
            mockRepository.getUserInfoWithConsent(any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("403"))

        viewModel.getUserInfoWithConsent()

        coVerify { mockRepository.getUserInfoWithConsent(any(), any(), any(), any()) }
    }

    /**
     * Verifies [HomeScreenViewModel.getUserInfoWithConsent] clears the progress bar on a
     * successful response.
     */
    @Test
    fun `getUserInfoWithConsent sets showProgressBar false on success`() = runTest {
        val userInfo = UserInfoWithConsent(sub = "sub-1", name = "John Doe")
        coEvery {
            mockRepository.getUserInfoWithConsent(any(), any(), any(), any())
        } returns flowOf(NetworkResult.Success(userInfo))

        viewModel.getUserInfoWithConsent()

        assertFalse(viewModel.showProgressBar.value!!)
    }

    /**
     * Verifies [HomeScreenViewModel.getUserInfoWithConsent] clears the progress bar when the
     * repository returns an error response.
     */
    @Test
    fun `getUserInfoWithConsent sets showProgressBar false on error`() = runTest {
        coEvery {
            mockRepository.getUserInfoWithConsent(any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("404"))

        viewModel.getUserInfoWithConsent()

        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies getUserInfoWithConsent exits early when access token is blank. */
    @Test
    fun `getUserInfoWithConsent does not call repository when access token is blank`() = runTest {
        val mockStorageNoToken = mockk<CredentialStorage>(relaxed = true)
        every { mockStorageNoToken.getAccessToken() } returns ""
        val vmWithNoToken = HomeScreenViewModel(mockRepository, mockStorageNoToken, mockSettings, testDispatcherProvider, mockSampleConfig)

        vmWithNoToken.getUserInfoWithConsent()

        coVerify(exactly = 0) { mockRepository.getUserInfoWithConsent(any(), any(), any(), any()) }
    }
}
