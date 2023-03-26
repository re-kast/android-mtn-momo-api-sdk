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
package io.rekast.momoapi.sample.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.io.rekast.momoapi.utils.Settings
import io.rekast.momoapi.BuildConfig
import io.rekast.momoapi.MomoAPI
import io.rekast.momoapi.callback.APIResult
import io.rekast.momoapi.model.api.MomoTransaction
import io.rekast.momoapi.sample.utils.SnackBarComponentConfiguration
import io.rekast.momoapi.sample.utils.Utils
import io.rekast.momoapi.utils.ProductType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

class AppMainViewModel : ViewModel() {
    fun momoAPI(): MomoAPI {
        return MomoAPI.builder(BuildConfig.MOMO_API_USER_ID).setEnvironment(BuildConfig.MOMO_ENVIRONMENT)
            .getBaseURL(BuildConfig.MOMO_BASE_URL).build()
    }

    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()
    var context: Context? = null
    private var momoAPi: MomoAPI? = null

    fun checkUser() {
        viewModelScope.launch {
            momoAPi?.checkApiUser(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                BuildConfig.MOMO_API_VERSION_V1
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        getApiKey()
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException!!
                    }
                }
            }
        }
    }

    private fun getApiKey() {
        viewModelScope.launch {
            val apiUserKey = context?.let { Utils.getApiKey(it) }
            if (StringUtils.isNotBlank(apiUserKey)) {
                getAccessToken()
            } else {
                momoAPi?.getUserApiKey(
                    Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                    BuildConfig.MOMO_API_VERSION_V1
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val generatedApiUserKey = momoAPIResult.value
                            context?.let { Utils.saveApiKey(it, generatedApiUserKey.apiKey) }
                            getAccessToken()
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException!!
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
                        ProductType.REMITTANCE.productType
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
                            }
                            is APIResult.Failure -> {
                                val momoAPIException = momoAPIResult.APIException!!
                            }
                        }
                    }
                }
            } else {
            }
        }
    }

    fun refund(requestToPayUuid: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        val transactionUuid = Settings.generateUUID()
        if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(requestToPayUuid)) {
            val creditTransaction = createRefundTransaction(requestToPayUuid)
            accessToken?.let {
                momoAPi?.refund(
                    it,
                    creditTransaction,
                    BuildConfig.MOMO_API_VERSION_V2,
                    Settings.getProductSubscriptionKeys(ProductType.DISBURSEMENTS),
                    transactionUuid
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            getRefundStatus(transactionUuid)
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                        }
                    }
                }
            }
        }
    }

    private fun getRefundStatus(referenceId: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi?.getRefundStatus(
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    Settings.getProductSubscriptionKeys(ProductType.DISBURSEMENTS),
                    it
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val completeTransfer =
                                Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), MomoTransaction::class.java)
                            Timber.d(completeTransfer.toString())
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                        }
                    }
                }
            }
        }
    }

    private fun createRefundTransaction(requestToPayUuid: String): MomoTransaction {
        return MomoTransaction(
            "30",
            "EUR",
            null,
            Settings.generateUUID(),
            null,
            null,
            "Testing",
            "The Good Company",
            null,
            null,
            requestToPayUuid
        )
    }

    fun provideContext(activityContext: Context) {
        context = activityContext
    }

    fun provideMomoAPI(fragmentMomoAPI: MomoAPI) {
        momoAPi = fragmentMomoAPI
    }

    private fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }

/*

    private fun getBasicUserInfo() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getBasicUserInfo(
                "46733123459",
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
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
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getUserInfoWithConsent() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getUserInfoWithConsent(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val getUserInfoWithoutConsent = momoAPIResult.value
                        Timber.d(getUserInfoWithoutConsent.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

*/
}
