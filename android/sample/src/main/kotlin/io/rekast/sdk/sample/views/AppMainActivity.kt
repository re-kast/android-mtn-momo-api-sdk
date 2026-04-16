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
package io.rekast.sdk.sample.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import io.rekast.sdk.sample.R

/**
 * Main activity for the MTN MOMO SDK sample application.
 *
 * This activity sets up the navigation host and initializes the main view model.
 */
@AndroidEntryPoint
@ExperimentalMaterialApi
open class AppMainActivity : AppCompatActivity() {
    lateinit var navHostFragment: NavHostFragment
    private val appMainViewModel by viewModels<AppMainViewModel>()

    /**
     * Called when the activity is created.
     *
     * This method initializes the navigation host and sets up the fragment manager.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FragmentContainerView(this).apply { id = R.id.navigation_host })
        if (savedInstanceState == null) {
            navHostFragment = NavHostFragment.create(R.navigation.navigation_graph)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.navigation_host, navHostFragment)
                .setPrimaryNavigationFragment(navHostFragment)
                .commit()
        } else {
            navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment
        }
    }

    /**
     * Called when the activity is resumed.
     *
     * This method checks the user status by invoking the checkUser method in the view model.
     */
    override fun onResume() {
        super.onResume()
        appMainViewModel.checkUser()
    }
}
