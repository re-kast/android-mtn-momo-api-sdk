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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import io.rekast.sdk.sample.ui.navigation.drawer.Drawer
import io.rekast.sdk.sample.ui.navigation.topbar.TopBar
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import io.rekast.sdk.sample.utils.hookSnackBar
import kotlinx.coroutines.flow.SharedFlow

/**
 * Shared screen shell for the sample app. Wraps a themed [Scaffold] with the app [TopBar],
 * navigation [Drawer], and snackbar host so every screen shares one consistent, dark-mode-aware
 * chrome. Surfaces and the drawer follow [MaterialTheme] (MTN blue in light, deep neutral in dark).
 *
 * @param titleRes String resource for the top bar title.
 * @param navController [NavController] used by the drawer; when null the drawer is omitted (previews).
 * @param snackStateFlow Flow of snackbar messages to display.
 * @param content Screen body; receives the scaffold content padding to apply to its root.
 */
@Composable
fun MomoScaffold(@StringRes titleRes: Int, navController: NavController?, snackStateFlow: SharedFlow<SnackBarComponentConfiguration>, content: @Composable (PaddingValues) -> Unit) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    var snackBarType by remember { mutableStateOf(SnackBarType.INFO) }

    LaunchedEffect(Unit) {
        snackStateFlow.hookSnackBar(scaffoldState, onDisplay = { snackBarType = it.type })
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scope = scope, scaffoldState = scaffoldState, title = titleRes) },
        drawerBackgroundColor = MaterialTheme.colors.primarySurface,
        drawerContentColor = MaterialTheme.colors.onPrimary,
        drawerContent = {
            navController?.let { Drawer(scope = scope, scaffoldState = scaffoldState, navController = it) }
        },
        drawerGesturesEnabled = true,
        backgroundColor = MaterialTheme.colors.background,
        snackbarHost = { snackBarHostState ->
            SnackBarComponent(snackBarHostState = snackBarHostState, type = snackBarType)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            content(padding)
        }
    }
}
