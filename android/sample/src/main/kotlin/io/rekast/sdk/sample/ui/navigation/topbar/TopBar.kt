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
package io.rekast.sdk.sample.ui.navigation.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Renders the application top app bar with a hamburger menu icon that opens the navigation drawer.
 *
 * The entire navigation icon area (full TopAppBar height, square aspect ratio) is tappable — not
 * just the 24 dp icon itself — so the drawer is easy to open with a thumb or imprecise tap.
 *
 * @param scope [CoroutineScope] used to launch the drawer open animation.
 * @param scaffoldState [ScaffoldState] providing access to the drawer state.
 * @param title String resource ID for the screen title displayed in the top bar.
 */
@Composable
fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState, title: Int) {
    TopAppBar(
        title = { Text(text = stringResource(title), fontSize = 20.sp) },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clickable { scope.launch { scaffoldState.drawerState.open() } },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Menu, contentDescription = Constants.EMPTY_STRING)
            }
        },
        backgroundColor = colorResource(id = R.color.accent_secondary),
        contentColor = Color.White
    )
}

@Preview(showBackground = false)
@Composable
fun TopBarPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    TopBar(scope = scope, scaffoldState = scaffoldState, title = R.string.home_screen)
}
