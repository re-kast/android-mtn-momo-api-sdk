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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import io.rekast.sdk.sample.ui.theme.InfoColor
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Renders the Collection Payment (V2) screen: a create form plus a check-status action, with a
 * console panel showing each operation's outcome.
 *
 * @param navController Used by the drawer; may be null in previews.
 * @param snackStateFlow Flow of snackbar messages.
 * @param showProgressBar Whether to show the loading indicator.
 * @param viewModel Provides form state and the create/status actions; null in previews.
 */
@Composable
fun PaymentScreen(navController: NavController?, snackStateFlow: SharedFlow<SnackBarComponentConfiguration>, showProgressBar: Boolean = false, viewModel: PaymentScreenViewModel?) {
    MomoScaffold(titleRes = R.string.payment_screen, navController = navController, snackStateFlow = snackStateFlow) {
        if (showProgressBar || viewModel == null) {
            CircularProgressBarComponent()
        } else {
            val amount by viewModel.amount.observeAsState(Constants.EMPTY_STRING)
            val currency by viewModel.currency.observeAsState(Constants.SANDBOX_CURRENCY)
            val customerReference by viewModel.customerReference.observeAsState(Constants.EMPTY_STRING)
            val receiverMessage by viewModel.receiverMessage.observeAsState(Constants.EMPTY_STRING)
            val senderNote by viewModel.senderNote.observeAsState(Constants.EMPTY_STRING)
            val referenceId by viewModel.referenceId.observeAsState()
            val result by viewModel.result.observeAsState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MomoCard {
                        CardTitle(title = stringResource(id = R.string.payment_title))
                        Spacer(modifier = Modifier.height(8.dp))
                        LabeledField(stringResource(R.string.label_amount), amount, viewModel::onAmountChanged, keyboardType = KeyboardType.Number)
                        LabeledField(stringResource(R.string.label_currency), currency, viewModel::onCurrencyChanged)
                        LabeledField(stringResource(R.string.label_customer_reference), customerReference, viewModel::onCustomerReferenceChanged, keyboardType = KeyboardType.Phone)
                        LabeledField(stringResource(R.string.label_receiver_message), receiverMessage, viewModel::onReceiverMessageChanged)
                        LabeledField(stringResource(R.string.label_sender_note), senderNote, viewModel::onSenderNoteChanged)
                        Spacer(modifier = Modifier.height(12.dp))
                        OperationActionButton(
                            text = stringResource(R.string.action_create),
                            onClick = viewModel::createPayment,
                            enabled = amount.isNotBlank()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OperationActionButton(
                            text = stringResource(R.string.action_check_status),
                            onClick = viewModel::checkStatus,
                            enabled = !referenceId.isNullOrBlank(),
                            backgroundColor = InfoColor,
                            contentColor = Color.White
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
fun PaymentScreenPreview() {
    PaymentScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        showProgressBar = false,
        viewModel = null
    )
}
