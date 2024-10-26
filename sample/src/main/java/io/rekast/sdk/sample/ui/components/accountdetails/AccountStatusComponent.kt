/*
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
package io.rekast.sdk.sample.ui.components.accountdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.api.AccountHolderStatus
import io.rekast.sdk.sample.R

@Composable
fun AccountStatusComponent(
    modifier: Modifier = Modifier,
    accountHolderStatus: MutableLiveData<AccountHolderStatus?>
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = modifier.padding(end = 20.dp)) {
            Text(
                text = stringResource(id = R.string.account_status_title),
                style = TextStyle(
                    fontSize = 18.sp
                ),
                color = colorResource(id = R.color.black),
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = modifier.padding(top = 10.dp, bottom = 10.dp))
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(id = R.string.status),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                accountHolderStatus.value?.result?.let { result ->
                    var text = stringResource(id = R.string.in_Active)
                    if (result) {
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
