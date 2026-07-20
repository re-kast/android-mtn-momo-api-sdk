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
package io.rekast.sdk.sample.ui.components.accountdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.ui.components.general.StatusPill
import io.rekast.sdk.sample.ui.theme.DangerColor
import io.rekast.sdk.sample.ui.theme.SuccessColor
import io.rekast.sdk.sample.ui.theme.subtleTextColor

/**
 * Renders a card showing the active/inactive status of an account holder as a colored pill.
 *
 * @param modifier Modifier applied to the card.
 * @param accountHolderStatus LiveData holding the [AccountHolderStatus]; shows "In Active" when null.
 */
@Composable
fun AccountStatusComponent(modifier: Modifier = Modifier, accountHolderStatus: MutableLiveData<AccountHolderStatus?>) {
    val status by accountHolderStatus.observeAsState()
    val isActive = status?.result == true
    MomoCard(modifier = modifier) {
        CardTitle(title = stringResource(id = R.string.card_account_status))
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.label_status),
                style = MaterialTheme.typography.body2,
                color = subtleTextColor
            )
            StatusPill(
                text = stringResource(id = if (isActive) R.string.active else R.string.in_Active),
                color = if (isActive) SuccessColor else DangerColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountStatusComponentPreview() {
    AccountStatusComponent(
        accountHolderStatus = MutableLiveData(AccountHolderStatus(result = true))
    )
}
