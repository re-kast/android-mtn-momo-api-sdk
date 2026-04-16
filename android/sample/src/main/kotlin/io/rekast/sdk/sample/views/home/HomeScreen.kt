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
package io.rekast.sdk.sample.views.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.accountdetails.AccountBalanceComponent
import io.rekast.sdk.sample.ui.components.accountdetails.AccountStatusComponent
import io.rekast.sdk.sample.ui.components.accountdetails.BasicUserInfoComponent
import io.rekast.sdk.sample.ui.components.general.CircularProgressBarComponent
import io.rekast.sdk.sample.ui.components.general.SnackBarComponent
import io.rekast.sdk.sample.ui.navigation.drawer.Drawer
import io.rekast.sdk.sample.ui.navigation.topbar.TopBar
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarThemeOptions
import io.rekast.sdk.sample.utils.hookSnackBar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Renders the Home screen displaying basic user info, account holder status, and account balance,
 * or a progress indicator while data is loading.
 *
 * @param modifier Modifier applied to the main box container.
 * @param navController [NavController] used to navigate between destinations via the drawer.
 * @param snackStateFlow Flow emitting [SnackBarComponentConfiguration] messages to display.
 * @param showProgressBar Whether to display a loading indicator instead of the data panels; defaults to false.
 * @param basicUserInfo LiveData holding the [BasicUserInfo] to render.
 * @param accountHolderStatus LiveData holding the [AccountHolderStatus] to render.
 * @param accountBalance LiveData holding the [AccountBalance] to render.
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController?,
    snackStateFlow: SharedFlow<SnackBarComponentConfiguration>,
    showProgressBar: Boolean = false,
    basicUserInfo: MutableLiveData<BasicUserInfo?>,
    accountHolderStatus: MutableLiveData<AccountHolderStatus?>,
    accountBalance: MutableLiveData<AccountBalance?>
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
        Box(modifier = modifier.padding(padding)) {
            if (!showProgressBar) {
                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    BasicUserInfoComponent(basicUserInfo = basicUserInfo)
                    Spacer(modifier = Modifier.height(30.dp))
                    AccountStatusComponent(accountHolderStatus = accountHolderStatus)
                    Spacer(modifier = Modifier.height(30.dp))
                    AccountBalanceComponent(accountBalance = accountBalance)
                }
            } else {
                CircularProgressBarComponent()
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
        basicUserInfo = MutableLiveData(null),
        accountHolderStatus = MutableLiveData(null),
        accountBalance = MutableLiveData(null)
    )
}
