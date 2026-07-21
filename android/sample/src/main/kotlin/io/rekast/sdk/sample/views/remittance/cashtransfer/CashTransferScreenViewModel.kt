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
package io.rekast.sdk.sample.views.remittance.cashtransfer

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.CashTransfer
import io.rekast.sdk.model.Party
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.sample.utils.valueOrEmpty
import io.rekast.sdk.sample.utils.valueOrNullIfBlank
import io.rekast.sdk.sample.views.BaseScreenViewModel
import io.rekast.sdk.utils.PartyTypes
import io.rekast.sdk.utils.PayerIdentificationType
import io.rekast.sdk.utils.ProductTypes
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the Remittance Cash Transfer (V2) screen. Showcases the cash transfer lifecycle
 * against the SDK: `cashTransfer` → `getCashTransferStatus`, keyed on a locally generated reference ID.
 *
 * The last operation's outcome (reference ID, status payload, or error) is exposed via [result] and
 * rendered in a console panel so the flow is visible to developers exploring the SDK.
 */
@HiltViewModel
class CashTransferScreenViewModel @Inject constructor(
    private val defaultRepository: DefaultRepository,
    private val credentialStorage: CredentialStorage,
    private val dispatchers: DispatcherProvider,
    private val sampleConfig: SampleConfig
) : BaseScreenViewModel() {

    /** The reference ID of the last cash transfer; enables the status action. */
    val referenceId = MutableLiveData<String?>(null)

    /** Console output describing the outcome of the last operation. */
    val result = MutableLiveData<String?>(null)

    private val _amount = MutableLiveData(Constants.EMPTY_STRING)
    val amount: LiveData<String> get() = _amount
    fun onAmountChanged(value: String) {
        _amount.value = value
    }

    private val _currency = MutableLiveData(Constants.SANDBOX_CURRENCY)
    val currency: LiveData<String> get() = _currency
    fun onCurrencyChanged(value: String) {
        _currency.value = value
    }

    private val _payeeMsisdn = MutableLiveData(Constants.EMPTY_STRING)
    val payeeMsisdn: LiveData<String> get() = _payeeMsisdn
    fun onPayeeMsisdnChanged(value: String) {
        _payeeMsisdn.value = value
    }

    private val _payerMessage = MutableLiveData(Constants.EMPTY_STRING)
    val payerMessage: LiveData<String> get() = _payerMessage
    fun onPayerMessageChanged(value: String) {
        _payerMessage.value = value
    }

    private val _payeeNote = MutableLiveData(Constants.EMPTY_STRING)
    val payeeNote: LiveData<String> get() = _payeeNote
    fun onPayeeNoteChanged(value: String) {
        _payeeNote.value = value
    }

    private val _payerFirstName = MutableLiveData(Constants.EMPTY_STRING)
    val payerFirstName: LiveData<String> get() = _payerFirstName
    fun onPayerFirstNameChanged(value: String) {
        _payerFirstName.value = value
    }

    private val _payerSurName = MutableLiveData(Constants.EMPTY_STRING)
    val payerSurName: LiveData<String> get() = _payerSurName
    fun onPayerSurNameChanged(value: String) {
        _payerSurName.value = value
    }

    private val _payerIdentificationType = MutableLiveData(Constants.EMPTY_STRING)
    val payerIdentificationType: LiveData<String> get() = _payerIdentificationType
    fun onPayerIdentificationTypeChanged(value: String) {
        _payerIdentificationType.value = value
    }

    private val _payerIdentificationNumber = MutableLiveData(Constants.EMPTY_STRING)
    val payerIdentificationNumber: LiveData<String> get() = _payerIdentificationNumber
    fun onPayerIdentificationNumberChanged(value: String) {
        _payerIdentificationNumber.value = value
    }

    private val _payerIdentity = MutableLiveData(Constants.EMPTY_STRING)
    val payerIdentity: LiveData<String> get() = _payerIdentity
    fun onPayerIdentityChanged(value: String) {
        _payerIdentity.value = value
    }

    private val _payerLanguageCode = MutableLiveData(Constants.EMPTY_STRING)
    val payerLanguageCode: LiveData<String> get() = _payerLanguageCode
    fun onPayerLanguageCodeChanged(value: String) {
        _payerLanguageCode.value = value
    }

    private val _payerEmail = MutableLiveData(Constants.EMPTY_STRING)
    val payerEmail: LiveData<String> get() = _payerEmail
    fun onPayerEmailChanged(value: String) {
        _payerEmail.value = value
    }

    private val _payerMsisdn = MutableLiveData(Constants.EMPTY_STRING)
    val payerMsisdn: LiveData<String> get() = _payerMsisdn
    fun onPayerMsisdnChanged(value: String) {
        _payerMsisdn.value = value
    }

    private val _payerGender = MutableLiveData(Constants.EMPTY_STRING)
    val payerGender: LiveData<String> get() = _payerGender
    fun onPayerGenderChanged(value: String) {
        _payerGender.value = value
    }

    private val _originatingCountry = MutableLiveData(Constants.EMPTY_STRING)
    val originatingCountry: LiveData<String> get() = _originatingCountry
    fun onOriginatingCountryChanged(value: String) {
        _originatingCountry.value = value
    }

    private val _originalAmount = MutableLiveData(Constants.EMPTY_STRING)
    val originalAmount: LiveData<String> get() = _originalAmount
    fun onOriginalAmountChanged(value: String) {
        _originalAmount.value = value
    }

    private val _originalCurrency = MutableLiveData(Constants.EMPTY_STRING)
    val originalCurrency: LiveData<String> get() = _originalCurrency
    fun onOriginalCurrencyChanged(value: String) {
        _originalCurrency.value = value
    }

    /**
     * Parses the free-text payer ID type into a [PayerIdentificationType], or `null` when blank or not
     * a recognised constant (e.g. `pass` → [PayerIdentificationType.PASS]).
     */
    private fun parsePayerIdentificationType(): PayerIdentificationType? = payerIdentificationType.valueOrNullIfBlank()?.let { raw ->
        runCatching { PayerIdentificationType.valueOf(raw.trim().uppercase()) }.getOrNull()
    }

    /** Sends a cash transfer with a fresh reference ID and stores that ID for the status action. */
    fun sendCashTransfer() = launchOperation {
        val reference = generateUuid()
        val subscriptionKey = Utils.getProductSubscriptionKeys(ProductTypes.REMITTANCE, sampleConfig)
        val cashTransfer = CashTransfer(
            amount = amount.valueOrEmpty(),
            currency = currency.valueOrEmpty().ifBlank { Constants.SANDBOX_CURRENCY },
            externalId = generateUuid(),
            payee = Party(partyIdType = PartyTypes.MSISDN, partyId = payeeMsisdn.valueOrEmpty()),
            payerMessage = payerMessage.valueOrEmpty(),
            payeeNote = payeeNote.valueOrEmpty(),
            payerIdentificationType = parsePayerIdentificationType(),
            payerIdentificationNumber = payerIdentificationNumber.valueOrNullIfBlank(),
            payerIdentity = payerIdentity.valueOrNullIfBlank(),
            payerFirstName = payerFirstName.valueOrNullIfBlank(),
            payerSurName = payerSurName.valueOrNullIfBlank(),
            payerLanguageCode = payerLanguageCode.valueOrNullIfBlank(),
            payerEmail = payerEmail.valueOrNullIfBlank(),
            payerMsisdn = payerMsisdn.valueOrNullIfBlank(),
            payerGender = payerGender.valueOrNullIfBlank(),
            originatingCountry = originatingCountry.valueOrNullIfBlank(),
            originalAmount = originalAmount.valueOrNullIfBlank(),
            originalCurrency = originalCurrency.valueOrNullIfBlank()
        )
        when (val response = defaultRepository.cashTransfer(sampleConfig.apiVersionV2, cashTransfer, reference, subscriptionKey).awaitTerminal()) {
            is NetworkResult.Success -> {
                referenceId.postValue(reference)
                result.postValue("Cash transfer sent.\nReference: $reference")
                emitSuccess(R.string.snackbar_cash_transfer_sent)
            }

            else -> {
                Timber.e("Cash transfer failed: %s", response.message)
                result.postValue("Send failed: ${response.message}")
                emitError(R.string.snackbar_cash_transfer_failed, response.message)
            }
        }
    }

    /** Fetches the status of the previously sent cash transfer and prints the raw payload. */
    fun checkStatus() = withReference { reference, subscriptionKey ->
        when (val response = defaultRepository.getCashTransferStatus(sampleConfig.apiVersionV2, reference, subscriptionKey).awaitTerminal()) {
            is NetworkResult.Success -> {
                result.postValue(response.response?.toString() ?: "No status body returned.")
                emitSuccess(R.string.snackbar_cash_transfer_status_fetched)
            }

            else -> {
                Timber.e("Cash transfer status failed: %s", response.message)
                result.postValue("Status failed: ${response.message}")
                emitError(R.string.snackbar_cash_transfer_status_failed, response.message)
            }
        }
    }

    /** Runs [block] inside a coroutine with a token guard, progress toggle, and error handling. */
    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Cash transfer operation skipped: access token is blank")
                emitError(R.string.snackbar_token_expired)
                return@launch
            }
            showProgressBar.postValue(true)
            try {
                block()
            } catch (exception: Exception) {
                Timber.e(exception, "Cash transfer operation failed")
                result.postValue("Error: ${exception.message}")
                emitError(R.string.snackbar_operation_failed, exception.message.orEmpty())
            } finally {
                showProgressBar.postValue(false)
            }
        }
    }

    /** Runs [block] with the stored reference ID, or shows an error when no cash transfer has been sent. */
    private fun withReference(block: suspend (String, String) -> Unit) {
        val reference = referenceId.value
        if (reference.isNullOrBlank()) {
            emitError(R.string.snackbar_cash_transfer_required_first)
            return
        }
        launchOperation { block(reference, Utils.getProductSubscriptionKeys(ProductTypes.REMITTANCE, sampleConfig)) }
    }
}
