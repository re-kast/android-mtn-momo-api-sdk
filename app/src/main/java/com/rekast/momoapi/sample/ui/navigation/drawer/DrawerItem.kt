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
package com.rekast.momoapi.sample.ui.navigation.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rekast.momoapi.sample.R
import com.rekast.momoapi.sample.ui.navigation.navigation.NavigationDrawerItem

@Composable
fun DrawerItem(item: NavigationDrawerItem, selected: Boolean, onItemClick: (NavigationDrawerItem) -> Unit) {
    val background = if (selected) R.color.accent_secondary else android.R.color.transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(item) })
            .height(60.dp)
            .background(colorResource(id = background))
            .padding(start = 10.dp, end = 10.dp)
    ) {
        Spacer(modifier = Modifier.width(7.dp))
        Column() {
            Text(
                text = item.title,
                fontSize = 18.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
    }
}

@Preview(showBackground = false)
@Composable
fun DrawerItemPreview() {
    DrawerItem(item = NavigationDrawerItem.Home, selected = false, onItemClick = {})
}
