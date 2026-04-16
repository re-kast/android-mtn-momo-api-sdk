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
package io.rekast.sdk.sample.ui.components.accountdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.SectionHeader

/**
 * Renders a section displaying the active/inactive status of an account holder.
 *
 * @param modifier Modifier applied to the root [Column].
 * @param accountHolderStatus LiveData holding the [AccountHolderStatus] to display; shows "Inactive" when null.
 */
@Composable
fun AccountStatusComponent(modifier: Modifier = Modifier, accountHolderStatus: MutableLiveData<AccountHolderStatus?>) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SectionHeader(titleResId = R.string.account_status_title)
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = dimensionResource(id = R.dimen.spacing_large))) {
                Text(
                    text = stringResource(id = R.string.status),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = dimensionResource(id = R.dimen.spacing_medium))) {
                accountHolderStatus.value?.result.let { result ->
                    var text = stringResource(id = R.string.in_Active)
                    if (result == true) {
                        text = stringResource(id = R.string.active)
                    }
                    Text(
                        text = text,
                        color = colorResource(
                            id = R.color.black
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountStatusComponentPreview() {
    AccountStatusComponent(
        accountHolderStatus = MutableLiveData(null)
    )
}
