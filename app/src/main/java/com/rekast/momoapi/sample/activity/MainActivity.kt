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
package com.rekast.momoapi.sample.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.rekast.momoapi.BuildConfig
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.callback.MomoAPIResult
import com.rekast.momoapi.sample.R
import com.rekast.momoapi.sample.databinding.ActivityMainBinding
import com.rekast.momoapi.sample.utils.Utils
import com.rekast.momoapi.utils.Constants
import com.rekast.momoapi.utils.ProductType
import com.rekast.momoapi.utils.Settings
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var momoRemittanceApi: MomoAPI
    private lateinit var momoCollectionApi: MomoAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        momoRemittanceApi = momoRemittanceApi()
        checkUser()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    private fun momoRemittanceApi(): MomoAPI {
        return MomoAPI.builder(BuildConfig.MOMO_API_USER_ID)
            .setTransactionType(ProductType.REMITTANCE)
            .setEnvironment(BuildConfig.MOMO_ENVIRONMENT)
            .getBaseURL(BuildConfig.MOMO_BASE_URL)
            .build()
    }

    private fun checkUser() {
        momoRemittanceApi.checkApiUser(
            Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
            BuildConfig.MOMO_API_USER_ID,
            BuildConfig.MOMO_API_VERSION_V1,
        ) { momoAPIResult ->
            when (momoAPIResult) {
                is MomoAPIResult.Success -> {
                    getApiKey()
                }
                is MomoAPIResult.Failure -> {
                    val momoAPIException = momoAPIResult.momoAPIException
                    toast(momoAPIException?.message ?: "An error occurred!")
                }
            }
        }
    }

    private fun getApiKey() {
        momoRemittanceApi.getUserApiKey(
            Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
            BuildConfig.MOMO_API_USER_ID,
            BuildConfig.MOMO_API_VERSION_V1,
        ) { momoAPIResult ->
            when (momoAPIResult) {
                is MomoAPIResult.Success -> {
                    val apiUserKey = momoAPIResult.value
                    Utils.saveApiKey(this, apiUserKey.apiKey)
                    getAccessToken()
                    Timber.d(apiUserKey.apiKey)
                }
                is MomoAPIResult.Failure -> {
                    val momoAPIException = momoAPIResult.momoAPIException
                    toast(momoAPIException?.message ?: "An error occurred!")
                }
            }
        }
    }

    private fun getAccessToken() {
        val apiUserKey = Utils.getApiKey(this)
        if (StringUtils.isNotBlank(apiUserKey)) {
            momoRemittanceApi.getAccessToken(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                BuildConfig.MOMO_API_USER_ID,
                apiUserKey,
                Constants.ProductTypes.REMITTANCE,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is MomoAPIResult.Success -> {
                        val accessToken = momoAPIResult.value
                        Utils.saveAccessToken(this, accessToken)
                        Timber.d(accessToken.accessToken)
                        getAccountBalance()
                        getBasicUserInfo()
                        getUserInfoWithoutConsent()
                    }
                    is MomoAPIResult.Failure -> {
                        val momoAPIException = momoAPIResult.momoAPIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getAccountBalance() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoRemittanceApi.getBalance(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
                BuildConfig.MOMO_ENVIRONMENT,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is MomoAPIResult.Success -> {
                        val balance = momoAPIResult.value
                        Timber.d(balance.toString())
                    }
                    is MomoAPIResult.Failure -> {
                        val momoAPIException = momoAPIResult.momoAPIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getBasicUserInfo() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoRemittanceApi.getBasicUserInfo(
                "46733123459",
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
                BuildConfig.MOMO_ENVIRONMENT,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is MomoAPIResult.Success -> {
                        val basicInfo = momoAPIResult.value
                        Timber.d(basicInfo.toString())
                    }
                    is MomoAPIResult.Failure -> {
                        val momoAPIException = momoAPIResult.momoAPIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getUserInfoWithoutConsent() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoRemittanceApi.getUserInfoWithoutConsent(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
                BuildConfig.MOMO_ENVIRONMENT,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is MomoAPIResult.Success -> {
                        val getUserInfoWithoutConsent = momoAPIResult.value
                        Timber.d(getUserInfoWithoutConsent.toString())
                    }
                    is MomoAPIResult.Failure -> {
                        val momoAPIException = momoAPIResult.momoAPIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
