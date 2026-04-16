/*
 * Copyright 2023-2024, Benjamin Mwalimu Mulyungi
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
package io.rekast.sdk.sample.views.disbursement.refund

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.sample.utils.SampleConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DisbursementRefundScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository = mockk<DefaultRepository>(relaxed = true)
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    private lateinit var viewModel: DisbursementRefundScreenViewModel

    @Before
    fun setUp() {
        viewModel = DisbursementRefundScreenViewModel(mockRepository, mockContext, mockConfig)
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
}
