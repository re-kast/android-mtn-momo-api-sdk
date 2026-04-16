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
package io.rekast.sdk.sample.views.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import dagger.hilt.android.AndroidEntryPoint
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.utils.applyWindowInsetListener
import io.rekast.sdk.sample.views.AppMainActivity
import io.rekast.sdk.sample.views.AppMainViewModel

/**
 * Entry-point activity that displays the [SplashScreen] for 3 seconds before launching
 * [AppMainActivity] and finishing itself.
 *
 * [AppMainViewModel.checkUser] is called here so that ART bytecode verification of the
 * ViewModel and Repository coroutine lambdas (and the credential bootstrap network calls)
 * happen during the existing 3-second splash window instead of on the main thread when
 * [AppMainActivity.onResume] runs.
 */
@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private val appMainViewModel by viewModels<AppMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyWindowInsetListener()

        setContent {
            AppTheme { SplashScreen() }
        }

        // Kick off credential bootstrap early — hides ART class verification latency
        // and network round-trips behind the splash delay.
        appMainViewModel.checkUser()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AppMainActivity::class.java))
            finish()
        }, 3000)
    }
}
