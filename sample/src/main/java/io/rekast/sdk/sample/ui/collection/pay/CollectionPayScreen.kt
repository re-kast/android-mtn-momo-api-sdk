/*
 *              Apache License
 *        Version 2.0, January 2004
 *     http://www.apache.org/licenses/
 *
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package io.rekast.sdk.sample.ui.collection.pay

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
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import io.rekast.sdk.model.api.MomoTransaction
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
import io.rekast.sdk.sample.utils.annotation.PreviewWithBackgroundExcludeGenerated
import io.rekast.sdk.sample.utils.hookSnackBar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Composable
fun CollectionScreen(
    navController: NavController?,
    snackStateFlow: SharedFlow<SnackBarComponentConfiguration>,
    showProgressBar: Boolean = false,
    collectionPayScreenViewModel: CollectionPayScreenViewModel?,
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
        topBar = { TopBar(scope = scope, scaffoldState = scaffoldState, title = R.string.collections_pay_screen) },
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
                collectionPayScreenViewModel?.let {
                    val phoneNumber by collectionPayScreenViewModel.phoneNumber.observeAsState(Constants.EMPTY_STRING)
                    val financialId by collectionPayScreenViewModel.financialId.observeAsState(Constants.EMPTY_STRING)
                    val amount by collectionPayScreenViewModel.amount.observeAsState(Constants.EMPTY_STRING)
                    val paymentMessage by collectionPayScreenViewModel.paymentMessage.observeAsState(Constants.EMPTY_STRING)
                    val paymentNote by collectionPayScreenViewModel.paymentNote.observeAsState(Constants.EMPTY_STRING)
                    val deliveryNote by collectionPayScreenViewModel.deliveryNote.observeAsState(Constants.EMPTY_STRING)
                    val referenceIdToRefund by collectionPayScreenViewModel.referenceIdToRefund.observeAsState(Constants.EMPTY_STRING)

                    if (momoTransaction.value == null) {
                        PaymentDataScreenComponent(
                            title = stringResource(id = R.string.request_to_pay_title),
                            submitButtonText = stringResource(id = R.string.request_payment_submit_button),
                            phoneNumber = phoneNumber,
                            financialId = financialId,
                            referenceIdToRefund = referenceIdToRefund,
                            showReferenceIdToRefund = false,
                            amount = amount,
                            paymentMessage = paymentMessage,
                            paymentNote = paymentNote,
                            deliveryNote = deliveryNote,
                            onRequestPayButtonClicked = { collectionPayScreenViewModel.requestToPay() },
                            onPhoneNumberUpdated = { collectionPayScreenViewModel.onPhoneNumberUpdated(it) },
                            onFinancialIdUpdated = { collectionPayScreenViewModel.onFinancialIdUpdated(it) },
                            onReferenceIdToRefundUpdated = { collectionPayScreenViewModel.onReferenceIdToRefundUpdated(it) },
                            onAmountUpdated = { collectionPayScreenViewModel.onAmountUpdated(it) },
                            onPayerMessageUpdated = { collectionPayScreenViewModel.onPayerMessageUpdated(it) },
                            onPayerNoteUpdated = { collectionPayScreenViewModel.onPayerNoteUpdated(it) },
                            onDeliveryNoteUpdated = { collectionPayScreenViewModel.onDeliveryNoteUpdated(it) }
                        )
                    } else {
                        PaymentDataDisplayComponent(
                            title = stringResource(id = R.string.request_to_pay_title),
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

@PreviewWithBackgroundExcludeGenerated
@Composable
fun CollectionScreenPreview() {
    CollectionScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        showProgressBar = false,
        collectionPayScreenViewModel = null,
        momoTransaction = MutableLiveData(null)
    )
}
