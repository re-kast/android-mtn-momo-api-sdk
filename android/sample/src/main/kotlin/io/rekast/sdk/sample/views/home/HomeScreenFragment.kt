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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.views.MainViewModel
import kotlin.getValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Fragment that hosts the Home screen, fetching user info, account status, and account balance on
 * resume and rendering [MainScreen] via Jetpack Compose.
 *
 * Data fetching is gated on [MainViewModel.isBootstrapComplete]: on each resume the fragment waits
 * for the first `true` emission before calling any [HomeScreenViewModel] method. This prevents the
 * home API calls from racing against the credential bootstrap sequence initiated by
 * [io.rekast.sdk.sample.views.MainActivity.onResume]. On subsequent resumes where credentials are
 * already cached, bootstrap completes in milliseconds and the calls fire immediately.
 *
 * Credentials are no longer pushed into the SDK from here — the [io.rekast.sdk.network.interfaces.CredentialProvider]
 * wired by the app's DI reads them from storage on every request automatically.
 */
@ExperimentalMaterialApi
@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    private val homeScreenViewModel by viewModels<HomeScreenViewModel>()

    /**
     * Activity-scoped so we observe the same [MainViewModel] instance that [MainActivity] uses to
     * run [MainViewModel.checkUser]. The bootstrap signal propagates correctly regardless of which
     * activity back-stack entry hosts this fragment.
     */
    private val mainViewModel by activityViewModels<MainViewModel>()

    /**
     * Inflates the Home screen Compose hierarchy, wiring up [MainScreen]
     * with its ViewModel, NavController, and snackbar state.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val isBootstrapComplete by mainViewModel.isBootstrapComplete.collectAsState()
                    val vmShowProgressBar by homeScreenViewModel.showProgressBar.observeAsState(false)
                    MainScreen(
                        navController = navController,
                        snackStateFlow = homeScreenViewModel.snackBarStateFlow,
                        showProgressBar = !isBootstrapComplete || vmShowProgressBar,
                        basicUserInfo = homeScreenViewModel.basicUserInfo,
                        userInfoWithConsent = homeScreenViewModel.userInfoWithConsent,
                        accountHolderStatus = homeScreenViewModel.accountHolderStatus,
                        accountBalance = homeScreenViewModel.accountBalance
                    )
                }
            }
        }
    }

    /**
     * Waits for [MainViewModel.isBootstrapComplete] to become `true` before triggering any home
     * screen API calls. This block is automatically cancelled on [onPause] and re-executed on the
     * next [onResume], ensuring data is refreshed on every visit without racing against bootstrap.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.isBootstrapComplete.first { it }
                homeScreenViewModel.loadHomeData()
            }
        }
    }
}
