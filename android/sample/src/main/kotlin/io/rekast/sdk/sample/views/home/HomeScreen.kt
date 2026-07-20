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
package io.rekast.sdk.sample.views.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.accountdetails.AccountBalanceComponent
import io.rekast.sdk.sample.ui.components.accountdetails.AccountStatusComponent
import io.rekast.sdk.sample.ui.components.accountdetails.BasicUserInfoComponent
import io.rekast.sdk.sample.ui.components.accountdetails.ProfileHeaderComponent
import io.rekast.sdk.sample.ui.components.accountdetails.UserInfoWithConsentComponent
import io.rekast.sdk.sample.ui.components.general.CircularProgressBarComponent
import io.rekast.sdk.sample.ui.components.general.MomoScaffold
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Renders the Home screen: a scrollable stack of cards showing the signed-in user's profile,
 * basic info, consent-granted verified profile, account status, and balance — or a progress
 * indicator while data is loading. Themed for light and dark mode via [MomoScaffold].
 *
 * @param navController [NavController] used to navigate between destinations via the drawer.
 * @param snackStateFlow Flow emitting [SnackBarComponentConfiguration] messages to display.
 * @param showProgressBar Whether to display a loading indicator instead of the data cards.
 * @param basicUserInfo LiveData holding the [BasicUserInfo] to render.
 * @param userInfoWithConsent LiveData holding the consent-granted [UserInfoWithConsent] to render.
 * @param accountHolderStatus LiveData holding the [AccountHolderStatus] to render.
 * @param accountBalance LiveData holding the [AccountBalance] to render.
 */
@Composable
fun MainScreen(
    navController: NavController?,
    snackStateFlow: SharedFlow<SnackBarComponentConfiguration>,
    showProgressBar: Boolean = false,
    basicUserInfo: MutableLiveData<BasicUserInfo?>,
    userInfoWithConsent: MutableLiveData<UserInfoWithConsent?>,
    accountHolderStatus: MutableLiveData<AccountHolderStatus?>,
    accountBalance: MutableLiveData<AccountBalance?>
) {
    MomoScaffold(titleRes = R.string.home_screen, navController = navController, snackStateFlow = snackStateFlow) {
        if (showProgressBar) {
            CircularProgressBarComponent()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProfileHeaderComponent(
                        basicUserInfo = basicUserInfo,
                        userInfoWithConsent = userInfoWithConsent
                    )
                }
                item { BasicUserInfoComponent(basicUserInfo = basicUserInfo) }
                item { UserInfoWithConsentComponent(userInfoWithConsent = userInfoWithConsent) }
                item { AccountStatusComponent(accountHolderStatus = accountHolderStatus) }
                item { AccountBalanceComponent(accountBalance = accountBalance) }
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
        showProgressBar = false,
        basicUserInfo = MutableLiveData(null),
        userInfoWithConsent = MutableLiveData(null),
        accountHolderStatus = MutableLiveData(null),
        accountBalance = MutableLiveData(null)
    )
}
