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
package io.rekast.sdk.sample.views.disbursement.deposit

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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.views.MainViewModel

/**
 * Fragment that hosts the Disbursement Deposit screen, rendering [DisbursementScreen] via Jetpack Compose.
 */
@ExperimentalMaterialApi
@AndroidEntryPoint
class DisbursementDepositScreenFragment : Fragment() {
    private val disbursementDepositScreenViewModel by viewModels<DisbursementDepositScreenViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    /**
     * Inflates the Disbursement Deposit screen Compose hierarchy, wiring up [DisbursementScreen]
     * with its ViewModel, NavController, and snackbar state.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val isBootstrapComplete by mainViewModel.isBootstrapComplete.collectAsState()
                    val vmShowProgressBar by disbursementDepositScreenViewModel.showProgressBar.observeAsState(false)
                    DisbursementScreen(
                        navController = navController,
                        snackStateFlow = disbursementDepositScreenViewModel.snackBarStateFlow,
                        showProgressBar = !isBootstrapComplete || vmShowProgressBar,
                        disbursementDepositScreenViewModel = disbursementDepositScreenViewModel,
                        momoTransaction = disbursementDepositScreenViewModel.momoTransaction
                    )
                }
            }
        }
    }

    /** Reserved for future per-resume lifecycle operations. */
    override fun onResume() {
        super.onResume()
    }
}
