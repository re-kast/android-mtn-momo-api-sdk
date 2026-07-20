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
package io.rekast.sdk.sample.views.collection.payment

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
 * Fragment that hosts the Collection Payment screen, rendering [PaymentScreen] via Jetpack Compose.
 */
@ExperimentalMaterialApi
@AndroidEntryPoint
class PaymentScreenFragment : Fragment() {
    private val paymentScreenViewModel by viewModels<PaymentScreenViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val isBootstrapComplete by mainViewModel.isBootstrapComplete.collectAsState()
                    val vmShowProgressBar by paymentScreenViewModel.showProgressBar.observeAsState(false)
                    PaymentScreen(
                        navController = navController,
                        snackStateFlow = paymentScreenViewModel.snackBarStateFlow,
                        showProgressBar = !isBootstrapComplete || vmShowProgressBar,
                        viewModel = paymentScreenViewModel
                    )
                }
            }
        }
    }
}
