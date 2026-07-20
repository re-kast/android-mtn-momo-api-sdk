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
package io.rekast.sdk.sample.views.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.ui.components.general.MomoScaffold
import io.rekast.sdk.sample.ui.components.general.StatusPill
import io.rekast.sdk.sample.ui.components.screens.ConfigCard
import io.rekast.sdk.sample.ui.components.screens.OperationActionButton
import io.rekast.sdk.sample.ui.theme.DangerColor
import io.rekast.sdk.sample.ui.theme.SuccessColor
import io.rekast.sdk.sample.ui.theme.subtleTextColor
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Renders the Setup & Config screen: the static SDK configuration, the live credential-provisioning
 * status (which the bootstrap flow normally fills in invisibly), and a button to re-run bootstrap.
 *
 * @param navController Used by the drawer; may be null in previews.
 * @param snackStateFlow Flow of snackbar messages.
 * @param viewModel Provides the config and credential status; null in previews.
 * @param onRerunSetup Invoked when the user taps "Re-run Setup".
 */
@Composable
fun SetupScreen(navController: NavController?, snackStateFlow: SharedFlow<SnackBarComponentConfiguration>, viewModel: SetupScreenViewModel?, onRerunSetup: () -> Unit = {}) {
    MomoScaffold(titleRes = R.string.setup_screen, navController = navController, snackStateFlow = snackStateFlow) {
        val status by (viewModel?.status?.observeAsState() ?: return@MomoScaffold)
        val config = viewModel.config

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ConfigCard(titleRes = R.string.setup_config_title, config = config)
            }
            item {
                MomoCard {
                    CardTitle(title = stringResource(R.string.setup_status_title))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.setup_status_hint),
                        style = MaterialTheme.typography.caption,
                        color = subtleTextColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StatusRow(stringResource(R.string.label_api_key), status?.apiKeyPresent == true)
                    StatusRow(stringResource(R.string.label_access_token), status?.accessTokenPresent == true)
                    StatusRow(stringResource(R.string.label_oauth_token), status?.oauthTokenPresent == true)
                    StatusRow(stringResource(R.string.label_auth_req_id), status?.authReqIdPresent == true)
                    StatusRow(stringResource(R.string.label_login_hint), status?.loginHintPresent == true)
                    Spacer(modifier = Modifier.height(14.dp))
                    OperationActionButton(text = stringResource(R.string.action_rerun_setup), onClick = onRerunSetup)
                }
            }
        }
    }
}

@Composable
private fun StatusRow(label: String, present: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.onSurface)
        StatusPill(
            text = stringResource(if (present) R.string.status_present else R.string.status_missing),
            color = if (present) SuccessColor else DangerColor
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SetupScreenPreview() {
    SetupScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        viewModel = null
    )
}
