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
package com.rekast.momoapi.sample.ui.collection

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.callback.APIResult
import com.rekast.momoapi.model.api.AccountHolder
import com.rekast.momoapi.model.api.Notification
import com.rekast.momoapi.model.api.Transaction
import com.rekast.momoapi.sample.BuildConfig
import com.rekast.momoapi.sample.ui.main.AppMainViewModel
import com.rekast.momoapi.sample.utils.SnackBarComponentConfiguration
import com.rekast.momoapi.sample.utils.Utils
import com.rekast.momoapi.utils.AccountHolderType
import com.rekast.momoapi.utils.Constants
import com.rekast.momoapi.utils.ProductType
import com.rekast.momoapi.utils.Settings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

class CollectionScreenViewModel : ViewModel() {
    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()
    var context: Context? = null
    private var momoAPi: MomoAPI? = null
    lateinit var _appMainViewModel: AppMainViewModel

    private fun requestToPay() {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        val creditTransaction = createRequestTpPayTransaction()
        val transactionUuid = Settings.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi?.requestToPay(
                    it,
                    creditTransaction,
                    BuildConfig.MOMO_API_VERSION_V1,
                    Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                    transactionUuid
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            requestToPayDeliveryNotification(transactionUuid, Constants.ProductTypes.COLLECTION)
                            requestToPayTransactionStatus(transactionUuid)
                            _appMainViewModel.refund(transactionUuid)
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                        }
                    }
                }
            }
        }
    }

    private fun createRequestTpPayTransaction(): Transaction {
        return Transaction(
            "10000",
            "EUR",
            null,
            Settings.generateUUID(),
            null,
            AccountHolder(AccountHolderType.MSISDN.name, "346736732646"),
            "Testing",
            "The Good Company",
            null,
            null
        )
    }

    private fun requestToPayTransactionStatus(referenceId: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi?.requestToPayTransactionStatus(
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                    it
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val completeTransfer =
                                Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), Transaction::class.java)
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

    private fun requestToWithdraw() {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        val creditTransaction = createRequestTpPayTransaction()
        val transactionUuid = Settings.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi?.requestToWithdraw(
                    it,
                    creditTransaction,
                    BuildConfig.MOMO_API_VERSION_V2,
                    Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                    transactionUuid
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            requestToWithdrawTransactionStatus(transactionUuid)
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                        }
                    }
                }
            }
        }
    }

    private fun requestToWithdrawTransactionStatus(referenceId: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi?.requestToWithdrawTransactionStatus(
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                    it
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val completeTransfer =
                                Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), Transaction::class.java)
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

    private fun requestToPayDeliveryNotification(referenceId: String, productType: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        val notification = Notification(
            notificationMessage =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur pellentesque mi" +
                " erat, vel placerat erat sollicitudin et. Sed id ex nisi. Quisque luctus metus"
        )
        if (StringUtils.isNotBlank(accessToken) &&
            Settings.checkNotificationMessageLength(notification.notificationMessage)
        ) {
            accessToken?.let {
                momoAPi?.requestToPayDeliveryNotification(
                    notification,
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    productType,
                    Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                    it
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
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

    fun provideAppMainViewModel(appMainViewModel: AppMainViewModel) {
        _appMainViewModel = appMainViewModel
    }

    private fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }
}
