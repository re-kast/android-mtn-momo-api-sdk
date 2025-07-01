package io.rekast.sdk.sample.ui.components.general

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import io.rekast.sdk.sample.R

@Composable
fun SectionHeader(
    @StringRes titleResId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier // Apply the passed modifier here
            .fillMaxWidth() // Ensure it still fills width by default if not overridden
            .padding(end = dimensionResource(id = R.dimen.spacing_large)) // Original end padding
    ) {
        Text(
            text = stringResource(id = titleResId),
            style = TextStyle(
                fontSize = dimensionResource(id = R.dimen.font_size_medium)
            ),
            color = colorResource(id = R.color.black),
            fontWeight = FontWeight.Bold
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth() // Ensure divider spans the width of its column
                .padding(
                    top = dimensionResource(id = R.dimen.spacing_medium),
                    bottom = dimensionResource(id = R.dimen.spacing_medium)
                )
        )
    }
}
