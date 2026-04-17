/*
 * Copyright 2023-2026, Benjamin Mwalimu
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
package io.rekast.sdk.sample.views.collection.withdraw

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Collection Request-to-Withdraw screen, managing form field state and the
 * resulting [MomoTransaction] after a withdrawal request.
 */
@HiltViewModel
class CollectionWithdrawScreenViewModel @Inject constructor(private val defaultRepository: DefaultRepository, @param:ApplicationContext private val context: Context, private val sampleConfig: SampleConfig) : ViewModel() {
    /** Controls whether the circular progress indicator is shown instead of the form. */
    val showProgressBar = MutableLiveData(false)

    /** Holds the completed [MomoTransaction] returned by the API; null while no request has succeeded. */
    var momoTransaction: MutableLiveData<MomoTransaction?> = MutableLiveData(null)
    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()

    /** Flow of [SnackBarComponentConfiguration] events to be displayed as snackbars. */
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()

    private val _phoneNumber = MutableLiveData(Constants.EMPTY_STRING)

    /** The current phone number entered in the form. */
    val phoneNumber: LiveData<String>
        get() = _phoneNumber

    private val _financialId = MutableLiveData(Constants.EMPTY_STRING)

    /** The current financial ID entered in the form. */
    val financialId: LiveData<String>
        get() = _financialId

    private val _referenceIdToRefund = MutableLiveData(Constants.EMPTY_STRING)

    /** The current reference ID to refund entered in the form. */
    val referenceIdToRefund: LiveData<String>
        get() = _referenceIdToRefund

    private val _amount = MutableLiveData(Constants.EMPTY_STRING)

    /** The current payment amount entered in the form. */
    val amount: LiveData<String>
        get() = _amount

    private val _payerMessage = MutableLiveData(Constants.EMPTY_STRING)

    /** The current payer message entered in the form. */
    val payerMessage: LiveData<String>
        get() = _payerMessage

    private val _payerNote = MutableLiveData(Constants.EMPTY_STRING)

    /** The current payer note entered in the form. */
    val payerNote: LiveData<String>
        get() = _payerNote

    private val _deliveryNote = MutableLiveData(Constants.EMPTY_STRING)

    /** The current delivery note entered in the form. */
    val deliveryNote: LiveData<String>
        get() = _deliveryNote

    /**
     * Updates the phone number field value.
     *
     * @param phoneNumber The new phone number string.
     */
    fun onPhoneNumberUpdated(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    /**
     * Updates the financial ID field value.
     *
     * @param financialId The new financial ID string.
     */
    fun onFinancialIdUpdated(financialId: String) {
        _financialId.value = financialId
    }

    /**
     * Updates the amount field value.
     *
     * @param amount The new amount string.
     */
    fun onAmountUpdated(amount: String) {
        _amount.value = amount
    }

    /**
     * Updates the payer message field value.
     *
     * @param payerMessage The new payer message string.
     */
    fun onPayerMessageUpdated(payerMessage: String) {
        _payerMessage.value = payerMessage
    }

    /**
     * Updates the payer note field value.
     *
     * @param payerNote The new payer note string.
     */
    fun onPayerNoteUpdated(payerNote: String) {
        _payerNote.value = payerNote
    }

    /**
     * Updates the delivery note field value.
     *
     * @param deliveryNote The new delivery note string.
     */
    fun onDeliveryNoteUpdated(deliveryNote: String) {
        _deliveryNote.value = deliveryNote
    }

    /**
     * Updates the reference ID to refund field value.
     *
     * @param deliveryNote The new reference ID to refund string.
     */
    fun onReferenceIdToRefundUpdated(deliveryNote: String) {
        _referenceIdToRefund.value = deliveryNote
    }

/*    fun requestToWithdraw() {
        showProgressBar.postValue(true)
        if (phoneNumber.value!!.isNotEmpty() && financialId.value!!.isNotEmpty() &&
            amount.value!!.isNotEmpty() && payerMessage.value!!.isNotEmpty() &&
            payerNote.value!!.isNotEmpty()
        ) {
            val accessToken = context?.let { Utils.getAccessToken(it) }
            val creditTransaction = createRequestToWithdrawTransaction()
            val transactionUuid = Settings().generateUUID()
            if (StringUtils.isNotBlank(accessToken)) {
                accessToken?.let {
                    momoAPi?.requestToWithdraw(
                        it,
                        creditTransaction,
                        BuildConfig.MOMO_API_VERSION_V2,
                        Settings().getProductSubscriptionKeys(ProductType.COLLECTION),
                        transactionUuid
                    ) { momoAPIResult ->
                        when (momoAPIResult) {
                            is MomoResponse.Success -> {
                                requestToPayDeliveryNotification(
                                    transactionUuid
                                )
                                requestToWithdrawTransactionStatus(transactionUuid)
                                showProgressBar.postValue(false)
                                emitSnackBarState(
                                    SnackBarComponentConfiguration(
                                        message = "Request to withdraw sent successfully"
                                    )
                                )
                            }
                            is MomoResponse.Failure -> {
                                val momoAPIException = momoAPIResult.momoException
                                showProgressBar.postValue(false)
                                emitSnackBarState(
                                    SnackBarComponentConfiguration(
                                        message = "${momoAPIException!!.message} Request to withdraw wasn't sent!"
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

    private fun requestToWithdrawTransactionStatus(referenceId: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        if (StringUtils.isNotBlank(accessToken)) {
            accessToken?.let {
                momoAPi?.requestToWithdrawTransactionStatus(
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    Settings().getProductSubscriptionKeys(ProductType.COLLECTION),
                    it
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is MomoResponse.Success -> {
                            val requestToWithdrawStatusFetch =
                                Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), MomoTransaction::class.java)
                            momoTransaction = MutableLiveData(requestToWithdrawStatusFetch)
                            showProgressBar.postValue(false)
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "Request to withdraw status fetched successfully"
                                )
                            )
                        }
                        is MomoResponse.Failure -> {
                            showProgressBar.postValue(false)
                            val momoAPIException = momoAPIResult.momoException
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "${momoAPIException!!.message} Request to withdraw status not fetch!"
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

    private fun createRequestToWithdrawTransaction(): MomoTransaction {
        return MomoTransaction(
            amount.value!!.toString(),
            Constants.SANDBOX_CURRENCY,
            financialId.value!!.toString(),
            RandomStringUtils.randomAlphanumeric(Constants.STRING_LENGTH),
            null,
            AccountHolder(AccountHolderType.MSISDN.name, phoneNumber.value!!.toString()),
            payerMessage.value!!.toString(),
            payerNote.value!!.toString(),
            null,
            null
        )
    }

    private fun requestToPayDeliveryNotification(referenceId: String) {
        val accessToken = context?.let { Utils.getAccessToken(it) }
        val momoNotification = MomoNotification(
            notificationMessage = deliveryNote.value!!.toString()
        )
        if (StringUtils.isNotBlank(accessToken) &&
            Settings().checkNotificationMessageLength(momoNotification.notificationMessage)
        ) {
            accessToken?.let {
                momoAPi?.requestToPayDeliveryNotification(
                    momoNotification,
                    referenceId,
                    BuildConfig.MOMO_API_VERSION_V1,
                    ProductType.COLLECTION.productType,
                    Settings().getProductSubscriptionKeys(ProductType.COLLECTION),
                    it
                ) { momoAPIResult ->
                    when (momoAPIResult) {
                        is MomoResponse.Success -> {
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "Request to pay delivery note sent successfully"
                                )
                            )
                        }
                        is MomoResponse.Failure -> {
                            val momoAPIException = momoAPIResult.momoException
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
    }*/

    private fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }
}
