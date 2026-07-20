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
package io.rekast.sdk.sample.views.disbursement.deposit

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
class DisbursementDepositScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider = object : DispatcherProvider {
        override fun io(): CoroutineDispatcher = testDispatcher
    }
    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    private lateinit var viewModel: DisbursementDepositScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(Utils)
        every { mockStorage.getAccessToken() } returns "test-access-token"
        every { Utils.getProductSubscriptionKeys(any(), any()) } returns "test-subscription-key"
        every { mockConfig.apiVersionV1 } returns "v1_0"
        every { mockConfig.environment } returns "sandbox"
        viewModel = DisbursementDepositScreenViewModel(mockRepository, mockStorage, testDispatcherProvider, mockConfig)
    }

    @After
    fun tearDown() {
        unmockkObject(Utils)
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has showProgressBar false`() {
        assertFalse(viewModel.showProgressBar.value!!)
    }

    @Test
    fun `initial state has null momoTransaction`() {
        assertNull(viewModel.momoTransaction.value)
    }

    @Test
    fun `initial state has all form fields empty`() {
        assertEquals("", viewModel.phoneNumber.value)
        assertEquals("", viewModel.financialId.value)
        assertEquals("", viewModel.amount.value)
        assertEquals("", viewModel.payerMessage.value)
        assertEquals("", viewModel.payerNote.value)
        assertEquals("", viewModel.deliveryNote.value)
        assertEquals("", viewModel.referenceIdToRefund.value)
    }

    @Test
    fun `onPhoneNumberUpdated updates phoneNumber LiveData`() {
        viewModel.onPhoneNumberUpdated("256700000000")
        assertEquals("256700000000", viewModel.phoneNumber.value)
    }

    @Test
    fun `onFinancialIdUpdated updates financialId LiveData`() {
        viewModel.onFinancialIdUpdated("FIN123456")
        assertEquals("FIN123456", viewModel.financialId.value)
    }

    @Test
    fun `onAmountUpdated updates amount LiveData`() {
        viewModel.onAmountUpdated("500")
        assertEquals("500", viewModel.amount.value)
    }

    @Test
    fun `onPayerMessageUpdated updates payerMessage LiveData`() {
        viewModel.onPayerMessageUpdated("Test payer message")
        assertEquals("Test payer message", viewModel.payerMessage.value)
    }

    @Test
    fun `onPayerNoteUpdated updates payerNote LiveData`() {
        viewModel.onPayerNoteUpdated("Test payer note")
        assertEquals("Test payer note", viewModel.payerNote.value)
    }

    @Test
    fun `onDeliveryNoteUpdated updates deliveryNote LiveData`() {
        viewModel.onDeliveryNoteUpdated("Test delivery note")
        assertEquals("Test delivery note", viewModel.deliveryNote.value)
    }

    @Test
    fun `onReferenceIdToRefundUpdated updates referenceIdToRefund LiveData`() {
        viewModel.onReferenceIdToRefundUpdated("REF-ABC-001")
        assertEquals("REF-ABC-001", viewModel.referenceIdToRefund.value)
    }

    /** Verifies deposit submits, polls status, posts the transaction, and clears the progress bar. */
    @Test
    fun `deposit submits then fetches status and posts transaction`() = runTest {
        every { mockRepository.deposit(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))
        every { mockRepository.getDepositStatus(any(), any(), any()) } returns
            flowOf(
                NetworkResult.Success(
                    """{"amount":"100","currency":"EUR","externalId":"ext-1","payerMessage":"msg","payeeNote":"note","status":"SUCCESSFUL"}"""
                        .toResponseBody("application/json".toMediaType())
                )
            )

        viewModel.onPhoneNumberUpdated("256700000000")
        viewModel.onAmountUpdated("100")
        viewModel.deposit()

        verify { mockRepository.deposit(any(), any(), any(), any()) }
        verify { mockRepository.getDepositStatus(any(), any(), any()) }
        assertNotNull(viewModel.momoTransaction.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** Verifies deposit skips the network call and stays idle when the access token is blank. */
    @Test
    fun `deposit does not call repository when access token is blank`() = runTest {
        every { mockStorage.getAccessToken() } returns ""

        viewModel.deposit()

        verify(exactly = 0) { mockRepository.deposit(any(), any(), any(), any()) }
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** A submit error does not poll for status and leaves the transaction null. */
    @Test
    fun `deposit error path does not fetch status`() = runTest {
        every { mockRepository.deposit(any(), any(), any(), any()) } returns flowOf(NetworkResult.Error("boom"))

        viewModel.onPhoneNumberUpdated("256700000000")
        viewModel.onAmountUpdated("100")
        viewModel.deposit()

        verify(exactly = 0) { mockRepository.getDepositStatus(any(), any(), any()) }
        assertNull(viewModel.momoTransaction.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** An exception during submit is caught and the progress bar is cleared. */
    @Test
    fun `deposit exception path clears progress bar`() = runTest {
        every { mockRepository.deposit(any(), any(), any(), any()) } throws RuntimeException("network down")

        viewModel.onPhoneNumberUpdated("256700000000")
        viewModel.onAmountUpdated("100")
        viewModel.deposit()

        assertNull(viewModel.momoTransaction.value)
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** A status error after a successful submit leaves the transaction null. */
    @Test
    fun `deposit status error leaves transaction null`() = runTest {
        every { mockRepository.deposit(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))
        every { mockRepository.getDepositStatus(any(), any(), any()) } returns flowOf(NetworkResult.Error("status boom"))

        viewModel.onPhoneNumberUpdated("256700000000")
        viewModel.onAmountUpdated("100")
        viewModel.deposit()

        assertNull(viewModel.momoTransaction.value)
    }

    /** A status body that cannot be parsed posts a null transaction rather than crashing. */
    @Test
    fun `deposit status with unparseable body posts null transaction`() = runTest {
        every { mockRepository.deposit(any(), any(), any(), any()) } returns flowOf(NetworkResult.Success(Unit))
        every { mockRepository.getDepositStatus(any(), any(), any()) } returns
            flowOf(NetworkResult.Success("not-json".toResponseBody("application/json".toMediaType())))

        viewModel.onPhoneNumberUpdated("256700000000")
        viewModel.onAmountUpdated("100")
        viewModel.deposit()

        assertNull(viewModel.momoTransaction.value)
    }
}
