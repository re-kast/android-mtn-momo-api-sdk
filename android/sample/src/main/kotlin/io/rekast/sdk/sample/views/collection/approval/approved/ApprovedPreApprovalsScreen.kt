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
package io.rekast.sdk.sample.views.collection.approval.approved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import io.rekast.sdk.sample.ui.components.general.InfoRow
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.ui.components.general.MomoScaffold
import io.rekast.sdk.sample.ui.components.screens.LabeledField
import io.rekast.sdk.sample.ui.components.screens.OperationActionButton
import io.rekast.sdk.sample.ui.components.screens.OperationConsole
import io.rekast.sdk.sample.ui.theme.DangerColor
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Renders the Collection Approved Pre-Approvals screen: an account-holder input plus a fetch action,
 * then one card per approved pre-approval — each with its own Cancel button keyed on that record's
 * `preApprovalId`.
 *
 * @param navController Used by the drawer; may be null in previews.
 * @param snackStateFlow Flow of snackbar messages.
 * @param showProgressBar Whether to show the loading indicator.
 * @param viewModel Provides form state and the fetch/cancel actions; null in previews.
 */
@Composable
fun ApprovedPreApprovalsScreen(navController: NavController?, snackStateFlow: SharedFlow<SnackBarComponentConfiguration>, showProgressBar: Boolean = false, viewModel: ApprovedPreApprovalsScreenViewModel?) {
    MomoScaffold(titleRes = R.string.approved_pre_approvals_screen, navController = navController, snackStateFlow = snackStateFlow) {
        if (showProgressBar || viewModel == null) {
            CircularProgressBarComponent()
        } else {
            val accountHolderId by viewModel.accountHolderId.observeAsState(Constants.EMPTY_STRING)
            val approvals by viewModel.approvals.observeAsState(emptyList())
            val result by viewModel.result.observeAsState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MomoCard {
                        CardTitle(title = stringResource(id = R.string.approved_pre_approvals_title))
                        Spacer(modifier = Modifier.height(8.dp))
                        LabeledField(stringResource(R.string.label_payer_msisdn), accountHolderId, viewModel::onAccountHolderIdChanged, keyboardType = KeyboardType.Phone)
                        Spacer(modifier = Modifier.height(12.dp))
                        OperationActionButton(
                            text = stringResource(R.string.action_get_approved),
                            onClick = viewModel::getApprovedPreApprovals,
                            enabled = accountHolderId.isNotBlank()
                        )
                    }
                }
                items(approvals) { approval ->
                    MomoCard {
                        CardTitle(title = approval.preApprovalId)
                        Spacer(modifier = Modifier.height(4.dp))
                        InfoRow(label = stringResource(R.string.label_status), value = approval.status.name)
                        InfoRow(label = stringResource(R.string.label_currency), value = approval.fromCurrency)
                        InfoRow(label = stringResource(R.string.label_message), value = approval.message)
                        Spacer(modifier = Modifier.height(12.dp))
                        OperationActionButton(
                            text = stringResource(R.string.action_cancel),
                            onClick = { viewModel.cancelPreApproval(approval.preApprovalId) },
                            backgroundColor = DangerColor,
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
fun ApprovedPreApprovalsScreenPreview() {
    ApprovedPreApprovalsScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        showProgressBar = false,
        viewModel = null
    )
}
