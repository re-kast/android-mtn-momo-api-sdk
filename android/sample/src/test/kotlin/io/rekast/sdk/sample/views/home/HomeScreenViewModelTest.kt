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
package io.rekast.sdk.sample.views.home

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.DispatcherProvider
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
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockSettings = mockk<Settings>(relaxed = true)

    private lateinit var viewModel: HomeScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Utils)
        every { Utils.getAccessToken(any()) } returns "test-access-token"
        every { Utils.getProductSubscriptionKeys(any()) } returns "test-subscription-key"
        every { Utils.convertToDate(any()) } returns "2001-09-09"
        viewModel = HomeScreenViewModel(mockRepository, mockContext, mockSettings, testDispatcherProvider)
    }

    @After
    fun tearDown() {
        unmockkObject(Utils)
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has showProgressBar false`() {
        assertFalse(viewModel.showProgressBar.value!!)
    }

    @Test
    fun `initial state has null basicUserInfo`() {
        assertNull(viewModel.basicUserInfo.value)
    }

    @Test
    fun `initial state has null accountHolderStatus`() {
        assertNull(viewModel.accountHolderStatus.value)
    }

    @Test
    fun `initial state has null accountBalance`() {
        assertNull(viewModel.accountBalance.value)
    }

    @Test
    fun `getBasicUserInfo calls repository getBasicUserInfo`() = runTest {
        coEvery {
            mockRepository.getBasicUserInfo(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("404"))

        viewModel.getBasicUserInfo()

        coVerify { mockRepository.getBasicUserInfo(any(), any(), any(), any(), any()) }
    }

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
            updatedAt = "1000000000"
        )
        coEvery {
            mockRepository.getBasicUserInfo(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Success(userInfo))

        viewModel.getBasicUserInfo()

        assertNotNull(viewModel.basicUserInfo.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    @Test
    fun `getBasicUserInfo sets showProgressBar false on error`() = runTest {
        coEvery {
            mockRepository.getBasicUserInfo(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("404 Not Found"))

        viewModel.getBasicUserInfo()

        assertFalse(viewModel.showProgressBar.value!!)
    }

    @Test
    fun `getAccountBalance calls repository getAccountBalance`() = runTest {
        coEvery {
            mockRepository.getAccountBalance(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("error"))

        viewModel.getAccountBalance()

        coVerify { mockRepository.getAccountBalance(any(), any(), any(), any(), any()) }
    }

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

    @Test
    fun `getAccountBalance sets showProgressBar false on error`() = runTest {
        coEvery {
            mockRepository.getAccountBalance(any(), any(), any(), any(), any())
        } returns flowOf(NetworkResult.Error("500 Internal Server Error"))

        viewModel.getAccountBalance()

        assertFalse(viewModel.showProgressBar.value!!)
    }

    @Test
    fun `getAccountBalance sets showProgressBar false when access token is blank`() = runTest {
        every { Utils.getAccessToken(any()) } returns ""
        val vmWithNoToken = HomeScreenViewModel(mockRepository, mockContext, mockSettings, testDispatcherProvider)

        vmWithNoToken.getAccountBalance()

        assertFalse(vmWithNoToken.showProgressBar.value!!)
    }

    @Test
    fun `validateAccountHolderStatus sets showProgressBar false when access token is blank`() = runTest {
        every { Utils.getAccessToken(any()) } returns ""
        val vmWithNoToken = HomeScreenViewModel(mockRepository, mockContext, mockSettings, testDispatcherProvider)

        vmWithNoToken.validateAccountHolderStatus()

        assertFalse(vmWithNoToken.showProgressBar.value!!)
    }
}
