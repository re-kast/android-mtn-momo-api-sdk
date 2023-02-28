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
package com.rekast.momoapi.sample.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.rekast.momoapi.sample.R
import com.rekast.momoapi.sample.ui.components.SnackBarMessage
import com.rekast.momoapi.sample.ui.main.AppMainViewModel
import com.rekast.momoapi.sample.ui.navigation.drawer.Drawer
import com.rekast.momoapi.sample.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@AndroidEntryPoint
class MainScreenFragment : Fragment() {
    val appMainViewModel by activityViewModels<AppMainViewModel>()
    val mainScreenFragmentViewModel by viewModels<MainScreenFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val scope = rememberCoroutineScope()
                val scaffoldState = rememberScaffoldState()
                val openDrawer: (Boolean) -> Unit = { open: Boolean ->
                    scope.launch {
                        if (open) scaffoldState.drawerState.open() else scaffoldState.drawerState.close()
                    }
                }

                AppTheme {
                    Scaffold(
                        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                        scaffoldState = scaffoldState,
                        drawerContent = {
                            Drawer(
                                scope = scope,
                                scaffoldState = scaffoldState,
                                navController = findNavController(),
                            )
                        },
                        snackbarHost = { snackBarHostState ->
                            SnackBarMessage(
                                snackBarHostState = snackBarHostState,
                                backgroundColorHex = colorResource(id = R.color.accent_secondary).toString(),
                                actionColorHex = colorResource(id = R.color.accent_primary).toString(),
                                contentColorHex = colorResource(id = R.color.white).toString(),
                            )
                        },
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }
}
