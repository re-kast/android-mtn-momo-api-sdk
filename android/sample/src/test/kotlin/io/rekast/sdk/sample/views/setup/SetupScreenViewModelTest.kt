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
package io.rekast.sdk.sample.views.setup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [SetupScreenViewModel].
 *
 * Verifies that the credential-provisioning status is read from [CredentialStorage] on construction
 * and on [SetupScreenViewModel.refresh], that the static config is exposed, and that
 * [SetupScreenViewModel.notifyRerun] emits an informational snackbar.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SetupScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    private lateinit var viewModel: SetupScreenViewModel

    private fun stubCredentials(apiKey: String = "", accessToken: String = "", oauth: String = "", authReqId: String = "", loginHint: String = "") {
        every { mockStorage.getApiKey() } returns apiKey
        every { mockStorage.getAccessToken() } returns accessToken
        every { mockStorage.getOauthAccessToken() } returns oauth
        every { mockStorage.getBackChannelAuthorizationRequestId() } returns authReqId
        every { mockStorage.getLoginHint() } returns loginHint
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** Every credential flag is false when storage holds nothing. */
    @Test
    fun `status is all-absent when storage is empty`() {
        stubCredentials()
        viewModel = SetupScreenViewModel(mockStorage, mockConfig)

        val status = viewModel.status.value!!
        assertFalse(status.apiKeyPresent)
        assertFalse(status.accessTokenPresent)
        assertFalse(status.oauthTokenPresent)
        assertFalse(status.authReqIdPresent)
        assertFalse(status.loginHintPresent)
    }

    /** Each stored credential flips its corresponding flag to true. */
    @Test
    fun `status reflects present credentials`() {
        stubCredentials(apiKey = "k", accessToken = "t", oauth = "o", authReqId = "r", loginHint = "h")
        viewModel = SetupScreenViewModel(mockStorage, mockConfig)

        val status = viewModel.status.value!!
        assertTrue(status.apiKeyPresent)
        assertTrue(status.accessTokenPresent)
        assertTrue(status.oauthTokenPresent)
        assertTrue(status.authReqIdPresent)
        assertTrue(status.loginHintPresent)
    }

    /** A blank access token is treated as absent (matches the storage's expiry semantics). */
    @Test
    fun `status treats blank access token as absent`() {
        stubCredentials(apiKey = "k", accessToken = "")
        viewModel = SetupScreenViewModel(mockStorage, mockConfig)

        assertTrue(viewModel.status.value!!.apiKeyPresent)
        assertFalse(viewModel.status.value!!.accessTokenPresent)
    }

    /** refresh re-reads storage so a credential provisioned after construction becomes visible. */
    @Test
    fun `refresh re-reads the credential status`() {
        stubCredentials()
        viewModel = SetupScreenViewModel(mockStorage, mockConfig)
        assertFalse(viewModel.status.value!!.apiKeyPresent)

        every { mockStorage.getApiKey() } returns "new-key"
        viewModel.refresh()

        assertTrue(viewModel.status.value!!.apiKeyPresent)
    }

    /** The static config is exposed unchanged for the screen to render. */
    @Test
    fun `config is exposed`() {
        stubCredentials()
        viewModel = SetupScreenViewModel(mockStorage, mockConfig)
        assertEquals(mockConfig, viewModel.config)
    }

    /** notifyRerun emits exactly one INFO snackbar. */
    @Test
    fun `notifyRerun emits an info snackbar`() = runTest {
        stubCredentials()
        viewModel = SetupScreenViewModel(mockStorage, mockConfig)
        val emissions = mutableListOf<SnackBarComponentConfiguration>()
        val job = launch(testDispatcher) { viewModel.snackBarStateFlow.collect { emissions.add(it) } }

        viewModel.notifyRerun()
        job.cancel()

        assertEquals(1, emissions.size)
        assertEquals(SnackBarType.INFO, emissions.first().type)
    }
}
