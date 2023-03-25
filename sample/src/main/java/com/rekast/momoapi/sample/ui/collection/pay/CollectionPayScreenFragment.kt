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
package com.rekast.momoapi.sample.ui.collection.pay

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.sample.activity.AppMainActivity
import com.rekast.momoapi.sample.activity.AppMainViewModel
import com.rekast.momoapi.sample.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@AndroidEntryPoint
class CollectionPayScreenFragment : Fragment() {
    private lateinit var activity: AppMainActivity
    private lateinit var fragmentMomoAPI: MomoAPI
    private val appMainViewModel by activityViewModels<AppMainViewModel>()
    private val collectionPayScreenViewModel by viewModels<CollectionPayScreenViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val showProgressBar by collectionPayScreenViewModel.showProgressBar.observeAsState(false)
                    CollectionScreen(
                        navController = findNavController(),
                        snackStateFlow = collectionPayScreenViewModel.snackBarStateFlow,
                        showProgressBar = showProgressBar,
                        collectionPayScreenViewModel = collectionPayScreenViewModel,
                        momoTransaction = collectionPayScreenViewModel.momoTransaction
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        activity = requireActivity() as AppMainActivity
        fragmentMomoAPI = activity.momoAPI

        collectionPayScreenViewModel.provideContext(activity)
        collectionPayScreenViewModel.provideMomoAPI(fragmentMomoAPI)
        collectionPayScreenViewModel.provideAppMainViewModel(appMainViewModel)
    }
}
