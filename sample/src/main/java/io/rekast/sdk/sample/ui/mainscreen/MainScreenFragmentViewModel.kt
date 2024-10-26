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
package io.rekast.sdk.sample.ui.mainscreen

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.io.rekast.momoapi.utils.Settings
import io.rekast.sdk.callback.APIResult
import io.rekast.sdk.model.api.AccountBalance
import io.rekast.sdk.model.api.AccountHolder
import io.rekast.sdk.model.api.AccountHolderStatus
import io.rekast.sdk.model.api.BasicUserInfo
import io.rekast.sdk.network.api.route.MomoAPI
import io.rekast.sdk.sample.BuildConfig
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.AccountHolderType
import io.rekast.sdk.utils.ProductType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

class MainScreenFragmentViewModel : ViewModel() {
    var context: Context? = null
    private var momoAPi: MomoAPI? = null
    val showProgressBar = MutableLiveData(false)
    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()
    var basicUserInfo: MutableLiveData<BasicUserInfo?> = MutableLiveData(null)
    var _accountHolderStatus: MutableLiveData<AccountHolderStatus?> = MutableLiveData(null)
    var _accountBalance: MutableLiveData<AccountBalance?> = MutableLiveData(null)

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
                    ProductType.REMITTANCE.productType
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
                                SnackBarComponentConfiguration(
                                    message = "${momoAPIException!!.message} " + "Basic User" +
                                        " info not fetched! Please try again"
                                )
                            )
                        }
                    }
                }
            }
        } else {
            showProgressBar.postValue(false)
            emitSnackBarState(
                SnackBarComponentConfiguration(
                    message = "Expired access token! Please refresh the token"
                )
            )
        }
    }

    fun validateAccountHolderStatus() {
        showProgressBar.postValue(true)
        val accessToken = context?.let { Utils.getAccessToken(it) }
        val accountHolder = AccountHolder(
            partyId = "346736732646",
            partyIdType = AccountHolderType.MSISDN.accountHolderType
        )
        if (StringUtils.isNotBlank(accessToken)) {
            if (accessToken != null) {
                momoAPi?.validateAccountHolderStatus(
                    accountHolder,
                    BuildConfig.MOMO_API_VERSION_V1,
                    ProductType.REMITTANCE.productType,
                    Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                    accessToken
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val accountHolderStatus = Gson().fromJson(
                                momoAPIResult.value!!.source().readUtf8(),
                                AccountHolderStatus::class.java
                            )
                            _accountHolderStatus.postValue(accountHolderStatus)
                            showProgressBar.postValue(false)

                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "Account status fetched successfully"
                                )
                            )
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                            showProgressBar.postValue(false)

                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "${momoAPIException!!.message} Account status not fetched!"
                                )
                            )
                            Timber.d(momoAPIException)
                        }
                    }
                }
            }
        } else {
            showProgressBar.postValue(false)
            emitSnackBarState(
                SnackBarComponentConfiguration(
                    message = "Expired access token! Please refresh the token"
                )
            )
        }
    }

    fun getAccountBalance() {
        showProgressBar.postValue(true)
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi?.getBalance(
                    Constants.SANDBOX_CURRENCY,
                    Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                    it,
                    BuildConfig.MOMO_API_VERSION_V1,
                    ProductType.REMITTANCE.productType
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val balance = momoAPIResult.value
                            _accountBalance.postValue(balance)
                            showProgressBar.postValue(false)

                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "Account balance fetched successfully"
                                )
                            )
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                            showProgressBar.postValue(false)

                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "${momoAPIException!!.message} Account balance not fetched!"
                                )
                            )

                            Timber.d(momoAPIException)
                        }
                    }
                }
            }
        } else {
            showProgressBar.postValue(false)
            emitSnackBarState(
                SnackBarComponentConfiguration(
                    message = "Expired access token! Please refresh the token"
                )
            )
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
