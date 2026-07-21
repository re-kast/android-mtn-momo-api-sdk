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
package io.rekast.sdk.sample.views.disbursement.deposit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.Deposit
import io.rekast.sdk.model.DepositStatus
import io.rekast.sdk.model.Party
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.sample.utils.valueOrEmpty
import io.rekast.sdk.sample.views.BaseScreenViewModel
import io.rekast.sdk.utils.PartyTypes
import io.rekast.sdk.utils.ProductTypes
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the Disbursement Deposit screen.
 *
 * On submit it runs the full Disbursement deposit flow against the SDK:
 * 1. `deposit` — sends money to the payee's wallet (HTTP 202).
 * 2. `getDepositStatus` — polls the outcome and posts it to [depositStatus].
 *
 * The screen shows the input form while [depositStatus] is null and the result once it is set.
 * Authentication is handled automatically by the SDK's interceptor/authenticator, so the ViewModel
 * only guards on the presence of an access token before starting.
 */
@HiltViewModel
class DisbursementDepositScreenViewModel @Inject constructor(
    private val defaultRepository: DefaultRepository,
    private val credentialStorage: CredentialStorage,
    private val dispatchers: DispatcherProvider,
    private val sampleConfig: SampleConfig
) : BaseScreenViewModel() {

    /** Holds the [DepositStatus] returned by the API; null while no request has succeeded. */
    var depositStatus: MutableLiveData<DepositStatus?> = MutableLiveData(null)

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
     * @param referenceIdToRefund The new reference ID to refund string.
     */
    fun onReferenceIdToRefundUpdated(referenceIdToRefund: String) {
        _referenceIdToRefund.value = referenceIdToRefund
    }

    /**
     * Submits a Disbursement deposit, then polls the status and posts the resulting
     * [DepositStatus] to [depositStatus].
     */
    fun deposit() {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Deposit skipped: access token is blank")
                emitError(R.string.snackbar_token_expired)
                return@launch
            }
            showProgressBar.postValue(true)
            try {
                val referenceId = generateUuid()
                val subscriptionKey = Utils.getProductSubscriptionKeys(ProductTypes.DISBURSEMENTS, sampleConfig)
                val submit = defaultRepository.deposit(
                    deposit = buildDeposit(),
                    apiVersion = sampleConfig.apiVersionV1,
                    productSubscriptionKey = subscriptionKey,
                    uuid = referenceId
                ).awaitTerminal()
                when (submit) {
                    is NetworkResult.Success -> {
                        Timber.d("Deposit accepted (ref=%s)", referenceId)
                        emitSuccess(R.string.snackbar_deposit_submitted)
                        fetchStatus(referenceId, subscriptionKey)
                    }

                    else -> {
                        Timber.e("Deposit failed: %s", submit.message)
                        emitError(R.string.snackbar_deposit_failed, submit.message)
                    }
                }
            } catch (exception: Exception) {
                Timber.e(exception, "Deposit failed")
                emitError(R.string.snackbar_deposit_failed, exception.message.orEmpty())
            } finally {
                showProgressBar.postValue(false)
            }
        }
    }

    /** Polls the deposit status and posts the decoded transaction to [depositStatus]. */
    private suspend fun fetchStatus(referenceId: String, subscriptionKey: String) {
        val result = defaultRepository.getDepositStatus(
            referenceId = referenceId,
            apiVersion = sampleConfig.apiVersionV1,
            productSubscriptionKey = subscriptionKey
        ).awaitTerminal()
        when (result) {
            is NetworkResult.Success -> {
                depositStatus.postValue(result.response)
                emitSuccess(R.string.snackbar_deposit_status_fetched)
            }

            else -> {
                Timber.e("Deposit status failed: %s", result.message)
                emitError(R.string.snackbar_deposit_status_failed, result.message)
            }
        }
    }

    /** Builds the deposit payload from the current form values. */
    private fun buildDeposit() = Deposit(
        amount = amount.valueOrEmpty(),
        currency = Constants.SANDBOX_CURRENCY,
        externalId = generateUuid(),
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = phoneNumber.valueOrEmpty()),
        payerMessage = payerMessage.valueOrEmpty(),
        payeeNote = payerNote.valueOrEmpty()
    )
}
