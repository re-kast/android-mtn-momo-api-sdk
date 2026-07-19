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
package io.rekast.sdk.sample.views.remittance.remittance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CircularProgressBarComponent
import io.rekast.sdk.sample.ui.components.general.MomoScaffold
import io.rekast.sdk.sample.ui.components.screens.PaymentDataDisplayComponent
import io.rekast.sdk.sample.ui.components.screens.PaymentDataScreenComponent
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Renders the Remittance Transfer screen, showing a payment capture form when no transaction
 * result is available, or a transaction summary once a [MomoTransaction] has been returned.
 *
 * @param navController [NavController] used to navigate between destinations via the drawer.
 * @param snackStateFlow Flow emitting [SnackBarComponentConfiguration] messages to display.
 * @param showProgressBar Whether to display a loading indicator instead of the form; defaults to false.
 * @param remittanceScreenViewModel ViewModel providing form state and callbacks; may be null in previews.
 * @param momoTransaction LiveData holding the completed [MomoTransaction]; null triggers the capture form.
 */
@Composable
fun RemittanceScreen(
    navController: NavController?,
    snackStateFlow: SharedFlow<SnackBarComponentConfiguration>,
    showProgressBar: Boolean = false,
    remittanceScreenViewModel: RemittanceScreenViewModel?,
    momoTransaction: MutableLiveData<MomoTransaction?>
) {
    MomoScaffold(
        titleRes = R.string.remittance_screen,
        navController = navController,
        snackStateFlow = snackStateFlow
    ) {
        if (!showProgressBar) {
            remittanceScreenViewModel?.let {
                val phoneNumber by remittanceScreenViewModel.phoneNumber.observeAsState(Constants.EMPTY_STRING)
                val financialId by remittanceScreenViewModel.financialId.observeAsState(Constants.EMPTY_STRING)
                val amount by remittanceScreenViewModel.amount.observeAsState(Constants.EMPTY_STRING)
                val paymentMessage by remittanceScreenViewModel.payerMessage.observeAsState(Constants.EMPTY_STRING)
                val paymentNote by remittanceScreenViewModel.payerNote.observeAsState(Constants.EMPTY_STRING)
                val deliveryNote by remittanceScreenViewModel.deliveryNote.observeAsState(Constants.EMPTY_STRING)
                val referenceIdToRefund by remittanceScreenViewModel.referenceIdToRefund.observeAsState(Constants.EMPTY_STRING)

                if (momoTransaction.value == null) {
                    PaymentDataScreenComponent(
                        title = stringResource(id = R.string.request_to_transfer_title),
                        submitButtonText = stringResource(id = R.string.transfer_submit_button),
                        phoneNumber = phoneNumber,
                        financialId = financialId,
                        referenceIdToRefund = referenceIdToRefund,
                        showReferenceIdToRefund = false,
                        amount = amount,
                        paymentMessage = paymentMessage,
                        paymentNote = paymentNote,
                        deliveryNote = deliveryNote,
                        onRequestPayButtonClicked = { remittanceScreenViewModel.transferRemittance() },
                        onPhoneNumberUpdated = { remittanceScreenViewModel.onPhoneNumberUpdated(it) },
                        onFinancialIdUpdated = { remittanceScreenViewModel.onFinancialIdUpdated(it) },
                        onReferenceIdToRefundUpdated = { remittanceScreenViewModel.onReferenceIdToRefundUpdated(it) },
                        onAmountUpdated = { remittanceScreenViewModel.onAmountUpdated(it) },
                        onPayerMessageUpdated = { remittanceScreenViewModel.onPayerMessageUpdated(it) },
                        onPayerNoteUpdated = { remittanceScreenViewModel.onPayerNoteUpdated(it) },
                        onDeliveryNoteUpdated = { remittanceScreenViewModel.onDeliveryNoteUpdated(it) }
                    )
                } else {
                    PaymentDataDisplayComponent(
                        title = stringResource(id = R.string.request_to_transfer_title),
                        momoTransaction = momoTransaction
                    )
                }
            }
        } else {
            CircularProgressBarComponent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemittanceScreenPreview() {
    RemittanceScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        showProgressBar = false,
        remittanceScreenViewModel = null,
        momoTransaction = MutableLiveData(null)
    )
}
