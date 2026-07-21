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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.CircularProgressBarComponent
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.ui.components.general.MomoScaffold
import io.rekast.sdk.sample.ui.components.screens.LabeledField
import io.rekast.sdk.sample.ui.components.screens.OperationActionButton
import io.rekast.sdk.sample.ui.components.screens.OperationConsole
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Renders the Remittance Cash Transfer (V2) screen: a send form plus a check-status action, with a
 * console panel showing each operation's outcome.
 *
 * @param navController Used by the drawer; may be null in previews.
 * @param snackStateFlow Flow of snackbar messages.
 * @param showProgressBar Whether to show the loading indicator.
 * @param viewModel Provides form state and the send/status actions; null in previews.
 */
@Composable
fun CashTransferScreen(navController: NavController?, snackStateFlow: SharedFlow<SnackBarComponentConfiguration>, showProgressBar: Boolean = false, viewModel: CashTransferScreenViewModel?) {
    MomoScaffold(titleRes = R.string.cash_transfer_screen, navController = navController, snackStateFlow = snackStateFlow) {
        if (showProgressBar || viewModel == null) {
            CircularProgressBarComponent()
        } else {
            val amount by viewModel.amount.observeAsState(Constants.EMPTY_STRING)
            val currency by viewModel.currency.observeAsState(Constants.SANDBOX_CURRENCY)
            val payeeMsisdn by viewModel.payeeMsisdn.observeAsState(Constants.EMPTY_STRING)
            val payerMessage by viewModel.payerMessage.observeAsState(Constants.EMPTY_STRING)
            val payeeNote by viewModel.payeeNote.observeAsState(Constants.EMPTY_STRING)
            val payerFirstName by viewModel.payerFirstName.observeAsState(Constants.EMPTY_STRING)
            val payerSurName by viewModel.payerSurName.observeAsState(Constants.EMPTY_STRING)
            val payerIdentificationType by viewModel.payerIdentificationType.observeAsState(Constants.EMPTY_STRING)
            val payerIdentificationNumber by viewModel.payerIdentificationNumber.observeAsState(Constants.EMPTY_STRING)
            val payerIdentity by viewModel.payerIdentity.observeAsState(Constants.EMPTY_STRING)
            val payerLanguageCode by viewModel.payerLanguageCode.observeAsState(Constants.EMPTY_STRING)
            val payerEmail by viewModel.payerEmail.observeAsState(Constants.EMPTY_STRING)
            val payerMsisdn by viewModel.payerMsisdn.observeAsState(Constants.EMPTY_STRING)
            val payerGender by viewModel.payerGender.observeAsState(Constants.EMPTY_STRING)
            val originatingCountry by viewModel.originatingCountry.observeAsState(Constants.EMPTY_STRING)
            val originalAmount by viewModel.originalAmount.observeAsState(Constants.EMPTY_STRING)
            val originalCurrency by viewModel.originalCurrency.observeAsState(Constants.EMPTY_STRING)
            val referenceId by viewModel.referenceId.observeAsState()
            val result by viewModel.result.observeAsState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MomoCard {
                        CardTitle(title = stringResource(id = R.string.cash_transfer_title))
                        Spacer(modifier = Modifier.height(8.dp))
                        LabeledField(stringResource(R.string.label_amount), amount, viewModel::onAmountChanged, keyboardType = KeyboardType.Number)
                        LabeledField(stringResource(R.string.label_currency), currency, viewModel::onCurrencyChanged)
                        LabeledField(stringResource(R.string.label_payee_msisdn), payeeMsisdn, viewModel::onPayeeMsisdnChanged, keyboardType = KeyboardType.Phone)
                        LabeledField(stringResource(R.string.label_message), payerMessage, viewModel::onPayerMessageChanged)
                        LabeledField(stringResource(R.string.label_note), payeeNote, viewModel::onPayeeNoteChanged)
                        LabeledField(stringResource(R.string.label_payer_first_name), payerFirstName, viewModel::onPayerFirstNameChanged)
                        LabeledField(stringResource(R.string.label_payer_surname), payerSurName, viewModel::onPayerSurNameChanged)
                        LabeledField(stringResource(R.string.label_payer_identification_type), payerIdentificationType, viewModel::onPayerIdentificationTypeChanged)
                        LabeledField(stringResource(R.string.label_payer_identification_number), payerIdentificationNumber, viewModel::onPayerIdentificationNumberChanged)
                        LabeledField(stringResource(R.string.label_payer_identity), payerIdentity, viewModel::onPayerIdentityChanged, keyboardType = KeyboardType.Phone)
                        LabeledField(stringResource(R.string.label_payer_language_code), payerLanguageCode, viewModel::onPayerLanguageCodeChanged)
                        LabeledField(stringResource(R.string.label_payer_email), payerEmail, viewModel::onPayerEmailChanged, keyboardType = KeyboardType.Email)
                        LabeledField(stringResource(R.string.label_payer_msisdn), payerMsisdn, viewModel::onPayerMsisdnChanged, keyboardType = KeyboardType.Phone)
                        LabeledField(stringResource(R.string.label_payer_gender), payerGender, viewModel::onPayerGenderChanged)
                        LabeledField(stringResource(R.string.label_originating_country), originatingCountry, viewModel::onOriginatingCountryChanged)
                        LabeledField(stringResource(R.string.label_original_amount), originalAmount, viewModel::onOriginalAmountChanged, keyboardType = KeyboardType.Number)
                        LabeledField(stringResource(R.string.label_original_currency), originalCurrency, viewModel::onOriginalCurrencyChanged)
                        Spacer(modifier = Modifier.height(12.dp))
                        OperationActionButton(
                            text = stringResource(R.string.action_send),
                            onClick = viewModel::sendCashTransfer,
                            enabled = amount.isNotBlank() && payeeMsisdn.isNotBlank()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OperationActionButton(
                            text = stringResource(R.string.action_check_status),
                            onClick = viewModel::checkStatus,
                            enabled = !referenceId.isNullOrBlank()
                        )
                    }
                }
                item { OperationConsole(result = result, hint = stringResource(R.string.console_hint)) }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun CashTransferScreenPreview() {
    CashTransferScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        showProgressBar = false,
        viewModel = null
    )
}
