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
package io.rekast.sdk.sample.ui.components.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.utils.annotation.PreviewWithBackgroundExcludeGenerated

/**
 * Renders a payment data capture form inside a design-system [MomoCard], with conditionally shown
 * fields for financial ID, reference ID to refund, and delivery note, followed by a submit button.
 *
 * Built from the shared [LabeledField] and [OperationActionButton] building blocks so it matches the
 * card-based look of the other operation screens (Invoice, Pre-Approval, Cash Transfer).
 *
 * @param modifier Modifier applied to the scrollable container.
 * @param title Card title displayed above the form fields.
 * @param submitButtonText Label for the submit button.
 * @param phoneNumber Current value of the phone number field.
 * @param financialId Current value of the financial ID field.
 * @param showFinancialId Whether to display the financial ID field; defaults to true.
 * @param referenceIdToRefund Current value of the reference ID to refund field.
 * @param showReferenceIdToRefund Whether to display the reference ID to refund field; defaults to true.
 * @param amount Current value of the amount field.
 * @param paymentMessage Current value of the payer message field.
 * @param paymentNote Current value of the payer note field.
 * @param deliveryNote Current value of the delivery note field.
 * @param showDeliveryTextField Whether to display the delivery note field; defaults to true.
 * @param onRequestPayButtonClicked Callback invoked when the submit button is clicked.
 * @param onPhoneNumberUpdated Callback invoked when the phone number field value changes.
 * @param onFinancialIdUpdated Callback invoked when the financial ID field value changes.
 * @param onReferenceIdToRefundUpdated Callback invoked when the reference ID to refund field value changes.
 * @param onAmountUpdated Callback invoked when the amount field value changes.
 * @param onPayerMessageUpdated Callback invoked when the payment message field value changes.
 * @param onPayerNoteUpdated Callback invoked when the payment note field value changes.
 * @param onDeliveryNoteUpdated Callback invoked when the delivery note field value changes.
 */
@Composable
fun PaymentDataScreenComponent(
    modifier: Modifier = Modifier,
    title: String,
    submitButtonText: String,
    phoneNumber: String,
    financialId: String,
    showFinancialId: Boolean = true,
    referenceIdToRefund: String,
    showReferenceIdToRefund: Boolean = true,
    amount: String,
    paymentMessage: String,
    paymentNote: String,
    deliveryNote: String,
    showDeliveryTextField: Boolean = true,
    onRequestPayButtonClicked: () -> Unit,
    onPhoneNumberUpdated: (String) -> Unit,
    onFinancialIdUpdated: (String) -> Unit,
    onReferenceIdToRefundUpdated: (String) -> Unit,
    onAmountUpdated: (String) -> Unit,
    onPayerMessageUpdated: (String) -> Unit,
    onPayerNoteUpdated: (String) -> Unit,
    onDeliveryNoteUpdated: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MomoCard {
                CardTitle(title = title)
                Spacer(modifier = Modifier.height(8.dp))
                LabeledField(stringResource(id = R.string.phone_number), phoneNumber, onPhoneNumberUpdated, keyboardType = KeyboardType.Phone)
                if (showFinancialId) {
                    LabeledField(stringResource(id = R.string.financial_id), financialId, onFinancialIdUpdated)
                }
                if (showReferenceIdToRefund) {
                    LabeledField(stringResource(id = R.string.reference_id_refund), referenceIdToRefund, onReferenceIdToRefundUpdated)
                }
                LabeledField(stringResource(id = R.string.amount), amount, onAmountUpdated, keyboardType = KeyboardType.Number)
                LabeledField(stringResource(id = R.string.payment_message), paymentMessage, onPayerMessageUpdated)
                LabeledField(stringResource(id = R.string.payment_note), paymentNote, onPayerNoteUpdated, singleLine = false, imeAction = ImeAction.Default)
                if (showDeliveryTextField) {
                    LabeledField(stringResource(id = R.string.delivery_note), deliveryNote, onDeliveryNoteUpdated, singleLine = false, imeAction = ImeAction.Default)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OperationActionButton(
                    text = submitButtonText,
                    onClick = onRequestPayButtonClicked,
                    enabled = phoneNumber.isNotEmpty() && amount.isNotEmpty() &&
                        paymentMessage.isNotEmpty() && paymentNote.isNotEmpty()
                )
            }
        }
    }
}

@PreviewWithBackgroundExcludeGenerated
@Composable
fun PaymentDataScreenComponentPreview() {
    PaymentDataScreenComponent(
        title = "Request to Pay",
        submitButtonText = "Pay Now",
        phoneNumber = "256770000000",
        financialId = "",
        showFinancialId = false,
        referenceIdToRefund = "",
        showReferenceIdToRefund = false,
        amount = "1000",
        paymentMessage = "Goods payment",
        paymentNote = "Monthly subscription",
        deliveryNote = "",
        showDeliveryTextField = false,
        onRequestPayButtonClicked = {},
        onPhoneNumberUpdated = {},
        onFinancialIdUpdated = {},
        onReferenceIdToRefundUpdated = {},
        onAmountUpdated = {},
        onPayerMessageUpdated = {},
        onPayerNoteUpdated = {},
        onDeliveryNoteUpdated = {}
    )
}
