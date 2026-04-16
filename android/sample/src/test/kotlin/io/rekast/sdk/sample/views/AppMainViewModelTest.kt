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
package io.rekast.sdk.sample.views

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
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
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AppMainViewModel].
 *
 * Credentials are injected via a [CredentialStorage] mock, so there is no
 * dependency on SharedPreferences or any Android runtime.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppMainViewModelTest {

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

    private lateinit var viewModel: AppMainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Default: no credentials stored yet
        every { mockStorage.getApiKey() } returns ""
        every { mockStorage.getAccessToken() } returns ""
        every { mockStorage.getOauthAccessToken() } returns ""
        viewModel = AppMainViewModel(mockRepository, mockStorage, mockSettings, testDispatcherProvider, mockSampleConfig)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** Verifies checkUser always delegates to the repository. */
    @Test
    fun `checkUser calls checkApiUser on repository`() = runTest {
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Error("User not found")
        )
        coEvery { mockRepository.createApiUser(any(), any(), any(), any()) } returns flowOf(
            NetworkResult.Error("Create failed")
        )

        viewModel.checkUser()

        coVerify { mockRepository.checkApiUser(any(), any()) }
    }

    /** Verifies createApiUser is called when checkApiUser returns an error (user does not exist). */
    @Test
    fun `checkUser calls createApiUser when checkApiUser returns error`() = runTest {
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Error("404 Not Found")
        )
        coEvery { mockRepository.createApiUser(any(), any(), any(), any()) } returns flowOf(
            NetworkResult.Error("Create failed")
        )

        viewModel.checkUser()

        coVerify { mockRepository.createApiUser(any(), any(), any(), any()) }
    }

    /** Verifies createApiUser is NOT called when the user already exists. */
    @Test
    fun `checkUser does not call createApiUser when checkApiUser succeeds`() = runTest {
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.createApiKey(any(), any()) } returns flowOf(
            NetworkResult.Error("Failed")
        )

        viewModel.checkUser()

        coVerify(exactly = 0) { mockRepository.createApiUser(any(), any(), any(), any()) }
    }
}
