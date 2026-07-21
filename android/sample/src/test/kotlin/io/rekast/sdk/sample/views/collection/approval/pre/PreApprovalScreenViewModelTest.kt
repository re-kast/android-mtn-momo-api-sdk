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
package io.rekast.sdk.sample.views.collection.approval.pre

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import io.rekast.sdk.model.PreApproval
import io.rekast.sdk.model.PreApprovalStatus
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PreApprovalScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    private lateinit var viewModel: PreApprovalScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Utils)
        every { mockStorage.getAccessToken() } returns "test-access-token"
        every { Utils.getProductSubscriptionKeys(any(), any()) } returns "test-subscription-key"
        every { mockConfig.apiVersionV1 } returns "v1_0"
        every { mockConfig.environment } returns "sandbox"
        viewModel = PreApprovalScreenViewModel(mockRepository, mockStorage, testDispatcherProvider, mockConfig)
    }

    @After
    fun tearDown() {
        unmockkObject(Utils)
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state`() {
        assertFalse(viewModel.showProgressBar.value!!)
        assertNull(viewModel.referenceId.value)
        assertNull(viewModel.result.value)
        assertEquals("EUR", viewModel.payerCurrency.value)
    }

    @Test
    fun `onPayerMsisdnChanged updates payer msisdn`() {
        viewModel.onPayerMsisdnChanged("256700000000")
        assertEquals("256700000000", viewModel.payerMsisdn.value)
    }

    /** Verifies createPreApproval calls the repository, stores a reference ID, and clears the progress bar. */
    @Test
    fun `createPreApproval stores reference id on success`() = runTest {
        every { mockRepository.createPreApproval(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.onValidityTimeChanged("60")
        viewModel.createPreApproval()

        verify { mockRepository.createPreApproval(any(), any(), any(), any()) }
        assertNotNull(viewModel.referenceId.value)
        assertNotNull(viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies createPreApproval is skipped when the access token is blank. */
    @Test
    fun `createPreApproval does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""

        viewModel.createPreApproval()

        verify(exactly = 0) { mockRepository.createPreApproval(any(), any(), any(), any()) }
    }

    /** Verifies checkStatus is a no-op (no repository call) until a pre-approval has been created. */
    @Test
    fun `checkStatus does nothing without a reference id`() = runTest {
        viewModel.checkStatus()

        verify(exactly = 0) { mockRepository.getPreApprovalStatus(any(), any(), any()) }
    }

    @Test
    fun `onPayerCurrencyChanged updates currency`() {
        viewModel.onPayerCurrencyChanged("USD")
        assertEquals("USD", viewModel.payerCurrency.value)
    }

    @Test
    fun `onPayerMessageChanged updates message`() {
        viewModel.onPayerMessageChanged("Approve me")
        assertEquals("Approve me", viewModel.payerMessage.value)
    }

    @Test
    fun `onValidityTimeChanged updates validity time`() {
        viewModel.onValidityTimeChanged("3600")
        assertEquals("3600", viewModel.validityTime.value)
    }

    /** A create error posts a failure message and leaves no reference ID. */
    @Test
    fun `createPreApproval error path posts failure result`() = runTest {
        every { mockRepository.createPreApproval(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("bad"))

        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createPreApproval()

        assertNull(viewModel.referenceId.value)
        assertEquals("Create failed: bad", viewModel.result.value)
    }

    /** An exception during an operation is caught and surfaced in the console. */
    @Test
    fun `createPreApproval exception path posts error result`() = runTest {
        every { mockRepository.createPreApproval(any(), any(), any(), any()) } throws RuntimeException("kaboom")

        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createPreApproval()

        assertEquals("Error: kaboom", viewModel.result.value)
    }

    /** A blank validity time defaults to 0 in the submitted payload. */
    @Test
    fun `createPreApproval defaults blank validity time to zero`() = runTest {
        val payload = slot<PreApproval>()
        every { mockRepository.createPreApproval(any(), capture(payload), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createPreApproval()

        assertEquals(0, payload.captured.validityTime)
    }

    /** A status error posts a failure message. */
    @Test
    fun `checkStatus error path posts failure result`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.getPreApprovalStatus(any(), any(), any()) } returns flowOf(NetworkResult.Error("nope"))

        viewModel.checkStatus()

        assertEquals("Status failed: nope", viewModel.result.value)
    }

    /**
     * A blank payer currency falls back to the sandbox default while a non-blank payer message is
     * kept, exercising both ifBlank branches in the payload builder.
     */
    @Test
    fun `createPreApproval with blank currency and message set succeeds`() = runTest {
        every { mockRepository.createPreApproval(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.onPayerCurrencyChanged("")
        viewModel.onPayerMessageChanged("Approve me")
        viewModel.createPreApproval()

        assertNotNull(viewModel.referenceId.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** An exception carrying no message is caught and surfaced with an empty detail. */
    @Test
    fun `createPreApproval exception with null message posts error result`() = runTest {
        every { mockRepository.createPreApproval(any(), any(), any(), any()) } throws RuntimeException()

        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createPreApproval()

        assertEquals("Error: null", viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** A parsed status payload is printed via its string representation. */
    @Test
    fun `checkStatus with non-blank body prints payload`() = runTest {
        viewModel.referenceId.value = "ref-1"
        val status = PreApprovalStatus(status = StatusTypes.PENDING)
        every { mockRepository.getPreApprovalStatus(any(), any(), any()) } returns
            flowOf(NetworkResult.Success(status))

        viewModel.checkStatus()

        assertEquals(status.toString(), viewModel.result.value)
    }

    /** A status success with a null response reports the placeholder rather than crashing. */
    @Test
    fun `checkStatus with null body reports no status body`() = runTest {
        viewModel.referenceId.value = "ref-1"
        @Suppress("UNCHECKED_CAST")
        every { mockRepository.getPreApprovalStatus(any(), any(), any()) } returns
            (flowOf(NetworkResult.Success(null)) as Flow<NetworkResult<PreApprovalStatus>>)

        viewModel.checkStatus()

        assertEquals("No status body returned.", viewModel.result.value)
    }

    /** A leading Loading emission is ignored and the terminal Success is used to complete the flow. */
    @Test
    fun `createPreApproval ignores loading emission before terminal success`() = runTest {
        every { mockRepository.createPreApproval(any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Loading(), NetworkResult.Success(Unit))

        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createPreApproval()

        assertNotNull(viewModel.referenceId.value)
    }
}
