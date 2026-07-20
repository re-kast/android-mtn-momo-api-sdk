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
package io.rekast.sdk.sample.views.collection.approval.approved

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import io.rekast.sdk.model.ApprovedPreApprovals
import io.rekast.sdk.model.PreApprovalDetails
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.StatusTypes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    private fun approval(id: String) = PreApprovalDetails(
        preApprovalId = id,
        toFri = "FRI:a/MM",
        fromFri = "FRI:b/MM",
        fromCurrency = "EUR",
        createdTime = "2026-07-20T21:17:49.68956",
        status = StatusTypes.APPROVED,
        message = "I PAY YOU"
    )

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
        assertTrue(viewModel.approvals.value!!.isEmpty())
    }

    @Test
    fun `onAccountHolderIdChanged updates account holder id`() {
        viewModel.onAccountHolderIdChanged("256700000000")
        assertEquals("256700000000", viewModel.accountHolderId.value)
    }

    /** A successful fetch populates the approvals list and reports the count. */
    @Test
    fun `getApprovedPreApprovals populates the list on success`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success(ApprovedPreApprovals(listOf(approval("1"), approval("2")))))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        verify { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) }
        assertEquals(2, viewModel.approvals.value!!.size)
        assertEquals("2 approved pre-approval(s) found.", viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** An empty result reports the placeholder and leaves the list empty. */
    @Test
    fun `getApprovedPreApprovals with empty result reports placeholder`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success(ApprovedPreApprovals(emptyList())))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertTrue(viewModel.approvals.value!!.isEmpty())
        assertEquals("No approved pre-approvals returned.", viewModel.result.value)
    }

    /** A fetch error clears the list and reports the failure. */
    @Test
    fun `getApprovedPreApprovals error clears the list`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("nope"))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertTrue(viewModel.approvals.value!!.isEmpty())
        assertEquals("Fetch failed: nope", viewModel.result.value)
    }

    /** An exception is caught and surfaced in the console. */
    @Test
    fun `getApprovedPreApprovals exception path posts error result`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } throws RuntimeException("kaboom")

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertEquals("Error: kaboom", viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** The fetch is skipped when the access token is blank. */
    @Test
    fun `getApprovedPreApprovals does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        verify(exactly = 0) { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) }
    }

    /** Cancelling a pre-approval drops just that entry from the list. */
    @Test
    fun `cancelPreApproval removes the cancelled entry on success`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success(ApprovedPreApprovals(listOf(approval("1"), approval("2")))))
        every { mockRepository.cancelPreApproval(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()
        viewModel.cancelPreApproval("1")

        assertEquals(listOf("2"), viewModel.approvals.value!!.map { it.preApprovalId })
        assertEquals("Pre-approval 1 cancelled.", viewModel.result.value)
    }

    /** A cancel error leaves the list unchanged and reports the failure. */
    @Test
    fun `cancelPreApproval error keeps the list and posts failure`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success(ApprovedPreApprovals(listOf(approval("1")))))
        every { mockRepository.cancelPreApproval(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("cant"))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()
        viewModel.cancelPreApproval("1")

        assertEquals(1, viewModel.approvals.value!!.size)
        assertEquals("Cancel failed: cant", viewModel.result.value)
    }

    /** A leading Loading emission is ignored and the terminal Success is used. */
    @Test
    fun `getApprovedPreApprovals ignores loading emission before terminal success`() = runTest {
        every { mockRepository.getApprovedPreApprovals(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Loading(), NetworkResult.Success(ApprovedPreApprovals(listOf(approval("1")))))

        viewModel.onAccountHolderIdChanged("256700000000")
        viewModel.getApprovedPreApprovals()

        assertEquals(1, viewModel.approvals.value!!.size)
    }
}
