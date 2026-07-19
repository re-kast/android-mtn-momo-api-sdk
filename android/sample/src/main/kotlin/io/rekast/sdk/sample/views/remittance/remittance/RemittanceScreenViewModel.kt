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
package io.rekast.sdk.sample.views.remittance.remittance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.MomoTransaction
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
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * ViewModel for the Remittance Transfer screen.
 *
 * On submit it runs the full Remittance transfer flow against the SDK:
 * 1. `transfer` — sends money to the payee (HTTP 202).
 * 2. `getTransferStatus` — polls the outcome and posts it to [momoTransaction].
 *
 * The screen shows the input form while [momoTransaction] is null and the result once it is set.
 * Authentication is handled automatically by the SDK's interceptor/authenticator, so the ViewModel
 * only guards on the presence of an access token before starting.
 */
@HiltViewModel
class RemittanceScreenViewModel @Inject constructor(
    private val defaultRepository: DefaultRepository,
    private val credentialStorage: CredentialStorage,
    private val dispatchers: DispatcherProvider,
    private val sampleConfig: SampleConfig
) : ViewModel() {
    private val json = Json { ignoreUnknownKeys = true }

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

    /** The current transfer amount entered in the form. */
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
     * Submits a Remittance transfer, then polls the status and posts the resulting
     * [MomoTransaction] to [momoTransaction].
     */
    fun transferRemittance() {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Remittance transfer skipped: access token is blank")
                emitError("Expired access token! Please refresh the token")
                return@launch
            }
            showProgressBar.postValue(true)
            try {
                val referenceId = UUID.randomUUID().toString()
                val subscriptionKey = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)
                val submit = defaultRepository.transfer(
                    productType = ProductType.REMITTANCE.productType,
                    apiVersion = sampleConfig.apiVersionV1,
                    momoTransaction = buildTransaction(),
                    uuid = referenceId,
                    productSubscriptionKey = subscriptionKey,
                    environment = sampleConfig.environment
                ).awaitTerminal()
                when (submit) {
                    is NetworkResult.Success -> {
                        Timber.d("Remittance transfer accepted (ref=%s)", referenceId)
                        emitSuccess("Remittance transfer submitted successfully")
                        fetchStatus(referenceId, subscriptionKey)
                    }

                    is NetworkResult.Error -> {
                        Timber.e("Remittance transfer failed: %s", submit.message)
                        emitError("Remittance transfer was not sent. ${submit.message}")
                    }

                    is NetworkResult.Loading -> {}
                }
            } catch (exception: Exception) {
                Timber.e(exception, "Remittance transfer failed")
                emitError("Remittance transfer was not sent. ${exception.message}")
            } finally {
                showProgressBar.postValue(false)
            }
        }
    }

    /** Polls the transfer status and posts the decoded transaction to [momoTransaction]. */
    private suspend fun fetchStatus(referenceId: String, subscriptionKey: String) {
        val result = defaultRepository.getTransferStatus(
            productType = ProductType.REMITTANCE.productType,
            apiVersion = sampleConfig.apiVersionV1,
            referenceId = referenceId,
            productSubscriptionKey = subscriptionKey,
            environment = sampleConfig.environment
        ).awaitTerminal()
        when (result) {
            is NetworkResult.Success -> {
                val transaction = result.response?.source()?.readUtf8()?.let { body ->
                    runCatching { json.decodeFromString<MomoTransaction>(body) }.getOrNull()
                }
                momoTransaction.postValue(transaction)
                emitSuccess("Remittance transfer status fetched successfully")
            }

            is NetworkResult.Error -> {
                Timber.e("Remittance transfer status failed: %s", result.message)
                emitError("Remittance transfer status not fetched. ${result.message}")
            }

            is NetworkResult.Loading -> {}
        }
    }

    /** Builds the transfer payload from the current form values. */
    private fun buildTransaction() = MomoTransaction(
        amount = amount.value.orEmpty(),
        currency = Constants.SANDBOX_CURRENCY,
        financialTransactionId = financialId.value?.ifBlank { null },
        externalId = UUID.randomUUID().toString(),
        payee = AccountHolder(partyIdType = AccountHolderType.MSISDN.accountHolderType, partyId = phoneNumber.value.orEmpty()),
        payer = null,
        payerMessage = payerMessage.value.orEmpty(),
        payeeNote = payerNote.value.orEmpty(),
        status = null,
        reason = null,
        referenceIdToRefund = null
    )

    /**
     * Collects this result [Flow] to completion and returns its terminal (non-[NetworkResult.Loading])
     * emission, so a suspend caller can await the flow's final success or error.
     */
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
