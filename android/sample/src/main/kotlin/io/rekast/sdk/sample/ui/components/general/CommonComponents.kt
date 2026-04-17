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
package io.rekast.sdk.sample.ui.components.general

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import io.rekast.sdk.sample.R

/**
 * Renders a bold section title followed by a full-width horizontal divider.
 *
 * @param titleResId String resource ID for the section title text.
 * @param modifier Modifier applied to the wrapping [Column].
 */
@Composable
fun SectionHeader(@StringRes titleResId: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = dimensionResource(id = R.dimen.spacing_large), top = dimensionResource(id = R.dimen.spacing_small), end = dimensionResource(id = R.dimen.spacing_large))
    ) {
        Text(
            text = stringResource(id = titleResId),
            style = TextStyle(
                fontSize = with(LocalDensity.current) { dimensionResource(id = R.dimen.font_size_medium).toSp() }
            ),
            color = colorResource(id = R.color.black),
            fontWeight = FontWeight.Bold
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.spacing_medium),
                    bottom = dimensionResource(id = R.dimen.spacing_medium)
                )
        )
    }
}
