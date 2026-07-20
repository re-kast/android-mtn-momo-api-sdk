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
package io.rekast.sdk.sample.views.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.InfoRow
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.ui.components.general.MomoScaffold
import io.rekast.sdk.sample.ui.components.screens.ConfigCard
import io.rekast.sdk.sample.ui.theme.DangerColor
import io.rekast.sdk.sample.ui.theme.subtleTextColor
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Renders the Settings screen: the target environment, the product subscription keys (masked, with a
 * Show/Hide toggle), the host app's build metadata, and a destructive "clear stored credentials"
 * action.
 *
 * @param navController Used by the drawer; may be null in previews.
 * @param snackStateFlow Flow of snackbar messages.
 * @param viewModel Provides the config and app info; null in previews.
 * @param onClearCredentials Invoked when the user taps "Clear stored credentials".
 */
@Composable
fun SettingsScreen(navController: NavController?, snackStateFlow: SharedFlow<SnackBarComponentConfiguration>, viewModel: SettingsScreenViewModel?, onClearCredentials: () -> Unit = {}) {
    MomoScaffold(titleRes = R.string.settings_screen, navController = navController, snackStateFlow = snackStateFlow) {
        val config = viewModel?.config ?: return@MomoScaffold
        val appInfo = viewModel.appInfo
        var revealKeys by remember { mutableStateOf(false) }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ConfigCard(titleRes = R.string.settings_environment_title, config = config)
            }
            item {
                MomoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CardTitle(title = stringResource(R.string.settings_keys_title), modifier = Modifier.weight(1f))
                        TextButton(onClick = { revealKeys = !revealKeys }) {
                            Text(
                                text = stringResource(if (revealKeys) R.string.action_hide else R.string.action_show),
                                color = MaterialTheme.colors.secondary
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.settings_keys_hint),
                        style = MaterialTheme.typography.caption,
                        color = subtleTextColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    KeyRow(stringResource(R.string.label_collection_primary_key), config.collectionPrimaryKey, revealKeys)
                    KeyRow(stringResource(R.string.label_collection_secondary_key), config.collectionSecondaryKey, revealKeys)
                    KeyRow(stringResource(R.string.label_remittance_primary_key), config.remittancePrimaryKey, revealKeys)
                    KeyRow(stringResource(R.string.label_remittance_secondary_key), config.remittanceSecondaryKey, revealKeys)
                    KeyRow(stringResource(R.string.label_disbursement_primary_key), config.disbursementsPrimaryKey, revealKeys)
                    KeyRow(stringResource(R.string.label_disbursement_secondary_key), config.disbursementsSecondaryKey, revealKeys)
                }
            }
            item {
                MomoCard {
                    CardTitle(title = stringResource(R.string.settings_app_title))
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoRow(stringResource(R.string.label_app_version), appInfo.versionName)
                    InfoRow(stringResource(R.string.label_app_build), appInfo.versionCode)
                    InfoRow(stringResource(R.string.label_package_name), appInfo.packageName)
                }
            }
            item {
                MomoCard {
                    CardTitle(title = stringResource(R.string.settings_danger_title))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.settings_danger_hint),
                        style = MaterialTheme.typography.caption,
                        color = subtleTextColor
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onClearCredentials,
                        colors = ButtonDefaults.buttonColors(backgroundColor = DangerColor, contentColor = Color.White),
                        elevation = null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.action_clear_credentials), modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * An [InfoRow] whose value is a subscription key: fully shown when [reveal] is true, otherwise masked
 * to its last four characters. Blank keys render as the localized "Not set" placeholder.
 */
@Composable
private fun KeyRow(label: String, value: String, reveal: Boolean) {
    val display = when {
        value.isBlank() -> stringResource(R.string.value_not_set)
        reveal -> value
        else -> "•".repeat(8) + value.takeLast(4)
    }
    InfoRow(label, display)
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        viewModel = null
    )
}
