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
package io.rekast.sdk.sample.ui.navigation.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.navigation.navigation.NavigationDrawerItem

@Composable
fun DrawerItem(item: NavigationDrawerItem, selected: Boolean, onItemClick: (NavigationDrawerItem) -> Unit) {
    val background = if (selected) R.color.accent_secondary else android.R.color.transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(item) })
            .height(dimensionResource(id = R.dimen.list_item_height_default))
            .background(colorResource(id = background))
            .padding(start = dimensionResource(id = R.dimen.spacing_medium), end = dimensionResource(id = R.dimen.spacing_medium))
    ) {
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_extra_small)))
        Row {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                colorFilter = ColorFilter.tint(Color.White),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.icon_size_default))
                    .width(dimensionResource(id = R.dimen.icon_size_default))
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_extra_small)))
            Text(
                text = item.title,
                fontSize = dimensionResource(id = R.dimen.font_size_medium),
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_extra_small)))
    }
}

@Preview(showBackground = false)
@Composable
fun DrawerItemPreview() {
    DrawerItem(item = NavigationDrawerItem.Home, selected = false, onItemClick = {})
}
