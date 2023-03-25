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
        cursorColor = colorResource(id = R.color.accent_primary),
    )
}
