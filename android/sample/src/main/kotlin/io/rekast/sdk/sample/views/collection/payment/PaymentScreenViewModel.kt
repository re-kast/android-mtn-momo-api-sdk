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
package io.rekast.sdk.sample.views.collection.payment

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.Money
import io.rekast.sdk.model.Payment
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
import io.rekast.sdk.utils.ProductTypes
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the Collection Payment (V2) screen. Showcases `createPayment` → `getPaymentStatus`
 * against the SDK, keyed on a locally generated reference ID.
 *
 * The last operation's outcome (reference ID, status payload, or error) is exposed via [result] and
 * rendered in a console panel so the flow is visible to developers exploring the SDK.
 */
@HiltViewModel
class PaymentScreenViewModel @Inject constructor(
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

    /** The reference ID of the last created payment; enables the status action. */
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

    private val _customerReference = MutableLiveData(Constants.EMPTY_STRING)
    val customerReference: LiveData<String> get() = _customerReference
    fun onCustomerReferenceChanged(value: String) {
        _customerReference.value = value
    }

    private val _receiverMessage = MutableLiveData(Constants.EMPTY_STRING)
    val receiverMessage: LiveData<String> get() = _receiverMessage
    fun onReceiverMessageChanged(value: String) {
        _receiverMessage.value = value
    }

    private val _senderNote = MutableLiveData(Constants.EMPTY_STRING)
    val senderNote: LiveData<String> get() = _senderNote
    fun onSenderNoteChanged(value: String) {
        _senderNote.value = value
    }

    /** Creates a payment with a fresh reference ID and stores that ID for the status action. */
    fun createPayment() = launchOperation {
        val reference = UUID.randomUUID().toString()
        val subscriptionKey = Utils.getProductSubscriptionKeys(ProductTypes.COLLECTION, sampleConfig)
        val payment = Payment(
            externalTransactionId = UUID.randomUUID().toString(),
            money = Money(
                amount = amount.valueOrEmpty(),
                currency = currency.valueOrEmpty().ifBlank { Constants.SANDBOX_CURRENCY }
            ),
            customerReference = customerReference.valueOrNullIfBlank(),
            receiverMessage = receiverMessage.valueOrNullIfBlank(),
            senderNote = senderNote.valueOrNullIfBlank()
        )
        when (val response = defaultRepository.createPayment(sampleConfig.apiVersionV2, payment, reference, subscriptionKey, sampleConfig.environment).awaitTerminal()) {
            is NetworkResult.Success -> {
                referenceId.postValue(reference)
                result.postValue("Payment created.\nReference: $reference")
                emitSuccess(R.string.snackbar_payment_created)
            }

            else -> {
                Timber.e("Create payment failed: %s", response.message)
                result.postValue("Create failed: ${response.message}")
                emitError(R.string.snackbar_payment_not_created, response.message)
            }
        }
    }

    /** Fetches the status of the previously created payment and prints the raw payload. */
    fun checkStatus() = withReference { reference, subscriptionKey ->
        when (val response = defaultRepository.getPaymentStatus(sampleConfig.apiVersionV2, reference, subscriptionKey, sampleConfig.environment).awaitTerminal()) {
            is NetworkResult.Success -> {
                result.postValue(response.response?.toString() ?: "No status body returned.")
                emitSuccess(R.string.snackbar_payment_status_fetched)
            }

            else -> {
                Timber.e("Payment status failed: %s", response.message)
                result.postValue("Status failed: ${response.message}")
                emitError(R.string.snackbar_payment_status_failed, response.message)
            }
        }
    }

    /** Runs [block] inside a coroutine with a token guard, progress toggle, and error handling. */
    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Payment operation skipped: access token is blank")
                emitError(R.string.snackbar_token_expired)
                return@launch
            }
            showProgressBar.postValue(true)
            try {
                block()
            } catch (exception: Exception) {
                Timber.e(exception, "Payment operation failed")
                result.postValue("Error: ${exception.message}")
                emitError(R.string.snackbar_operation_failed, exception.message.orEmpty())
            } finally {
                showProgressBar.postValue(false)
            }
        }
    }

    /** Runs [block] with the stored reference ID, or shows an error when no payment has been created. */
    private fun withReference(block: suspend (String, String) -> Unit) {
        val reference = referenceId.value
        if (reference.isNullOrBlank()) {
            emitError(R.string.snackbar_payment_required_first)
            return
        }
        launchOperation { block(reference, Utils.getProductSubscriptionKeys(ProductTypes.COLLECTION, sampleConfig)) }
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
