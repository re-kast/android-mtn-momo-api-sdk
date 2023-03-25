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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rekast.momoapi.BuildConfig
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.callback.APIResult
import com.rekast.momoapi.model.api.BasicUserInfo
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

class MainScreenFragmentViewModel : ViewModel() {
    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()
    var context: Context? = null
    private var momoAPi: MomoAPI? = null
    val showProgressBar = MutableLiveData(false)
    var basicUserInfo: MutableLiveData<BasicUserInfo?> = MutableLiveData(null)

    fun getBasicUserInfo() {
        showProgressBar.postValue(true)
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi!!.getBasicUserInfo(
                    "99733123459",
                    Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                    it,
                    BuildConfig.MOMO_API_VERSION_V1,
                    Constants.ProductTypes.REMITTANCE
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val date = Utils.convertToDate(momoAPIResult.value.updatedAt.toLong())
                            momoAPIResult.value.updatedAt = date
                            basicUserInfo.postValue(momoAPIResult.value)
                            showProgressBar.postValue(false)

                            emitSnackBarState(
                                SnackBarComponentConfiguration(message = "Basic User info fetched successfully")
                            )
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                            showProgressBar.postValue(false)

                            emitSnackBarState(
                                SnackBarComponentConfiguration(message = "Basic User info not fetched! Please try again")
                            )
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
