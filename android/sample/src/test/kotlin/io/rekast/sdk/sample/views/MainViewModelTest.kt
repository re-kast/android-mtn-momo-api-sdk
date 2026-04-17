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
package io.rekast.sdk.sample.views

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.ApiKey
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.model.authentication.Oauth2AccessToken
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
 * Unit tests for [MainViewModel].
 *
 * Credentials are injected via a [CredentialStorage] mock, so there is no
 * dependency on SharedPreferences or any Android runtime.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

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

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Default: no credentials stored yet
        every { mockStorage.getApiKey() } returns ""
        every { mockStorage.getAccessToken() } returns ""
        every { mockStorage.getOauthAccessToken() } returns ""
        viewModel = MainViewModel(mockRepository, mockStorage, mockSettings, testDispatcherProvider, mockSampleConfig)
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

    /**
     * Verifies that [MainViewModel.checkUser] triggers [DefaultRepository.createApiKey] when
     * [checkApiUser] returns success but no API key is yet stored in [CredentialStorage].
     */
    @Test
    fun `checkUser calls createApiKey when checkApiUser succeeds and no key stored`() = runTest {
        every { mockStorage.getApiKey() } returns ""
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.createApiKey(any(), any()) } returns flowOf(
            NetworkResult.Error("Failed")
        )

        viewModel.checkUser()

        coVerify { mockRepository.createApiKey(any(), any()) }
    }

    /**
     * Verifies that [MainViewModel.checkUser] skips [DefaultRepository.createApiKey] and
     * proceeds directly to fetching the access token when an API key is already stored.
     */
    @Test
    fun `checkUser skips createApiKey when API key already stored`() = runTest {
        every { mockStorage.getApiKey() } returns "existing-api-key"
        every { mockStorage.getAccessToken() } returns ""
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.getAccessToken(any(), any()) } returns flowOf(
            NetworkResult.Error("Failed")
        )

        viewModel.checkUser()

        coVerify(exactly = 0) { mockRepository.createApiKey(any(), any()) }
    }

    /**
     * Verifies that when [DefaultRepository.createApiKey] returns [NetworkResult.Success],
     * the new API key is written to [CredentialStorage] before proceeding.
     */
    @Test
    fun `checkUser saves API key after successful createApiKey`() = runTest {
        every { mockStorage.getApiKey() } returnsMany listOf("", "new-api-key", "new-api-key")
        every { mockStorage.getAccessToken() } returns "existing-token"
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.createApiKey(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiKey(apiKey = "new-api-key"))
        )

        viewModel.checkUser()

        coVerify { mockStorage.saveApiKey("new-api-key") }
    }

    /**
     * Verifies that [DefaultRepository.getAccessToken] is called when the API key is present
     * in [CredentialStorage] but no access token has been stored yet.
     */
    @Test
    fun `checkUser calls getAccessToken when API key present but no access token`() = runTest {
        every { mockStorage.getApiKey() } returns "stored-api-key"
        every { mockStorage.getAccessToken() } returns ""
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.getAccessToken(any(), any()) } returns flowOf(
            NetworkResult.Error("Failed")
        )

        viewModel.checkUser()

        coVerify { mockRepository.getAccessToken(any(), any()) }
    }

    /**
     * Verifies that [DefaultRepository.getAccessToken] is NOT called when a valid access token
     * is already stored — the ViewModel skips straight to [DefaultRepository.getOauthAccessToken].
     */
    @Test
    fun `checkUser skips getAccessToken when access token already stored`() = runTest {
        every { mockStorage.getApiKey() } returns "stored-api-key"
        every { mockStorage.getAccessToken() } returns "valid-token"
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns "stored-auth-req-id"
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.getOauthAccessToken(any(), any(), any(), any()) } returns flowOf(
            NetworkResult.Error("Failed")
        )

        viewModel.checkUser()

        coVerify(exactly = 0) { mockRepository.getAccessToken(any(), any()) }
        coVerify { mockRepository.getOauthAccessToken(any(), any(), any(), any()) }
    }

    /**
     * Verifies that [DefaultRepository.getOauthAccessToken] is called when an access token is
     * available but no OAuth2 token has been stored yet.
     */
    @Test
    fun `checkUser calls getOauthAccessToken when access token present but no oauth token`() = runTest {
        every { mockStorage.getApiKey() } returns "stored-api-key"
        every { mockStorage.getAccessToken() } returns "valid-token"
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns "stored-auth-req-id"
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.getOauthAccessToken(any(), any(), any(), any()) } returns flowOf(
            NetworkResult.Error("Failed")
        )

        viewModel.checkUser()

        coVerify { mockRepository.getOauthAccessToken(any(), any(), any(), any()) }
    }

    /**
     * Verifies that [DefaultRepository.getOauthAccessToken] is NOT called when an OAuth2 token
     * is already stored — the bootstrap sequence is complete.
     */
    @Test
    fun `checkUser skips getOauthAccessToken when oauth token already stored`() = runTest {
        every { mockStorage.getApiKey() } returns "stored-api-key"
        every { mockStorage.getAccessToken() } returns "valid-token"
        every { mockStorage.getOauthAccessToken() } returns "valid-oauth-token"
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )

        viewModel.checkUser()

        coVerify(exactly = 0) { mockRepository.getOauthAccessToken(any(), any(), any(), any()) }
    }

    /**
     * Verifies that [CredentialStorage.saveOauthAccessToken] is called after a successful
     * [DefaultRepository.getOauthAccessToken] response.
     */
    @Test
    fun `checkUser saves oauth token after successful getOauthAccessToken`() = runTest {
        val oauthToken = Oauth2AccessToken(
            accessToken = "new-oauth-tok",
            tokenType = "Bearer",
            expiresIn = 3600,
            scope = "profile",
            refreshToken = "refresh",
            refreshTokenExpiredIn = 7200
        )
        every { mockStorage.getApiKey() } returns "stored-api-key"
        every { mockStorage.getAccessToken() } returns "valid-token"
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns "stored-auth-req-id"
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.getOauthAccessToken(any(), any(), any(), any()) } returns flowOf(
            NetworkResult.Success(oauthToken)
        )

        viewModel.checkUser()

        coVerify { mockStorage.saveOauthAccessToken(oauthToken) }
    }

    /**
     * Verifies that [CredentialStorage.saveAccessToken] is called after a successful
     * [DefaultRepository.getAccessToken] response, and that [DefaultRepository.getOauthAccessToken]
     * is then invoked to continue the bootstrap sequence.
     */
    @Test
    fun `checkUser saves access token and calls getOauthAccessToken after getAccessToken success`() = runTest {
        val accessToken = AccessToken(accessToken = "new-token", tokenType = "Bearer", expiresIn = 3600)
        every { mockStorage.getApiKey() } returns "stored-api-key"
        every { mockStorage.getAccessToken() } returnsMany listOf("", "new-token", "new-token")
        every { mockStorage.getOauthAccessToken() } returns ""
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns "stored-auth-req-id"
        coEvery { mockRepository.checkApiUser(any(), any()) } returns flowOf(
            NetworkResult.Success(ApiUser(targetEnvironment = "sandbox"))
        )
        coEvery { mockRepository.getAccessToken(any(), any()) } returns flowOf(
            NetworkResult.Success(accessToken)
        )
        coEvery { mockRepository.getOauthAccessToken(any(), any(), any(), any()) } returns flowOf(
            NetworkResult.Error("Failed")
        )

        viewModel.checkUser()

        coVerify { mockStorage.saveAccessToken(accessToken) }
        coVerify { mockRepository.getOauthAccessToken(any(), any(), any(), any()) }
    }
}
