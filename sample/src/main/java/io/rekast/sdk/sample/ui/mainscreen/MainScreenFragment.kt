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
package io.rekast.sdk.sample.ui.mainscreen

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
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.rekast.sdk.MomoAPI
import io.rekast.sdk.sample.activity.AppMainActivity
import io.rekast.sdk.sample.ui.theme.AppTheme

@ExperimentalMaterialApi
@AndroidEntryPoint
class MainScreenFragment : Fragment() {
    private lateinit var activity: AppMainActivity
    private lateinit var fragmentMomoAPI: MomoAPI
    private val mainScreenFragmentViewModel by viewModels<MainScreenFragmentViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val showProgressBar by mainScreenFragmentViewModel.showProgressBar.observeAsState(false)
                    MainScreen(
                        navController = findNavController(),
                        snackStateFlow = mainScreenFragmentViewModel.snackBarStateFlow,
                        showProgressBar = showProgressBar,
                        basicUserInfo = mainScreenFragmentViewModel.basicUserInfo,
                        accountHolderStatus = mainScreenFragmentViewModel._accountHolderStatus,
                        accountBalance = mainScreenFragmentViewModel._accountBalance
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity = requireActivity() as AppMainActivity
        fragmentMomoAPI = activity.momoAPI

        mainScreenFragmentViewModel.provideContext(activity)
        mainScreenFragmentViewModel.provideMomoAPI(fragmentMomoAPI)
        mainScreenFragmentViewModel.getBasicUserInfo()
        mainScreenFragmentViewModel.validateAccountHolderStatus()
        // mainScreenFragmentViewModel.getAccountBalance()
    }
}
