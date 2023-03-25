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
package com.rekast.momoapi.sample.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.sample.ui.main.AppMainActivity
import com.rekast.momoapi.sample.ui.main.AppMainViewModel
import com.rekast.momoapi.sample.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@AndroidEntryPoint
class CollectionScreenFragment : Fragment() {
    private lateinit var activity: AppMainActivity
    private lateinit var fragmentMomoAPI: MomoAPI
    private val appMainViewModel by activityViewModels<AppMainViewModel>()
    private val collectionScreenViewModel by viewModels<CollectionScreenViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    CollectionScreen(
                        navController = findNavController(),
                        snackStateFlow = collectionScreenViewModel.snackBarStateFlow
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        activity = requireActivity() as AppMainActivity
        fragmentMomoAPI = activity.momoAPI

        collectionScreenViewModel.provideContext(activity)
        collectionScreenViewModel.provideMomoAPI(fragmentMomoAPI)
        collectionScreenViewModel.provideAppMainViewModel(appMainViewModel)
    }
}
