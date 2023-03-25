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
package com.rekast.momoapi.sample.ui.components.general

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.rekast.momoapi.sample.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textFieldDefaultsComponent(): TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = colorResource(id = R.color.accent_primary),
        unfocusedBorderColor = colorResource(id = R.color.black),
        cursorColor = colorResource(id = R.color.accent_primary)
    )
}