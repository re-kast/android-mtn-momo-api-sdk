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
package io.rekast.sdk.sample.views.collection.invoice

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
class InvoiceScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    private lateinit var viewModel: InvoiceScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Utils)
        every { mockStorage.getAccessToken() } returns "test-access-token"
        every { Utils.getProductSubscriptionKeys(any(), any()) } returns "test-subscription-key"
        every { mockConfig.apiVersionV1 } returns "v1_0"
        every { mockConfig.environment } returns "sandbox"
        viewModel = InvoiceScreenViewModel(mockRepository, mockStorage, testDispatcherProvider, mockConfig)
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
    fun `onPayerMsisdnChanged updates payer msisdn`() {
        viewModel.onPayerMsisdnChanged("256700000000")
        assertEquals("256700000000", viewModel.payerMsisdn.value)
    }

    /** Verifies createInvoice calls the repository, stores a reference ID, and clears the progress bar. */
    @Test
    fun `createInvoice stores reference id on success`() = runTest {
        every { mockRepository.createInvoice(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createInvoice()

        verify { mockRepository.createInvoice(any(), any(), any(), any(), any()) }
        assertNotNull(viewModel.referenceId.value)
        assertNotNull(viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies createInvoice is skipped when the access token is blank. */
    @Test
    fun `createInvoice does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""

        viewModel.createInvoice()

        verify(exactly = 0) { mockRepository.createInvoice(any(), any(), any(), any(), any()) }
    }

    /** Verifies checkStatus is a no-op (no repository call) until an invoice has been created. */
    @Test
    fun `checkStatus does nothing without a reference id`() = runTest {
        viewModel.checkStatus()

        verify(exactly = 0) { mockRepository.getInvoiceStatus(any(), any(), any(), any()) }
    }

    /** Verifies checkStatus fetches and prints the status once an invoice exists. */
    @Test
    fun `checkStatus fetches status after create`() = runTest {
        every { mockRepository.createInvoice(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))
        every { mockRepository.getInvoiceStatus(any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success("""{"status":"PENDING"}""".toResponseBody("application/json".toMediaType())))

        viewModel.onAmountChanged("100")
        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createInvoice()
        viewModel.checkStatus()

        verify { mockRepository.getInvoiceStatus(any(), any(), any(), any()) }
        assertNotNull(viewModel.result.value)
    }

    @Test
    fun `onCurrencyChanged updates currency`() {
        viewModel.onCurrencyChanged("USD")
        assertEquals("USD", viewModel.currency.value)
    }

    @Test
    fun `onValidityDurationChanged updates validity duration`() {
        viewModel.onValidityDurationChanged("3600")
        assertEquals("3600", viewModel.validityDuration.value)
    }

    @Test
    fun `onDescriptionChanged updates description`() {
        viewModel.onDescriptionChanged("Invoice for order 42")
        assertEquals("Invoice for order 42", viewModel.description.value)
    }

    /** A create error posts a failure message and leaves no reference ID. */
    @Test
    fun `createInvoice error path posts failure result`() = runTest {
        every { mockRepository.createInvoice(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("bad"))

        viewModel.onAmountChanged("100")
        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createInvoice()

        assertNull(viewModel.referenceId.value)
        assertEquals("Create failed: bad", viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** An exception during an operation is caught and surfaced in the console. */
    @Test
    fun `createInvoice exception path posts error result`() = runTest {
        every { mockRepository.createInvoice(any(), any(), any(), any(), any()) } throws RuntimeException("kaboom")

        viewModel.onAmountChanged("100")
        viewModel.onPayerMsisdnChanged("256700000000")
        viewModel.createInvoice()

        assertEquals("Error: kaboom", viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** A status error posts a failure message. */
    @Test
    fun `checkStatus error path posts failure result`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.getInvoiceStatus(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("nope"))

        viewModel.checkStatus()

        assertEquals("Status failed: nope", viewModel.result.value)
    }

    /** A blank status body reports the placeholder rather than an empty console. */
    @Test
    fun `checkStatus with blank body reports no status body`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.getInvoiceStatus(any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success("".toResponseBody("application/json".toMediaType())))

        viewModel.checkStatus()

        assertEquals("No status body returned.", viewModel.result.value)
    }

    /** Cancel succeeds and reports the cancelled reference. */
    @Test
    fun `cancelInvoice success posts cancelled result`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.cancelInvoice(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.cancelInvoice()

        assertEquals("Invoice ref-1 cancelled.", viewModel.result.value)
    }

    /** Cancel error posts a failure message. */
    @Test
    fun `cancelInvoice error posts failure result`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.cancelInvoice(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("cant"))

        viewModel.cancelInvoice()

        assertEquals("Cancel failed: cant", viewModel.result.value)
    }

    /** Cancel is a no-op (no repository call) before any invoice has been created. */
    @Test
    fun `cancelInvoice without reference does nothing`() = runTest {
        viewModel.cancelInvoice()

        verify(exactly = 0) { mockRepository.cancelInvoice(any(), any(), any(), any()) }
    }

    /** Cancel is skipped when the access token is blank. */
    @Test
    fun `cancelInvoice does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""
        viewModel.referenceId.value = "ref-1"

        viewModel.cancelInvoice()

        verify(exactly = 0) { mockRepository.cancelInvoice(any(), any(), any(), any()) }
    }
}
