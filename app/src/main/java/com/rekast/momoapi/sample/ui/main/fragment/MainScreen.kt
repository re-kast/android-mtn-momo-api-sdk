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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.rekast.momoapi.model.api.BasicUserInfo
import com.rekast.momoapi.sample.R
import com.rekast.momoapi.sample.ui.components.SnackBarComponent
import com.rekast.momoapi.sample.ui.navigation.drawer.Drawer
import com.rekast.momoapi.sample.ui.navigation.topbar.TopBar
import com.rekast.momoapi.sample.utils.SnackBarComponentConfiguration
import com.rekast.momoapi.sample.utils.SnackBarThemeOptions
import com.rekast.momoapi.sample.utils.hookSnackBar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController?,
    snackStateFlow: SharedFlow<SnackBarComponentConfiguration>,
    showProgressBar: Boolean = false,
    basicUserInfo: MutableLiveData<BasicUserInfo?>
) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    val snackBarTheme = SnackBarThemeOptions()

    LaunchedEffect(Unit) {
        snackStateFlow.hookSnackBar(scaffoldState)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scope = scope, scaffoldState = scaffoldState, title = R.string.home_screen) },
        drawerBackgroundColor = colorResource(id = R.color.accent_secondary),
        drawerContent = {
            navController?.let { Drawer(scope = scope, scaffoldState = scaffoldState, navController = it) }
        },
        drawerGesturesEnabled = true,
        backgroundColor = colorResource(id = R.color.white),
        snackbarHost = { snackBarHostState ->
            SnackBarComponent(
                snackBarHostState = snackBarHostState,
                backgroundColorHex = snackBarTheme.backgroundColor,
                actionColorHex = snackBarTheme.actionTextColor,
                contentColorHex = snackBarTheme.messageTextColor
            )
        }
    ) { padding ->
        Box(modifier = modifier.padding(20.dp)) {
            if (!showProgressBar) {
                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    Column(modifier = modifier.padding(end = 20.dp)) {
                        Text(
                            text = "Basic User Information",
                            color = colorResource(id = R.color.black),
                            fontWeight = FontWeight.Bold
                        )
                        Divider(modifier = modifier.padding(top = 10.dp, bottom = 10.dp))
                    }
                    Row {
                        Column(modifier = modifier.padding(end = 20.dp)) {
                            Text(
                                text = "Name",
                                color = colorResource(id = R.color.black),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = modifier.padding(end = 10.dp)) {
                            basicUserInfo.value?.name?.let { Text(text = it, color = colorResource(id = R.color.black)) }
                        }
                    }
                    Row {
                        Column(modifier = modifier.padding(end = 20.dp)) {
                            Text(
                                text = "BirthDate",
                                color = colorResource(id = R.color.black),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = modifier.padding(end = 10.dp)) {
                            basicUserInfo.value?.birthDate?.let {
                                Text(
                                    text = it,
                                    color = colorResource(id = R.color.black)
                                )
                            }
                        }
                    }
                    Row {
                        Column(modifier = modifier.padding(end = 20.dp)) {
                            Text(
                                text = "Gender",
                                color = colorResource(id = R.color.black),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = modifier.padding(end = 10.dp)) {
                            basicUserInfo.value?.gender?.let { Text(text = it, color = colorResource(id = R.color.black)) }
                        }
                    }
                    Row {
                        Column(modifier = modifier.padding(end = 20.dp)) {
                            Text(
                                text = "Updated At",
                                color = colorResource(id = R.color.black),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = modifier.padding(end = 10.dp)) {
                            basicUserInfo.value?.updatedAt?.let {
                                Text(
                                    text = it,
                                    color = colorResource(id = R.color.black)
                                )
                            }
                        }
                    }
                    Row {
                        Column(modifier = modifier.padding(end = 20.dp)) {
                            Text(
                                text = "Locale",
                                color = colorResource(id = R.color.black),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = modifier.padding(end = 10.dp)) {
                            basicUserInfo.value?.locale?.let { Text(text = it, color = colorResource(id = R.color.black)) }
                        }
                    }
                    Row {
                        Column(modifier = modifier.padding(end = 20.dp)) {
                            Text(
                                text = "Sub",
                                color = colorResource(id = R.color.black),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = modifier.padding(end = 10.dp)) {
                            basicUserInfo.value?.sub?.let { Text(text = it, color = colorResource(id = R.color.black)) }
                        }
                    }
                }
            } else {
                Column(
                    modifier = modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = modifier.size(36.dp),
                        strokeWidth = 2.6.dp,
                        color = colorResource(id = R.color.accent_primary)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        navController = null,
        snackStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow(),
        showProgressBar = true,
        basicUserInfo = MutableLiveData(null)
    )
}
