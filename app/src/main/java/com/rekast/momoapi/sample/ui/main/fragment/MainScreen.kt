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
package com.rekast.momoapi.sample.ui.main.fragment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.rekast.momoapi.sample.R
import com.rekast.momoapi.sample.ui.navigation.drawer.Drawer
import com.rekast.momoapi.sample.ui.navigation.navigation.Navigation
import com.rekast.momoapi.sample.ui.navigation.topbar.TopBar

@Composable
fun MainScreen() {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scope = scope, scaffoldState = scaffoldState) },
        drawerBackgroundColor = colorResource(id = R.color.accent_secondary),
        drawerContent = {
            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
        },
        backgroundColor = colorResource(id = R.color.white),
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Navigation(navController = navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
