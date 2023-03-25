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
package com.rekast.momoapi.sample.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.sample.R
import com.rekast.momoapi.sample.utils.DefaultDispatcherProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalMaterialApi
open class AppMainActivity : AppCompatActivity() {
    @Inject
    lateinit var dispatcherProvider: DefaultDispatcherProvider
    lateinit var momoAPI: MomoAPI
    private val appMainViewModel by viewModels<AppMainViewModel>()
    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FragmentContainerView(this).apply { id = R.id.nav_host })
        navHostFragment =
            NavHostFragment.create(R.navigation.navigation_graph)
        // initialize the API
        momoAPI = appMainViewModel.momoAPI()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        appMainViewModel.viewModelScope.launch(dispatcherProvider.io()) {}

        appMainViewModel.provideContext(this)
        appMainViewModel.provideMomoAPI(momoAPI)
        appMainViewModel.checkUser()
    }
}
