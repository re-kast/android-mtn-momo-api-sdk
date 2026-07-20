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
package io.rekast.sdk.sample.views.collection.preapproval.approved

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.Utils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ApprovedPreApprovalsScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    private lateinit var viewModel: ApprovedPreApprovalsScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Utils)
        every { mockStorage.getAccessToken() } returns "test-access-token"
        every { Utils.getProductSubscriptionKeys(any(), any()) } returns "test-subscription-key"
        every { mockConfig.apiVersionV1 } returns "v1_0"
        every { mockConfig.environment } returns "sandbox"
        viewModel = ApprovedPreApprovalsScreenViewModel(mockRepository, mockStorage, testDispatcherProvider, mockConfig)
    }

    @After
    fun tearDown() {
        unmockkObject(Utils)
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state`() {
        assertFalse(viewModel.showProgressBar.value!!)
        assertNull(viewModel.result.value)
        assertEquals("", viewModel.accountHolderId.value)
    }

    @Test
    fun `onAccountHolderIdChanged updates account holder id`() {
        viewModel.onAccountHolderIdChanged("256700000000")
        assertEquals("256700000000", viewModel.accountHolderId.value)
    }

    /** A non-blank response body is printed verbatim. */
    @Test
    fun `getApprovedPreApprovals success prints payload`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success("""[{"payerId":"256700000000"}]""".toResponseBody("application/json".toMediaType())))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        verify { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) }
        assertEquals("""[{"payerId":"256700000000"}]""", viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** A blank body reports the placeholder rather than an empty console. */
    @Test
    fun `getApprovedPreApprovals with blank body reports placeholder`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success("".toResponseBody("application/json".toMediaType())))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertEquals("No approved pre-approvals returned.", viewModel.result.value)
    }

    /** A success with a null response body reports the placeholder rather than crashing. */
    @Test
    fun `getApprovedPreApprovals with null body reports placeholder`() = runTest {
        @Suppress("UNCHECKED_CAST")
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            (flowOf(NetworkResult.Success(null)) as Flow<NetworkResult<ResponseBody>>)

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertEquals("No approved pre-approvals returned.", viewModel.result.value)
    }

    /** An error posts a failure message. */
    @Test
    fun `getApprovedPreApprovals error path posts failure result`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("nope"))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertEquals("Fetch failed: nope", viewModel.result.value)
    }

    /** An exception during the operation is caught and surfaced in the console. */
    @Test
    fun `getApprovedPreApprovals exception path posts error result`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } throws RuntimeException("kaboom")

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertEquals("Error: kaboom", viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** An exception carrying no message is surfaced with an empty detail. */
    @Test
    fun `getApprovedPreApprovals exception with null message posts error result`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } throws RuntimeException()

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertEquals("Error: null", viewModel.result.value)
    }

    /** The operation is skipped when the access token is blank. */
    @Test
    fun `getApprovedPreApprovals does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        verify(exactly = 0) { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) }
    }

    /** A leading Loading emission is ignored and the terminal Success completes the flow. */
    @Test
    fun `getApprovedPreApprovals ignores loading emission before terminal success`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Loading(), NetworkResult.Success("[]".toResponseBody("application/json".toMediaType())))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertNotNull(viewModel.result.value)
    }
}
