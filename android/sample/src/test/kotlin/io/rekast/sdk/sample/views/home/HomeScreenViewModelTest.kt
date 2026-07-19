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
import androidx.lifecycle.Observer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [HomeScreenViewModel].
 *
 * Verifies initial LiveData state and the single ordered data-loading pipeline [HomeScreenViewModel.loadHomeData]:
 * the request ordering, the data dependency (the verified profile's phone number becomes the account
 * holder for the account-scoped calls), the progress-bar lifecycle (shown for the whole batch, hidden
 * only once every request completes), and the blank-token guard.
 *
 * [InstantTaskExecutorRule] ensures LiveData `postValue` calls are applied synchronously.
 * [UnconfinedTestDispatcher] runs IO-dispatcher coroutines on the calling thread so the pipeline
 * completes before [HomeScreenViewModel.loadHomeData] returns.
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

    private fun sampleBasicUserInfo() = BasicUserInfo(
        sub = "sub-1",
        name = "John Doe",
        givenName = "John",
        familyName = "Doe",
        birthDate = "1990-01-01",
        locale = "en",
        gender = "male",
        updatedAt = 1000000000
    )

    private fun activeStatusBody() = """{"result":true}""".toResponseBody("application/json".toMediaType())

    /** Stubs all four repository calls to return success, with the given consent profile. */
    private fun stubAllSuccess(consent: UserInfoWithConsent) {
        coEvery { mockRepository.getUserInfoWithConsent(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(consent))
        coEvery { mockRepository.getBasicUserInfo(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(sampleBasicUserInfo()))
        coEvery {
            mockRepository.validateAccountHolderStatus(any(), any(), any<AccountHolder>(), any(), any())
        } returns flowOf(NetworkResult.Success(activeStatusBody()))
        coEvery { mockRepository.getAccountBalance(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(AccountBalance("100.00", "EUR")))
    }

    /** Verifies showProgressBar is initialised to false before any API call is made. */
    @Test
    fun `initial state has showProgressBar false`() {
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies all data LiveData are null before loadHomeData is called. */
    @Test
    fun `initial state has null data`() {
        assertNull(viewModel.basicUserInfo.value)
        assertNull(viewModel.userInfoWithConsent.value)
        assertNull(viewModel.accountHolderStatus.value)
        assertNull(viewModel.accountBalance.value)
    }

    /** Verifies loadHomeData skips every repository call and never shows the spinner when the token is blank. */
    @Test
    fun `loadHomeData does not call repositories when access token is blank`() = runTest {
        val mockStorageNoToken = mockk<CredentialStorage>(relaxed = true)
        every { mockStorageNoToken.getAccessToken() } returns ""
        val vmWithNoToken = HomeScreenViewModel(mockRepository, mockStorageNoToken, mockSettings, testDispatcherProvider, mockSampleConfig)

        vmWithNoToken.loadHomeData()

        assertFalse(vmWithNoToken.showProgressBar.value!!)
        coVerify(exactly = 0) { mockRepository.getUserInfoWithConsent(any(), any(), any(), any()) }
        coVerify(exactly = 0) { mockRepository.getBasicUserInfo(any(), any(), any(), any(), any()) }
        coVerify(exactly = 0) { mockRepository.validateAccountHolderStatus(any(), any(), any<AccountHolder>(), any(), any()) }
        coVerify(exactly = 0) { mockRepository.getAccountBalance(any(), any(), any(), any(), any()) }
    }

    /**
     * Verifies the pipeline runs in order — verified profile, then basic user info, then account
     * status, then account balance — and populates every LiveData on success.
     */
    @Test
    fun `loadHomeData fetches in order and populates all data`() = runTest {
        stubAllSuccess(UserInfoWithConsent(sub = "sub-1", name = "Sand Box", phonenumber = "46123456789"))

        viewModel.loadHomeData()

        coVerifyOrder {
            mockRepository.getUserInfoWithConsent(any(), any(), any(), any())
            mockRepository.getBasicUserInfo(any(), any(), any(), any(), any())
            mockRepository.validateAccountHolderStatus(any(), any(), any<AccountHolder>(), any(), any())
            mockRepository.getAccountBalance(any(), any(), any(), any(), any())
        }
        assertNotNull(viewModel.userInfoWithConsent.value)
        assertNotNull(viewModel.basicUserInfo.value)
        assertNotNull(viewModel.accountHolderStatus.value)
        assertNotNull(viewModel.accountBalance.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /**
     * Verifies the data dependency: the phone number from the verified profile is threaded into the
     * account-scoped calls (basic user info and account holder status) as the account holder.
     */
    @Test
    fun `loadHomeData threads consent phone number into account calls`() = runTest {
        val basicHolder = slot<String>()
        val statusHolder = slot<AccountHolder>()
        coEvery { mockRepository.getUserInfoWithConsent(any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success(UserInfoWithConsent(sub = "sub-1", name = "Sand Box", phonenumber = "46123456789")))
        coEvery { mockRepository.getBasicUserInfo(any(), any(), capture(basicHolder), any(), any()) } returns
            flowOf(NetworkResult.Success(sampleBasicUserInfo()))
        coEvery {
            mockRepository.validateAccountHolderStatus(any(), any(), capture(statusHolder), any(), any())
        } returns flowOf(NetworkResult.Success(activeStatusBody()))
        coEvery { mockRepository.getAccountBalance(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success(AccountBalance("100.00", "EUR")))

        viewModel.loadHomeData()

        assertEquals("46123456789", basicHolder.captured)
        assertEquals("46123456789", statusHolder.captured.partyId)
    }

    /** Verifies the default account holder is used when the verified profile carries no phone number. */
    @Test
    fun `loadHomeData falls back to default account holder when consent has no phone number`() = runTest {
        val basicHolder = slot<String>()
        stubAllSuccess(UserInfoWithConsent(sub = "sub-1", name = "Sand Box"))
        coEvery { mockRepository.getBasicUserInfo(any(), any(), capture(basicHolder), any(), any()) } returns
            flowOf(NetworkResult.Success(sampleBasicUserInfo()))

        viewModel.loadHomeData()

        assertEquals("99733123459", basicHolder.captured)
    }

    /** Verifies the progress bar is shown for the batch and hidden only after every request completes. */
    @Test
    fun `loadHomeData shows progress bar then hides it after all requests complete`() = runTest {
        stubAllSuccess(UserInfoWithConsent(sub = "sub-1", name = "Sand Box", phonenumber = "46123456789"))
        val values = mutableListOf<Boolean>()
        val observer = Observer<Boolean> { values.add(it) }
        viewModel.showProgressBar.observeForever(observer)

        viewModel.loadHomeData()
        viewModel.showProgressBar.removeObserver(observer)

        assertTrue("Progress bar should be shown while loading", values.contains(true))
        assertFalse("Progress bar should be hidden after loading", values.last())
    }

    /** Verifies the progress bar is still hidden and the pipeline completes even when every request fails. */
    @Test
    fun `loadHomeData hides progress bar even when all requests fail`() = runTest {
        coEvery { mockRepository.getUserInfoWithConsent(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("403"))
        coEvery { mockRepository.getBasicUserInfo(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("404"))
        coEvery {
            mockRepository.validateAccountHolderStatus(any(), any(), any<AccountHolder>(), any(), any())
        } returns flowOf(NetworkResult.Error("500"))
        coEvery { mockRepository.getAccountBalance(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("500"))

        viewModel.loadHomeData()

        assertFalse(viewModel.showProgressBar.value!!)
        assertNull(viewModel.basicUserInfo.value)
        assertNull(viewModel.accountBalance.value)
    }
}
