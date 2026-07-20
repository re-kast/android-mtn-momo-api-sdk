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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.InfoRow
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.utils.annotation.PreviewWithBackgroundExcludeGenerated
import io.rekast.sdk.utils.StatusTypes

/**
 * Renders a read-only summary of a completed MOMO transaction inside a card: amount, currency,
 * financial transaction ID, external ID, payer/payee, message, note, status, reason, and reference
 * ID to refund. Fields that are null or blank are omitted automatically.
 *
 * @param modifier Modifier applied to the root container.
 * @param title Card title displayed above the transaction details.
 * @param momoTransaction LiveData holding the [MomoTransaction] to display.
 */
@Composable
fun PaymentDataDisplayComponent(modifier: Modifier = Modifier, title: String, momoTransaction: MutableLiveData<MomoTransaction?>) {
    val transaction by momoTransaction.observeAsState()
    val counterpartyLabel = if (transaction?.payee == null) {
        stringResource(id = R.string.payer)
    } else {
        stringResource(id = R.string.payee)
    }
    val counterparty = (transaction?.payee ?: transaction?.payer)?.let { holder ->
        holder.partyId?.let { "$it -- ${holder.partyIdType.partyType}" }
    }

    MomoCard(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        CardTitle(title = title)
        Spacer(modifier = Modifier.height(12.dp))
        InfoRow(label = stringResource(id = R.string.display_amount), value = transaction?.amount)
        InfoRow(label = stringResource(id = R.string.currency), value = transaction?.currency)
        InfoRow(label = stringResource(id = R.string.financial_transaction_id), value = transaction?.financialTransactionId)
        InfoRow(label = stringResource(id = R.string.external_id), value = transaction?.externalId)
        InfoRow(label = counterpartyLabel, value = counterparty)
        InfoRow(label = stringResource(id = R.string.payment_message_display), value = transaction?.payerMessage)
        InfoRow(label = stringResource(id = R.string.payment_note_display), value = transaction?.payeeNote)
        InfoRow(label = stringResource(id = R.string.status), value = transaction?.status?.name)
        InfoRow(label = stringResource(id = R.string.reason), value = transaction?.reason)
        InfoRow(label = stringResource(id = R.string.reference_id_to_refund), value = transaction?.referenceIdToRefund)
    }
}

@PreviewWithBackgroundExcludeGenerated
@Composable
fun PaymentDataDisplayComponentPreview() {
    PaymentDataDisplayComponent(
        title = stringResource(id = R.string.request_to_pay_title),
        momoTransaction = MutableLiveData(
            MomoTransaction(
                amount = "1500",
                currency = "EUR",
                externalId = "947354",
                payerMessage = "Payment for goods",
                payeeNote = "Monthly subscription",
                status = StatusTypes.SUCCESSFUL
            )
        )
    )
}
