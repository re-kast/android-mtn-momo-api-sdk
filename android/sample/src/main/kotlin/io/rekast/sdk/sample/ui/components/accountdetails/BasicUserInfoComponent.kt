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
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.SectionHeader

/**
 * Rstarters a section displaying basic user information fields: name, birth date, gstarter,
 * updated-at date, locale, and sub identifier.
 *
 * @param modifier Modifier applied to the root [Column].
 * @param basicUserInfo LiveData holding the [BasicUserInfo] to display; individual fields are hidden when null.
 */
@Composable
fun BasicUserInfoComponent(modifier: Modifier = Modifier, basicUserInfo: MutableLiveData<BasicUserInfo?>) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SectionHeader(titleResId = R.string.basic_user_info_title)
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_large), end = dimensionResource(id = R.dimen.spacing_large))) {
                Text(
                    text = stringResource(id = R.string.name),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_medium), end = dimensionResource(id = R.dimen.spacing_medium))) {
                basicUserInfo.value?.name?.let { Text(text = it, color = colorResource(id = R.color.black)) }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_large), end = dimensionResource(id = R.dimen.spacing_large))) {
                Text(
                    text = stringResource(id = R.string.birth_date),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_medium), end = dimensionResource(id = R.dimen.spacing_medium))) {
                basicUserInfo.value?.birthDate?.let {
                    Text(
                        text = it,
                        color = colorResource(id = R.color.black)
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_large), end = dimensionResource(id = R.dimen.spacing_large))) {
                Text(
                    text = stringResource(id = R.string.gender),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_medium), end = dimensionResource(id = R.dimen.spacing_medium))) {
                basicUserInfo.value?.gender?.let { Text(text = it, color = colorResource(id = R.color.black)) }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_large), end = dimensionResource(id = R.dimen.spacing_large))) {
                Text(
                    text = stringResource(id = R.string.updated_at),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_medium), end = dimensionResource(id = R.dimen.spacing_medium))) {
                basicUserInfo.value?.displayUpdatedAt?.let {
                    Text(
                        text = it,
                        color = colorResource(id = R.color.black)
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_large), end = dimensionResource(id = R.dimen.spacing_large))) {
                Text(
                    text = stringResource(id = R.string.locale),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_medium), end = dimensionResource(id = R.dimen.spacing_medium))) {
                basicUserInfo.value?.locale?.let { Text(text = it, color = colorResource(id = R.color.black)) }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_large), end = dimensionResource(id = R.dimen.spacing_large))) {
                Text(
                    text = stringResource(id = R.string.sub),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(start = dimensionResource(id = R.dimen.spacing_medium), end = dimensionResource(id = R.dimen.spacing_medium))) {
                basicUserInfo.value?.sub?.let { Text(text = it, color = colorResource(id = R.color.black)) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasicUserInfoComponentPreview() {
    BasicUserInfoComponent(
        basicUserInfo = MutableLiveData(null)
    )
}
