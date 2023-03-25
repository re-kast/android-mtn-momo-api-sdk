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
package com.rekast.momoapi.sample.ui.remittance

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.callback.APIResult
import com.rekast.momoapi.model.api.AccountHolder
import com.rekast.momoapi.model.api.MomoNotification
import com.rekast.momoapi.model.api.MomoTransaction
import com.rekast.momoapi.sample.BuildConfig
import com.rekast.momoapi.sample.activity.AppMainViewModel
import com.rekast.momoapi.sample.utils.Constants
import com.rekast.momoapi.sample.utils.SnackBarComponentConfiguration
import com.rekast.momoapi.sample.utils.Utils
import com.rekast.momoapi.utils.AccountHolderType
import com.rekast.momoapi.utils.MomoConstants
import com.rekast.momoapi.utils.ProductType
import com.rekast.momoapi.utils.Settings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils

class RemittanceScreenViewModel : ViewModel() {
    var context: Context? = null
    private var momoAPi: MomoAPI? = null
    val showProgressBar = MutableLiveData(false)
    private lateinit var _appMainViewModel: AppMainViewModel
    var momoTransaction: MutableLiveData<MomoTransaction?> = MutableLiveData(null)
    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()

    private val _phoneNumber = MutableLiveData("")
    val phoneNumber: LiveData<String>
        get() = _phoneNumber

    private val _financialId = MutableLiveData("")
    val financialId: LiveData<String>
        get() = _financialId

    private val _amount = MutableLiveData("")
    val amount: LiveData<String>
        get() = _amount

    private val _payerMessage = MutableLiveData("")
    val paymentMessage: LiveData<String>
        get() = _payerMessage

    private val _payerNote = MutableLiveData("")
    val paymentNote: LiveData<String>
        get() = _payerNote

    private val _deliveryNote = MutableLiveData("")
    val deliveryNote: LiveData<String>
        get() = _deliveryNote

    fun onPhoneNumberUpdated(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun onFinancialIdUpdated(financialId: String) {
        _financialId.value = financialId
    }

    fun onAmountUpdated(amount: String) {
        _amount.value = amount
    }

    fun onPayerMessageUpdated(payerMessage: String) {
        _payerMessage.value = payerMessage
    }

    fun onPayerNoteUpdated(payerNote: String) {
        _payerNote.value = payerNote
    }

    fun onDeliveryNoteUpdated(deliveryNote: String) {
        _deliveryNote.value = deliveryNote
    }

    fun transferRemittance() {
        showProgressBar.postValue(true)
        if (phoneNumber.value!!.isNotEmpty() && financialId.value!!.isNotEmpty() &&
            amount.value!!.isNotEmpty() && paymentMessage.value!!.isNotEmpty() &&
            paymentNote.value!!.isNotEmpty()
        ) {
            val accessToken = context?.let { Utils.getAccessToken(it) }
            val creditTransaction = createDebitTransaction()
            val transactionUuid = Settings.generateUUID()
            if (StringUtils.isNotBlank(accessToken)) {
                if (accessToken != null) {
                    momoAPi?.transfer(
                        accessToken,
                        creditTransaction,
                        BuildConfig.MOMO_API_VERSION_V1,
                        MomoConstants.ProductTypes.REMITTANCE,
                        Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                        transactionUuid
                    ) { momoAPIResult ->
                        when (momoAPIResult) {
                            is APIResult.Success -> {
                                if (deliveryNote.value!!.isNotEmpty()) {
                                    requestToPayDeliveryNotification(
                                        transactionUuid
                                    )
                                }

                                getTransferStatus(transactionUuid)
                                showProgressBar.postValue(false)
                                emitSnackBarState(
                                    SnackBarComponentConfiguration(
                                        message = "Transfer sent successfully"
                                    )
                                )
                            }
                            is APIResult.Failure -> {
                                val momoAPIException = momoAPIResult.APIException
                                showProgressBar.postValue(false)
                                emitSnackBarState(
                                    SnackBarComponentConfiguration(
                                        message = "${momoAPIException!!.message} Transfer not sent!"
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
    }

    private fun createDebitTransaction(): MomoTransaction {
        return MomoTransaction(
            amount.value!!.toString(),
            Constants.SANDBOX_CURRENCY,
            financialId.value!!.toString(),
            Settings.generateUUID(),
            AccountHolder(AccountHolderType.MSISDN.name, phoneNumber.value!!.toString()),
            null,
            paymentMessage.value!!.toString(),
            paymentNote.value!!.toString(),
            null,
            null
        )
    }

    private fun getTransferStatus(referenceId: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            if (accessToken != null) {
                momoAPi?.getTransferStatus(
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    MomoConstants.ProductTypes.REMITTANCE,
                    Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                    accessToken
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            val completeTransferStatusFetch =
                                Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), MomoTransaction::class.java)
                            momoTransaction = MutableLiveData(completeTransferStatusFetch)
                            showProgressBar.postValue(false)
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "Transfer status fetched successfully"
                                )
                            )
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                            showProgressBar.postValue(false)
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "${momoAPIException!!.message} Transfer status not fetched!"
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

    private fun requestToPayDeliveryNotification(referenceId: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        val momoNotification = MomoNotification(
            notificationMessage = deliveryNote.value!!.toString()
        )
        if (StringUtils.isNotBlank(accessToken) &&
            Settings.checkNotificationMessageLength(momoNotification.notificationMessage)
        ) {
            accessToken?.let {
                momoAPi?.requestToPayDeliveryNotification(
                    momoNotification,
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    MomoConstants.ProductTypes.REMITTANCE,
                    Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                    it
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is APIResult.Success -> {
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "Remittance delivery note sent successfully"
                                )
                            )
                        }
                        is APIResult.Failure -> {
                            val momoAPIException = momoAPIResult.APIException
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "${momoAPIException!!.message} Delivery note was not sent!"
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
