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

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import io.rekast.sdk.sample.R

/**
 * Returns a [TextFieldColors] configuration for text fields throughout the sample application.
 *
 * - Focused border/indicator: `accent_primary`
 * - Unfocused border/indicator: `accent_secondary`
 * - Focused container background: `white`
 * - Unfocused container background: `whiteish`
 * - Cursor: `accent_primary`
 */
@Composable
fun textFieldDefaultsComponent(): TextFieldColors = TextFieldDefaults.colors(
    focusedIndicatorColor = colorResource(id = R.color.accent_primary),
    unfocusedIndicatorColor = colorResource(id = R.color.accent_secondary),
    focusedContainerColor = colorResource(id = R.color.white),
    unfocusedContainerColor = colorResource(id = R.color.whiteish),
    cursorColor = colorResource(id = R.color.accent_primary)
)
