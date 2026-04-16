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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.rekast.sdk.sample.ui.theme.AppTheme
import kotlin.getValue

/**
 * Fragment that hosts the Home screen, fetching user info, account status, and account balance on
 * resume and rendering [MainScreen] via Jetpack Compose.
 *
 * Credentials are no longer pushed into the SDK from here — the [io.rekast.sdk.network.interfaces.CredentialProvider]
 * wired by the app's DI reads them from storage on every request automatically.
 */
@ExperimentalMaterialApi
@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    private val homeScreenViewModel by viewModels<HomeScreenViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val showProgressBar by homeScreenViewModel.showProgressBar.observeAsState(false)
                    MainScreen(
                        navController = navController,
                        snackStateFlow = homeScreenViewModel.snackBarStateFlow,
                        showProgressBar = showProgressBar,
                        basicUserInfo = homeScreenViewModel.basicUserInfo,
                        accountHolderStatus = homeScreenViewModel.accountHolderStatus,
                        accountBalance = homeScreenViewModel.accountBalance
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        homeScreenViewModel.getBasicUserInfo()
        homeScreenViewModel.validateAccountHolderStatus()
        homeScreenViewModel.getAccountBalance()
        homeScreenViewModel.getUserInfoWithConsent()
    }
}
