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
package io.rekast.sdk.sample.views.collection.invoice

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.Invoice
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
import io.rekast.sdk.sample.utils.bodyText
import io.rekast.sdk.sample.utils.valueOrEmpty
import io.rekast.sdk.sample.utils.valueOrNullIfBlank
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
 * ViewModel for the Collection Invoice screen. Showcases the full invoice lifecycle against the SDK:
 * `createInvoice` → `getInvoiceStatus` → `cancelInvoice`, keyed on a locally generated reference ID.
 *
 * The last operation's outcome (reference ID, status payload, or error) is exposed via [result] and
 * rendered in a console panel so the flow is visible to developers exploring the SDK.
 */
@HiltViewModel
class InvoiceScreenViewModel @Inject constructor(
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

    /** The reference ID of the last created invoice; enables status/cancel actions. */
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

    private val _payerMsisdn = MutableLiveData(Constants.EMPTY_STRING)
    val payerMsisdn: LiveData<String> get() = _payerMsisdn
    fun onPayerMsisdnChanged(value: String) {
        _payerMsisdn.value = value
    }

    private val _validityDuration = MutableLiveData(Constants.EMPTY_STRING)
    val validityDuration: LiveData<String> get() = _validityDuration
    fun onValidityDurationChanged(value: String) {
        _validityDuration.value = value
    }

    private val _description = MutableLiveData(Constants.EMPTY_STRING)
    val description: LiveData<String> get() = _description
    fun onDescriptionChanged(value: String) {
        _description.value = value
    }

    /** Creates an invoice with a fresh reference ID and stores that ID for status/cancel. */
    fun createInvoice() = launchOperation {
        val reference = UUID.randomUUID().toString()
        val subscriptionKey = Utils.getProductSubscriptionKeys(ProductType.COLLECTION, sampleConfig)
        val invoice = Invoice(
            externalId = UUID.randomUUID().toString(),
            amount = amount.valueOrEmpty(),
            currency = currency.valueOrEmpty().ifBlank { Constants.SANDBOX_CURRENCY },
            validityDuration = validityDuration.valueOrNullIfBlank(),
            intendedPayer = AccountHolder(partyIdType = AccountHolderType.MSISDN.accountHolderType, partyId = payerMsisdn.valueOrEmpty()),
            payerMessage = null,
            payeeNote = null,
            description = description.valueOrNullIfBlank()
        )
        when (val response = defaultRepository.createInvoice(sampleConfig.apiVersionV1, invoice, reference, subscriptionKey, sampleConfig.environment).awaitTerminal()) {
            is NetworkResult.Success -> {
                referenceId.postValue(reference)
                result.postValue("Invoice created.\nReference: $reference")
                emitSuccess(R.string.snackbar_invoice_created)
            }

            else -> {
                Timber.e("Create invoice failed: %s", response.message)
                result.postValue("Create failed: ${response.message}")
                emitError(R.string.snackbar_invoice_not_created, response.message)
            }
        }
    }

    /** Fetches the status of the previously created invoice and prints the raw payload. */
    fun checkStatus() = withReference { reference, subscriptionKey ->
        when (val response = defaultRepository.getInvoiceStatus(sampleConfig.apiVersionV1, reference, subscriptionKey, sampleConfig.environment).awaitTerminal()) {
            is NetworkResult.Success -> {
                result.postValue(response.bodyText().orEmpty().ifBlank { "No status body returned." })
                emitSuccess(R.string.snackbar_invoice_status_fetched)
            }

            else -> {
                Timber.e("Invoice status failed: %s", response.message)
                result.postValue("Status failed: ${response.message}")
                emitError(R.string.snackbar_invoice_status_failed, response.message)
            }
        }
    }

    /** Cancels the previously created invoice. */
    fun cancelInvoice() = withReference { reference, subscriptionKey ->
        when (val response = defaultRepository.cancelInvoice(sampleConfig.apiVersionV1, reference, subscriptionKey, sampleConfig.environment).awaitTerminal()) {
            is NetworkResult.Success -> {
                result.postValue("Invoice $reference cancelled.")
                emitSuccess(R.string.snackbar_invoice_cancelled)
            }

            else -> {
                Timber.e("Cancel invoice failed: %s", response.message)
                result.postValue("Cancel failed: ${response.message}")
                emitError(R.string.snackbar_invoice_not_cancelled, response.message)
            }
        }
    }

    /** Runs [block] inside a coroutine with a token guard, progress toggle, and error handling. */
    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Invoice operation skipped: access token is blank")
                emitError(R.string.snackbar_token_expired)
                return@launch
            }
            showProgressBar.postValue(true)
            try {
                block()
            } catch (exception: Exception) {
                Timber.e(exception, "Invoice operation failed")
                result.postValue("Error: ${exception.message}")
                emitError(R.string.snackbar_operation_failed, exception.message.orEmpty())
            } finally {
                showProgressBar.postValue(false)
            }
        }
    }

    /** Runs [block] with the stored reference ID, or shows an error when no invoice has been created. */
    private fun withReference(block: suspend (String, String) -> Unit) {
        val reference = referenceId.value
        if (reference.isNullOrBlank()) {
            emitError(R.string.snackbar_invoice_required_first)
            return
        }
        launchOperation { block(reference, Utils.getProductSubscriptionKeys(ProductType.COLLECTION, sampleConfig)) }
    }

    private suspend fun <T> Flow<NetworkResult<T>>.awaitTerminal(): NetworkResult<T> {
        var terminal: NetworkResult<T> = NetworkResult.Error("No response received")
        collect { emission -> if (emission !is NetworkResult.Loading) terminal = emission }
        return terminal
    }

    private fun emitSuccess(@StringRes messageResId: Int, vararg args: Any) = emitSnackBarState(SnackBarComponentConfiguration(messageResId = messageResId, messageArgs = args.toList(), type = SnackBarType.SUCCESS))

    private fun emitError(@StringRes messageResId: Int, vararg args: Any) = emitSnackBarState(SnackBarComponentConfiguration(messageResId = messageResId, messageArgs = args.toList(), type = SnackBarType.ERROR))

    private fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }
}
