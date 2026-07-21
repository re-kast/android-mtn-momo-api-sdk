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
package io.rekast.sdk.sample.views.collection.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import io.rekast.sdk.model.Payment
import io.rekast.sdk.model.PaymentStatus
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
class PaymentScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    private lateinit var viewModel: PaymentScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Utils)
        every { mockStorage.getAccessToken() } returns "test-access-token"
        every { Utils.getProductSubscriptionKeys(any(), any()) } returns "test-subscription-key"
        every { mockConfig.apiVersionV2 } returns "v2_0"
        every { mockConfig.environment } returns "sandbox"
        viewModel = PaymentScreenViewModel(mockRepository, mockStorage, testDispatcherProvider, mockConfig)
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
    fun `field setters update state`() {
        viewModel.onAmountChanged("100")
        viewModel.onCurrencyChanged("USD")
        viewModel.onCustomerReferenceChanged("+46070911111")
        viewModel.onReceiverMessageChanged("thanks")
        viewModel.onSenderNoteChanged("bill")
        assertEquals("100", viewModel.amount.value)
        assertEquals("USD", viewModel.currency.value)
        assertEquals("+46070911111", viewModel.customerReference.value)
        assertEquals("thanks", viewModel.receiverMessage.value)
        assertEquals("bill", viewModel.senderNote.value)
    }

    /** A successful create stores the reference id and builds the expected Payment/Money payload. */
    @Test
    fun `createPayment stores reference id and builds payload on success`() = runTest {
        val payload = slot<Payment>()
        every { mockRepository.createPayment(any(), capture(payload), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onCustomerReferenceChanged("+46070911111")
        viewModel.createPayment()

        verify { mockRepository.createPayment(any(), any(), any(), any(), any()) }
        assertNotNull(viewModel.referenceId.value)
        assertEquals("100", payload.captured.money?.amount)
        assertEquals("EUR", payload.captured.money?.currency)
        assertEquals("+46070911111", payload.captured.customerReference)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** A blank currency falls back to the sandbox default in the built payload. */
    @Test
    fun `createPayment defaults blank currency to sandbox currency`() = runTest {
        val payload = slot<Payment>()
        every { mockRepository.createPayment(any(), capture(payload), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onCurrencyChanged("")
        viewModel.createPayment()

        assertEquals("EUR", payload.captured.money?.currency)
    }

    /** A create error posts a failure message and leaves no reference id. */
    @Test
    fun `createPayment error path posts failure result`() = runTest {
        every { mockRepository.createPayment(any(), any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("bad"))

        viewModel.onAmountChanged("100")
        viewModel.createPayment()

        assertNull(viewModel.referenceId.value)
        assertEquals("Create failed: bad", viewModel.result.value)
    }

    /** An exception is caught and surfaced in the console. */
    @Test
    fun `createPayment exception path posts error result`() = runTest {
        every { mockRepository.createPayment(any(), any(), any(), any(), any()) } throws RuntimeException("kaboom")

        viewModel.onAmountChanged("100")
        viewModel.createPayment()

        assertEquals("Error: kaboom", viewModel.result.value)
    }

    /** Create is skipped when the access token is blank. */
    @Test
    fun `createPayment does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""

        viewModel.createPayment()

        verify(exactly = 0) { mockRepository.createPayment(any(), any(), any(), any(), any()) }
    }

    /** Status is a no-op until a payment has been created. */
    @Test
    fun `checkStatus does nothing without a reference id`() = runTest {
        viewModel.checkStatus()

        verify(exactly = 0) { mockRepository.getPaymentStatus(any(), any(), any(), any()) }
    }

    /** A parsed status payload is printed via its string representation. */
    @Test
    fun `checkStatus prints the status payload`() = runTest {
        viewModel.referenceId.value = "ref-1"
        val status = PaymentStatus(referenceId = "ref-1", status = StatusTypes.SUCCESSFUL)
        every { mockRepository.getPaymentStatus(any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Success(status))

        viewModel.checkStatus()

        assertEquals(status.toString(), viewModel.result.value)
    }

    /** A status success with a null response reports the placeholder rather than crashing. */
    @Test
    fun `checkStatus with null body reports no status body`() = runTest {
        viewModel.referenceId.value = "ref-1"
        @Suppress("UNCHECKED_CAST")
        every { mockRepository.getPaymentStatus(any(), any(), any(), any()) } returns
            (flowOf(NetworkResult.Success(null)) as Flow<NetworkResult<PaymentStatus>>)

        viewModel.checkStatus()

        assertEquals("No status body returned.", viewModel.result.value)
    }

    /** A status error posts a failure message. */
    @Test
    fun `checkStatus error path posts failure result`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.getPaymentStatus(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("nope"))

        viewModel.checkStatus()

        assertEquals("Status failed: nope", viewModel.result.value)
    }

    /** A leading Loading emission is ignored and the terminal Success is used. */
    @Test
    fun `createPayment ignores loading emission before terminal success`() = runTest {
        every { mockRepository.createPayment(any(), any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Loading(), NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.createPayment()

        assertNotNull(viewModel.referenceId.value)
    }
}
