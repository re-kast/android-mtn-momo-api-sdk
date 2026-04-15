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
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.utils.DefaultDispatcherProvider
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.sample.views.AppMainActivity
import javax.inject.Inject
import kotlin.getValue
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    @Inject
    lateinit var dispatcherProvider: DefaultDispatcherProvider

    @Inject
    lateinit var defaultRepository: DefaultRepository
    private lateinit var activity: AppMainActivity
    private val homeScreenViewModel by viewModels<HomeScreenViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val showProgressBar by homeScreenViewModel.showProgressBar.observeAsState(false)
                    MainScreen(
                        navController = findNavController(),
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
        activity = requireActivity() as AppMainActivity

        val accessToken = Utils.getAccessToken(activity.applicationContext)
        setAccessToken(accessToken)
        homeScreenViewModel.viewModelScope.launch(dispatcherProvider.io()) {
            homeScreenViewModel.getBasicUserInfo()
            homeScreenViewModel.validateAccountHolderStatus()
            homeScreenViewModel.getAccountBalance()
        }

        val oauthAccessToken = Utils.getOauthAccessToken(activity.applicationContext)
        setAccessToken(oauthAccessToken)
        homeScreenViewModel.viewModelScope.launch(dispatcherProvider.io()) {
            homeScreenViewModel.getUserInfoWithConsent()
        }
    }

    /**
     * Sets the Access Token credentials.
     *
     * @param accessToken The access token.
     */
    fun setAccessToken(accessToken: String) {
        val accessTokenCredentials = AccessTokenCredentials(accessToken)
        homeScreenViewModel.viewModelScope.launch {
            defaultRepository.setUpAccessTokenAuth(accessTokenCredentials)
        }
    }
}
