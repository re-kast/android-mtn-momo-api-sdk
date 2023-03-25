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
package com.rekast.momoapi.sample.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import com.rekast.momoapi.sample.activity.AppMainActivity
import com.rekast.momoapi.sample.ui.theme.AppTheme
import com.rekast.momoapi.sample.utils.applyWindowInsetListener
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreenActivity = this@SplashScreenActivity
        splashScreenActivity.applyWindowInsetListener()

        setContent {
            AppTheme { SplashScreen() }
        }

        Handler().postDelayed({
            val i = Intent(this@SplashScreenActivity, AppMainActivity::class.java)
            startActivity(i)
            finish()
        }, 3000)
    }
}