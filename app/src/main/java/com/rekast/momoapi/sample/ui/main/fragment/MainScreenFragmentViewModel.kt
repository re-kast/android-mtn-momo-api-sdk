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

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rekast.momoapi.BuildConfig
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.callback.APIResult
import com.rekast.momoapi.sample.utils.SnackBarComponentConfiguration
import com.rekast.momoapi.sample.utils.Utils
import com.rekast.momoapi.utils.Constants
import com.rekast.momoapi.utils.ProductType
import com.rekast.momoapi.utils.Settings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

class MainScreenFragmentViewModel : ViewModel() {
    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()
    var context: Context? = null
    private var momoAPi: MomoAPI? = null

    fun checkUser() {
        viewModelScope.launch {
            //withContext(dispatcherProvider.io()) { getApiKey() }
            momoAPi!!.checkApiUser(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                BuildConfig.MOMO_API_VERSION_V1,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        emitSnackBarState(SnackBarComponentConfiguration(message = "The API User provided was found! Happy testing"))
                        getApiKey()
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException!!
                        emitSnackBarState(
                            SnackBarComponentConfiguration(
                                message = "${momoAPIException.message} Error fetching the API User. Please confirm if the API User ID or API product supplied is valid",
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getApiKey() {
        viewModelScope.launch {
            //withContext(dispatcherProvider.io()) { getAccessToken() }

            val apiUserKey = context?.let { Utils.getApiKey(it) }
            if (StringUtils.isNotBlank(apiUserKey)) {
                emitSnackBarState(SnackBarComponentConfiguration(message = "An API Key exists."))
                getAccessToken()
            } else {
                momoAPi!!.getUserApiKey(
                    Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                    BuildConfig.MOMO_API_VERSION_V1,
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val generatedApiUserKey = momoAPIResult.value
                            context?.let { Utils.saveApiKey(it, generatedApiUserKey.apiKey) }
                            emitSnackBarState(SnackBarComponentConfiguration(message = "Generated and saved a valid API Key"))
                            getAccessToken()
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException!!
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "${momoAPIException.message} Error fetching the API Key. Please confirm if the API User ID or API product supplied is valid",
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getAccessToken() {
        viewModelScope.launch {
            val apiUserKey = context?.let { Utils.getApiKey(it) }
            val accessToken = context?.let { Utils.getAccessToken(it) }
            if (StringUtils.isNotBlank(apiUserKey) && StringUtils.isBlank(accessToken)) {
                apiUserKey?.let { apiKey ->
                    momoAPi?.getAccessToken(
                        Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                        apiKey,
                        Constants.ProductTypes.REMITTANCE,
                    ) { momoAPIResult ->
                        when (momoAPIResult) {
                            is APIResult.Success -> {
                                val generatedAccessToken = momoAPIResult.value
                                context?.let { activityContext ->
                                    Utils.saveAccessToken(
                                        activityContext,
                                        generatedAccessToken
                                    )
                                }
                                emitSnackBarState(SnackBarComponentConfiguration(message = "Generated and saved a valid Access Token"))
                            }
                            is APIResult.Failure -> {
                                val momoAPIException = momoAPIResult.APIException!!
                                emitSnackBarState(
                                    SnackBarComponentConfiguration(
                                        message = "${momoAPIException.message} Error fetching the API Access Token. Please confirm if the API User ID, API Key or API product supplied is valid",
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                emitSnackBarState(SnackBarComponentConfiguration(message = "A valid Access Token exists. Please renew it after expiry"))
                getBasicUserInfo()
            }
        }
    }

    fun getBasicUserInfo() {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi!!.getBasicUserInfo(
                    "46733123459",
                    Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                    it,
                    BuildConfig.MOMO_API_VERSION_V1,
                    Constants.ProductTypes.REMITTANCE,
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val basicInfo = momoAPIResult.value
                            Timber.d(basicInfo.toString())
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                        }
                    }
                }
            }
        }
    }

    fun provideContext(fragmentContext: Context) {
        context = fragmentContext
    }

    fun provideMomoAPI(fragmentMomoAPI: MomoAPI) {
        momoAPi = fragmentMomoAPI
    }

    private fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }
}
