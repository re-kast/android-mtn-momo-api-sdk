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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.CashTransfer
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.AccountHolderType
import io.rekast.sdk.utils.ProductType
import java.util.UUID
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
) : ViewModel() {

    /** Controls whether the circular progress indicator is shown. */
    val showProgressBar = MutableLiveData(false)

    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()

    /** Flow of snackbar events to be displayed. */
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()

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

    /** Sends a cash transfer with a fresh reference ID and stores that ID for the status action. */
    fun sendCashTransfer() = launchOperation {
        val reference = UUID.randomUUID().toString()
        val subscriptionKey = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
        val cashTransfer = CashTransfer(
            amount = amount.value.orEmpty(),
            currency = currency.value.orEmpty().ifBlank { Constants.SANDBOX_CURRENCY },
            externalId = UUID.randomUUID().toString(),
            payee = AccountHolder(partyIdType = AccountHolderType.MSISDN.accountHolderType, partyId = payeeMsisdn.value.orEmpty()),
            payerMessage = payerMessage.value.orEmpty(),
            payeeNote = payeeNote.value.orEmpty(),
            payerFirstName = payerFirstName.value?.ifBlank { null },
            payerSurName = payerSurName.value?.ifBlank { null }
        )
        when (val response = defaultRepository.cashTransfer(sampleConfig.apiVersionV1, cashTransfer, reference, subscriptionKey, sampleConfig.environment).awaitTerminal()) {
            is NetworkResult.Success -> {
                referenceId.postValue(reference)
                result.postValue("Cash transfer sent.\nReference: $reference")
                emitSuccess("Cash transfer sent successfully")
            }

            else -> {
                Timber.e("Cash transfer failed: %s", response.message)
                result.postValue("Send failed: ${response.message}")
                emitError("Cash transfer not sent. ${response.message}")
            }
        }
    }

    /** Fetches the status of the previously sent cash transfer and prints the raw payload. */
    fun checkStatus() = withReference { reference, subscriptionKey ->
        when (val response = defaultRepository.getCashTransferStatus(sampleConfig.apiVersionV1, reference, subscriptionKey, sampleConfig.environment).awaitTerminal()) {
            is NetworkResult.Success -> {
                result.postValue(response.response?.source()?.readUtf8().orEmpty().ifBlank { "No status body returned." })
                emitSuccess("Cash transfer status fetched successfully")
            }

            else -> {
                Timber.e("Cash transfer status failed: %s", response.message)
                result.postValue("Status failed: ${response.message}")
                emitError("Cash transfer status not fetched. ${response.message}")
            }
        }
    }

    /** Runs [block] inside a coroutine with a token guard, progress toggle, and error handling. */
    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Cash transfer operation skipped: access token is blank")
                emitError("Expired access token! Please refresh the token")
                return@launch
            }
            showProgressBar.postValue(true)
            try {
                block()
            } catch (exception: Exception) {
                Timber.e(exception, "Cash transfer operation failed")
                result.postValue("Error: ${exception.message}")
                emitError("Operation failed. ${exception.message}")
            } finally {
                showProgressBar.postValue(false)
            }
        }
    }

    /** Runs [block] with the stored reference ID, or shows an error when no cash transfer has been sent. */
    private fun withReference(block: suspend (String, String) -> Unit) {
        val reference = referenceId.value
        if (reference.isNullOrBlank()) {
            emitError("Send a cash transfer first")
            return
        }
        launchOperation { block(reference, Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)) }
    }

    private suspend fun <T> Flow<NetworkResult<T>>.awaitTerminal(): NetworkResult<T> {
        var terminal: NetworkResult<T> = NetworkResult.Error("No response received")
        collect { emission -> if (emission !is NetworkResult.Loading) terminal = emission }
        return terminal
    }

    private fun emitSuccess(message: String) = emitSnackBarState(SnackBarComponentConfiguration(message = message, type = SnackBarType.SUCCESS))

    private fun emitError(message: String) = emitSnackBarState(SnackBarComponentConfiguration(message = message, type = SnackBarType.ERROR))

    private fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }
}
