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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.InfoRow
import io.rekast.sdk.sample.ui.components.general.MomoCard

/**
 * Renders a card displaying the basic user information fields: name, birth date, gender,
 * updated-at date, locale, and sub identifier. Missing fields are omitted automatically.
 *
 * @param modifier Modifier applied to the card.
 * @param basicUserInfo LiveData holding the [BasicUserInfo] to display.
 */
@Composable
fun BasicUserInfoComponent(modifier: Modifier = Modifier, basicUserInfo: MutableLiveData<BasicUserInfo?>) {
    val info by basicUserInfo.observeAsState()
    MomoCard(modifier = modifier) {
        CardTitle(title = stringResource(id = R.string.card_basic_user_info))
        Spacer(modifier = Modifier.height(12.dp))
        InfoRow(label = stringResource(id = R.string.label_name), value = info?.name)
        InfoRow(label = stringResource(id = R.string.label_birth_date), value = info?.birthDate)
        InfoRow(label = stringResource(id = R.string.label_gender), value = info?.gender)
        InfoRow(label = stringResource(id = R.string.label_updated_at), value = info?.displayUpdatedAt)
        InfoRow(label = stringResource(id = R.string.label_locale), value = info?.locale)
        InfoRow(label = stringResource(id = R.string.label_sub), value = info?.sub)
    }
}

@Preview(showBackground = true)
@Composable
fun BasicUserInfoComponentPreview() {
    BasicUserInfoComponent(
        basicUserInfo = MutableLiveData(
            BasicUserInfo(
                sub = "0",
                name = "Sand Box",
                givenName = "Sand",
                familyName = "Box",
                birthDate = "1976-08-13",
                locale = "sv_SE",
                gender = "MALE",
                updatedAt = 1784414651
            ).apply { displayUpdatedAt = "2026-07-18" }
        )
    )
}
