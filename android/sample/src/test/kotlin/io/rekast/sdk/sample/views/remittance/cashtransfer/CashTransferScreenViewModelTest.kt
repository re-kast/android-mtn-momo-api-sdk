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
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import io.rekast.sdk.model.CashTransfer
import io.rekast.sdk.model.CashTransferStatus
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.PartyTypes
import io.rekast.sdk.utils.PayerIdentificationType
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
        every { mockRepository.cashTransfer(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.sendCashTransfer()

        verify { mockRepository.cashTransfer(any(), any(), any(), any()) }
        assertNotNull(viewModel.referenceId.value)
        assertNotNull(viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies sendCashTransfer is skipped when the access token is blank. */
    @Test
    fun `sendCashTransfer does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""

        viewModel.sendCashTransfer()

        verify(exactly = 0) { mockRepository.cashTransfer(any(), any(), any(), any()) }
    }

    /** Verifies checkStatus is a no-op (no repository call) until a cash transfer has been sent. */
    @Test
    fun `checkStatus does nothing without a reference id`() = runTest {
        viewModel.checkStatus()

        verify(exactly = 0) { mockRepository.getCashTransferStatus(any(), any(), any()) }
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

    @Test
    fun `onPayerIdentificationTypeChanged updates id type`() {
        viewModel.onPayerIdentificationTypeChanged("PASS")
        assertEquals("PASS", viewModel.payerIdentificationType.value)
    }

    @Test
    fun `onPayerIdentificationNumberChanged updates id number`() {
        viewModel.onPayerIdentificationNumberChanged("A1234567")
        assertEquals("A1234567", viewModel.payerIdentificationNumber.value)
    }

    @Test
    fun `onPayerIdentityChanged updates identity`() {
        viewModel.onPayerIdentityChanged("256700000001")
        assertEquals("256700000001", viewModel.payerIdentity.value)
    }

    @Test
    fun `onPayerLanguageCodeChanged updates language code`() {
        viewModel.onPayerLanguageCodeChanged("en")
        assertEquals("en", viewModel.payerLanguageCode.value)
    }

    @Test
    fun `onPayerEmailChanged updates email`() {
        viewModel.onPayerEmailChanged("jane@example.com")
        assertEquals("jane@example.com", viewModel.payerEmail.value)
    }

    @Test
    fun `onPayerMsisdnChanged updates payer msisdn`() {
        viewModel.onPayerMsisdnChanged("256700000002")
        assertEquals("256700000002", viewModel.payerMsisdn.value)
    }

    @Test
    fun `onPayerGenderChanged updates gender`() {
        viewModel.onPayerGenderChanged("FEMALE")
        assertEquals("FEMALE", viewModel.payerGender.value)
    }

    @Test
    fun `onOriginatingCountryChanged updates originating country`() {
        viewModel.onOriginatingCountryChanged("UG")
        assertEquals("UG", viewModel.originatingCountry.value)
    }

    @Test
    fun `onOriginalAmountChanged updates original amount`() {
        viewModel.onOriginalAmountChanged("370000")
        assertEquals("370000", viewModel.originalAmount.value)
    }

    @Test
    fun `onOriginalCurrencyChanged updates original currency`() {
        viewModel.onOriginalCurrencyChanged("UGX")
        assertEquals("UGX", viewModel.originalCurrency.value)
    }

    /**
     * Verifies the full KYC payload is forwarded: every form field is mapped onto the [CashTransfer]
     * sent to the repository, the payee is built from the payee MSISDN, and the free-text
     * `payerIdentificationType` is parsed into the [PayerIdentificationType] enum.
     */
    @Test
    fun `sendCashTransfer sends the full payload with all KYC fields`() = runTest {
        val sent = slot<CashTransfer>()
        every { mockRepository.cashTransfer(any(), capture(sent), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onCurrencyChanged("EUR")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.onPayerMessageChanged("From abroad")
        viewModel.onPayeeNoteChanged("Family support")
        viewModel.onPayerIdentificationTypeChanged("pass")
        viewModel.onPayerIdentificationNumberChanged("A1234567")
        viewModel.onPayerIdentityChanged("256700000001")
        viewModel.onPayerFirstNameChanged("Jane")
        viewModel.onPayerSurNameChanged("Doe")
        viewModel.onPayerLanguageCodeChanged("en")
        viewModel.onPayerEmailChanged("jane@example.com")
        viewModel.onPayerMsisdnChanged("256700000002")
        viewModel.onPayerGenderChanged("FEMALE")
        viewModel.onOriginatingCountryChanged("UG")
        viewModel.onOriginalAmountChanged("370000")
        viewModel.onOriginalCurrencyChanged("UGX")
        viewModel.sendCashTransfer()

        val payload = sent.captured
        assertEquals("100", payload.amount)
        assertEquals("EUR", payload.currency)
        assertEquals(PartyTypes.MSISDN, payload.payee.partyIdType)
        assertEquals("256700000000", payload.payee.partyId)
        assertEquals("From abroad", payload.payerMessage)
        assertEquals("Family support", payload.payeeNote)
        assertEquals(PayerIdentificationType.PASS, payload.payerIdentificationType)
        assertEquals("A1234567", payload.payerIdentificationNumber)
        assertEquals("256700000001", payload.payerIdentity)
        assertEquals("Jane", payload.payerFirstName)
        assertEquals("Doe", payload.payerSurName)
        assertEquals("en", payload.payerLanguageCode)
        assertEquals("jane@example.com", payload.payerEmail)
        assertEquals("256700000002", payload.payerMsisdn)
        assertEquals("FEMALE", payload.payerGender)
        assertEquals("UG", payload.originatingCountry)
        assertEquals("370000", payload.originalAmount)
        assertEquals("UGX", payload.originalCurrency)
    }

    /** A blank / unrecognised payer ID type is parsed to null rather than crashing. */
    @Test
    fun `sendCashTransfer parses an unrecognised payer id type to null`() = runTest {
        val sent = slot<CashTransfer>()
        every { mockRepository.cashTransfer(any(), capture(sent), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.onPayerIdentificationTypeChanged("not-a-real-type")
        viewModel.sendCashTransfer()

        assertNull(sent.captured.payerIdentificationType)
    }

    /** A send error posts a failure message and leaves no reference ID. */
    @Test
    fun `sendCashTransfer error path posts failure result`() = runTest {
        every { mockRepository.cashTransfer(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("bad"))

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.sendCashTransfer()

        assertNull(viewModel.referenceId.value)
        assertEquals("Send failed: bad", viewModel.result.value)
    }

    /** An exception during an operation is caught and surfaced in the console. */
    @Test
    fun `sendCashTransfer exception path posts error result`() = runTest {
        every { mockRepository.cashTransfer(any(), any(), any(), any()) } throws RuntimeException("kaboom")

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.sendCashTransfer()

        assertEquals("Error: kaboom", viewModel.result.value)
    }

    /** A status error posts a failure message. */
    @Test
    fun `checkStatus error path posts failure result`() = runTest {
        viewModel.referenceId.value = "ref-1"
        every { mockRepository.getCashTransferStatus(any(), any(), any()) } returns flowOf(NetworkResult.Error("nope"))

        viewModel.checkStatus()

        assertEquals("Status failed: nope", viewModel.result.value)
    }

    /**
     * A blank currency falls back to the sandbox default while non-blank payer names are kept,
     * exercising all three ifBlank branches in the payload builder.
     */
    @Test
    fun `sendCashTransfer with blank currency and names set succeeds`() = runTest {
        every { mockRepository.cashTransfer(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.onCurrencyChanged("")
        viewModel.onPayerFirstNameChanged("Jane")
        viewModel.onPayerSurNameChanged("Doe")
        viewModel.sendCashTransfer()

        assertNotNull(viewModel.referenceId.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** An exception carrying no message is caught and surfaced with an empty detail. */
    @Test
    fun `sendCashTransfer exception with null message posts error result`() = runTest {
        every { mockRepository.cashTransfer(any(), any(), any(), any()) } throws RuntimeException()

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.sendCashTransfer()

        assertEquals("Error: null", viewModel.result.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** A parsed status payload is printed via its string representation. */
    @Test
    fun `checkStatus with non-blank body prints payload`() = runTest {
        viewModel.referenceId.value = "ref-1"
        val cashTransferStatus = CashTransferStatus(amount = "100", currency = "EUR", externalId = "ext-1", payerMessage = "msg", payeeNote = "note")
        every { mockRepository.getCashTransferStatus(any(), any(), any()) } returns
            flowOf(NetworkResult.Success(cashTransferStatus))

        viewModel.checkStatus()

        assertEquals(cashTransferStatus.toString(), viewModel.result.value)
    }

    /** A status success with a null response reports the placeholder rather than crashing. */
    @Test
    fun `checkStatus with null body reports no status body`() = runTest {
        viewModel.referenceId.value = "ref-1"
        @Suppress("UNCHECKED_CAST")
        every { mockRepository.getCashTransferStatus(any(), any(), any()) } returns
            (flowOf(NetworkResult.Success(null)) as Flow<NetworkResult<CashTransferStatus>>)

        viewModel.checkStatus()

        assertEquals("No status body returned.", viewModel.result.value)
    }

    /** A leading Loading emission is ignored and the terminal Success is used to complete the flow. */
    @Test
    fun `sendCashTransfer ignores loading emission before terminal success`() = runTest {
        every { mockRepository.cashTransfer(any(), any(), any(), any()) } returns
            flowOf(NetworkResult.Loading(), NetworkResult.Success(Unit))

        viewModel.onAmountChanged("100")
        viewModel.onPayeeMsisdnChanged("256700000000")
        viewModel.sendCashTransfer()

        assertNotNull(viewModel.referenceId.value)
    }
}
