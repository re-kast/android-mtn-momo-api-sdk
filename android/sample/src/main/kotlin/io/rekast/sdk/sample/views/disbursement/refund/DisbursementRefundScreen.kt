/*
 * Copyright 2023-2024, Benjamin Mwalimu
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CircularProgressBarComponent
import io.rekast.sdk.sample.ui.components.general.SnackBarComponent
import io.rekast.sdk.sample.ui.components.screens.PaymentDataDisplayComponent
import io.rekast.sdk.sample.ui.components.screens.PaymentDataScreenComponent
import io.rekast.sdk.sample.ui.navigation.drawer.Drawer
import io.rekast.sdk.sample.ui.navigation.topbar.TopBar
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarThemeOptions
import io.rekast.sdk.sample.utils.hookSnackBar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Composable
fun DisbursementScreen(
    navController: NavController?,
    snackStateFlow: SharedFlow<SnackBarComponentConfiguration>,
    showProgressBar: Boolean = false,
    disbursementRefundScreenViewModel: DisbursementRefundScreenViewModel?,
    momoTransaction: MutableLiveData<MomoTransaction?>
) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    val snackBarTheme = SnackBarThemeOptions()

    LaunchedEffect(Unit) {
        snackStateFlow.hookSnackBar(scaffoldState)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scope = scope, scaffoldState = scaffoldState, title = R.string.disbursement_Refund_screen) },
        drawerBackgroundColor = colorResource(id = R.color.accent_secondary),
        drawerContent = {
            navController?.let { Drawer(scope = scope, scaffoldState = scaffoldState, navController = it) }
        },
        drawerGesturesEnabled = true,
        backgroundColor = colorResource(id = R.color.white),
        snackbarHost = { snackBarHostState ->
            SnackBarComponent(
                snackBarHostState = snackBarHostState,
                backgroundColorHex = snackBarTheme.backgroundColor,
                actionColorHex = snackBarTheme.actionTextColor,
                contentColorHex = snackBarTheme.messageTextColor
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (!showProgressBar) {
                disbursementRefundScreenViewModel?.let {
                    val phoneNumber by disbursementRefundScreenViewModel.phoneNumber.observeAsState(Constants.EMPTY_STRING)
                    val financialId by disbursementRefundScreenViewModel.financialId.observeAsState(Constants.EMPTY_STRING)
                    val amount by disbursementRefundScreenViewModel.amount.observeAsState(Constants.EMPTY_STRING)
                    val paymentMessage by disbursementRefundScreenViewModel.paymentMessage.observeAsState(Constants.EMPTY_STRING)
                    val paymentNote by disbursementRefundScreenViewModel.paymentNote.observeAsState(Constants.EMPTY_STRING)
                    val deliveryNote by disbursementRefundScreenViewModel.deliveryNote.observeAsState(Constants.EMPTY_STRING)
                    val referenceIdToRefund by disbursementRefundScreenViewModel.referenceIdToRefund.observeAsState(Constants.EMPTY_STRING)

                    if (momoTransaction.value == null) {
                        PaymentDataScreenComponent(
                            title = stringResource(id = R.string.request_to_refund_title),
                            submitButtonText = stringResource(id = R.string.send_refund_submit_button),
                            phoneNumber = phoneNumber,
                            financialId = financialId,
                            showFinancialId = false,
                            referenceIdToRefund = referenceIdToRefund,
                            amount = amount,
                            paymentMessage = paymentMessage,
                            paymentNote = paymentNote,
                            deliveryNote = deliveryNote,
                            showDeliveryTextField = false,
                            onRequestPayButtonClicked = { /*disbursementRefundScreenViewModel.refund()*/ },
                            onPhoneNumberUpdated = { disbursementRefundScreenViewModel.onPhoneNumberUpdated(it) },
                            onFinancialIdUpdated = { disbursementRefundScreenViewModel.onFinancialIdUpdated(it) },
                            onReferenceIdToRefundUpdated = { disbursementRefundScreenViewModel.onReferenceIdToRefundUpdated(it) },
                            onAmountUpdated = { disbursementRefundScreenViewModel.onAmountUpdated(it) },
                            onPayerMessageUpdated = { disbursementRefundScreenViewModel.onPayerMessageUpdated(it) },
                            onPayerNoteUpdated = { disbursementRefundScreenViewModel.onPayerNoteUpdated(it) },
                            onDeliveryNoteUpdated = { disbursementRefundScreenViewModel.onDeliveryNoteUpdated(it) }
                        )
                    } else {
                        PaymentDataDisplayComponent(
                            title = stringResource(id = R.string.request_to_refund_title),
                            momoTransaction = momoTransaction
                        )
                    }
                }
            } else {
                CircularProgressBarComponent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisbursementScreenPreview() {
    DisbursementScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        showProgressBar = false,
        disbursementRefundScreenViewModel = null,
        momoTransaction = MutableLiveData(null)
    )
}
