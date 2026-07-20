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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.InfoRow
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.utils.SampleConfig

/**
 * A card that displays the SDK's static environment configuration — the target environment, provider
 * callback host, API versions, and API user ID. Shared by the Setup and Settings screens so the two
 * render the same set of fields consistently.
 *
 * @param titleRes The card title string resource (e.g. "Configuration" or "Environment").
 * @param config The static app configuration to display.
 * @param modifier Modifier applied to the card.
 */
@Composable
fun ConfigCard(@StringRes titleRes: Int, config: SampleConfig, modifier: Modifier = Modifier) {
    MomoCard(modifier = modifier) {
        CardTitle(title = stringResource(titleRes))
        Spacer(modifier = Modifier.height(10.dp))
        InfoRow(stringResource(R.string.label_environment), config.environment)
        InfoRow(stringResource(R.string.label_callback_host), config.providerCallbackHost)
        InfoRow(stringResource(R.string.label_api_version_v1), config.apiVersionV1)
        InfoRow(stringResource(R.string.label_api_version_v2), config.apiVersionV2)
        InfoRow(stringResource(R.string.label_api_user_id), config.apiUserId)
    }
}
