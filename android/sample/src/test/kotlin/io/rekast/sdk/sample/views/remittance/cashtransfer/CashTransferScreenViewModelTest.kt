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
package io.rekast.sdk.sample.views.remittance.cashtransfer

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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CashTransferScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    private lateinit var viewModel: CashTransferScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Utils)
        every { mockStorage.getAccessToken() } returns "test-access-token"
        every { Utils.getProductSubscriptionKeys(any(), any()) } returns "test-subscription-key"
        every { mockConfig.apiVersionV1 } returns "v1_0"
        every { mockConfig.environment } returns "sandbox"
        viewModel = CashTransferScreenViewModel(mockRepository, mockStorage, testDispatcherProvider, mockConfig)
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
        assertEquals("EUR", viewModel.currency.value)
    }

    @Test
    fun `onAmountChanged updates amount`() {
        viewModel.onAmountChanged("500")
        assertEquals("500", viewModel.amount.value)
    }

    @Test
    fun `onPayeeMsisdnChanged updates payee msisdn`() {
        viewModel.onPayeeMsisdnChanged("256700000000")
        assertEquals("256700000000", viewModel.payeeMsisdn.value)
    }

    /** Verifies sendCashTransfer calls the repository, stores a reference ID, and clears the progress bar. */
    @Test
    fun `sendCashTransfer stores reference id on success`() = runTest {
        every { mockRepository.cashTransfer(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.sendCashTransfer()

        verify { mockRepository.cashTransfer(any(), any(), any(), any(), any()) }
        assertNotNull(viewModel.referenceId.value)
        assertNotNull(viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies sendCashTransfer is skipped when the access token is blank. */
    @Test
    fun `sendCashTransfer does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""

        viewModel.sendCashTransfer()

        verify(exactly = 0) { mockRepository.cashTransfer(any(), any(), any(), any(), any()) }
    }

    /** Verifies checkStatus is a no-op (no repository call) until a cash transfer has been sent. */
    @Test
    fun `checkStatus does nothing without a reference id`() = runTest {
        viewModel.checkStatus()

        verify(exactly = 0) { mockRepository.getCashTransferStatus(any(), any(), any(), any()) }
    }

    @Test
    fun `onCurrencyChanged updates currency`() {
        viewModel.onCurrencyChanged("USD")
        assertEquals("USD", viewModel.currency.value)
    }

    @Test
    fun `onPayerMessageChanged updates message`() {
        viewModel.onPayerMessageChanged("From abroad")
        assertEquals("From abroad", viewModel.payerMessage.value)
    }

    @Test
    fun `onPayeeNoteChanged updates note`() {
        viewModel.onPayeeNoteChanged("Family support")
        assertEquals("Family support", viewModel.payeeNote.value)
    }

    @Test
    fun `onPayerFirstNameChanged updates first name`() {
        viewModel.onPayerFirstNameChanged("Jane")
        assertEquals("Jane", viewModel.payerFirstName.value)
    }

    @Test
    fun `onPayerSurNameChanged updates surname`() {
        viewModel.onPayerSurNameChanged("Doe")
        assertEquals("Doe", viewModel.payerSurName.value)
    }

    /** A send error posts a failure message and leaves no reference ID. */
    @Test
    fun `sendCashTransfer error path posts failure result`() = runTest {
        every { mockRepository.cashTransfer(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("bad"))

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.sendCashTransfer()

        assertNull(viewModel.referenceId.value)
        assertEquals("Send failed: bad", viewModel.result.value)
    }

    /** An exception during an operation is caught and surfaced in the console. */
    @Test
    fun `sendCashTransfer exception path posts error result`() = runTest {
        every { mockRepository.cashTransfer(any(), any(), any(), any(), any()) } throws RuntimeException("kaboom")

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.sendCashTransfer()

        assertEquals("Error: kaboom", viewModel.result.value)
    }

    /** A status error posts a failure message. */
    @Test
    fun `checkStatus error path posts failure result`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.getCashTransferStatus(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("nope"))

        viewModel.checkStatus()

        assertEquals("Status failed: nope", viewModel.result.value)
    }

    /** A blank status body reports the placeholder rather than an empty console. */
    @Test
    fun `checkStatus with blank body reports no status body`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.getCashTransferStatus(any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success("".toResponseBody("application/json".toMediaType())))

        viewModel.checkStatus()

        assertEquals("No status body returned.", viewModel.result.value)
    }
}
