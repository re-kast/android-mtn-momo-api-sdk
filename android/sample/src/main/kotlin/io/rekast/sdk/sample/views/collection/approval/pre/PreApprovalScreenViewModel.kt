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
package io.rekast.sdk.sample.views.collection.approval.pre

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.Party
import io.rekast.sdk.model.PreApproval
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
import io.rekast.sdk.utils.ProductTypes
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the Collection Pre-Approval screen. Showcases `createPreApproval` →
 * `getPreApprovalStatus`, keyed on a locally generated reference ID. Cancellation lives on the
 * Approved Pre-Approvals screen, since it is keyed on the server-assigned `preApprovalId`.
 *
 * The last operation's outcome (reference ID, status payload, or error) is exposed via [result] and
 * rendered in a console panel so the flow is visible to developers exploring the SDK.
 */
@HiltViewModel
class PreApprovalScreenViewModel @Inject constructor(
    private val defaultRepository: DefaultRepository,
    private val credentialStorage: CredentialStorage,
    private val dispatchers: DispatcherProvider,
    private val sampleConfig: SampleConfig
) : BaseScreenViewModel() {

    /** The reference ID of the last created pre-approval; enables status/cancel actions. */
    val referenceId = MutableLiveData<String?>(null)

    /** Console output describing the outcome of the last operation. */
    val result = MutableLiveData<String?>(null)

    private val _payerMsisdn = MutableLiveData(Constants.EMPTY_STRING)
    val payerMsisdn: LiveData<String> get() = _payerMsisdn
    fun onPayerMsisdnChanged(value: String) {
        _payerMsisdn.value = value
    }

    private val _payerCurrency = MutableLiveData(Constants.SANDBOX_CURRENCY)
    val payerCurrency: LiveData<String> get() = _payerCurrency
    fun onPayerCurrencyChanged(value: String) {
        _payerCurrency.value = value
    }

    private val _payerMessage = MutableLiveData(Constants.EMPTY_STRING)
    val payerMessage: LiveData<String> get() = _payerMessage
    fun onPayerMessageChanged(value: String) {
        _payerMessage.value = value
    }

    private val _validityTime = MutableLiveData(Constants.EMPTY_STRING)
    val validityTime: LiveData<String> get() = _validityTime
    fun onValidityTimeChanged(value: String) {
        _validityTime.value = value
    }

    /** Creates a pre-approval with a fresh reference ID and stores that ID for status/cancel. */
    fun createPreApproval() = launchOperation {
        val reference = generateUuid()
        val subscriptionKey = Utils.getProductSubscriptionKeys(ProductTypes.COLLECTION, sampleConfig)
        val preApproval = PreApproval(
            payer = Party(partyIdType = PartyTypes.MSISDN, partyId = payerMsisdn.valueOrEmpty()),
            payerCurrency = payerCurrency.valueOrEmpty().ifBlank { Constants.SANDBOX_CURRENCY },
            payerMessage = payerMessage.valueOrNullIfBlank(),
            validityTime = validityTime.valueOrEmpty().toIntOrNull() ?: 0
        )
        when (val response = defaultRepository.createPreApproval(sampleConfig.apiVersionV2, preApproval, reference, subscriptionKey).awaitTerminal()) {
            is NetworkResult.Success -> {
                referenceId.postValue(reference)
                result.postValue("Pre-approval created.\nReference: $reference")
                emitSuccess(R.string.snackbar_preapproval_created)
            }

            else -> {
                Timber.e("Create pre-approval failed: %s", response.message)
                result.postValue("Create failed: ${response.message}")
                emitError(R.string.snackbar_preapproval_not_created, response.message)
            }
        }
    }

    /** Fetches the status of the previously created pre-approval and prints the raw payload. */
    fun checkStatus() = withReference { reference, subscriptionKey ->
        when (val response = defaultRepository.getPreApprovalStatus(sampleConfig.apiVersionV2, reference, subscriptionKey).awaitTerminal()) {
            is NetworkResult.Success -> {
                result.postValue(response.response?.toString() ?: "No status body returned.")
                emitSuccess(R.string.snackbar_preapproval_status_fetched)
            }

            else -> {
                Timber.e("Pre-approval status failed: %s", response.message)
                result.postValue("Status failed: ${response.message}")
                emitError(R.string.snackbar_preapproval_status_failed, response.message)
            }
        }
    }

    /** Runs [block] inside a coroutine with a token guard, progress toggle, and error handling. */
    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Pre-approval operation skipped: access token is blank")
                emitError(R.string.snackbar_token_expired)
                return@launch
            }
            showProgressBar.postValue(true)
            try {
                block()
            } catch (exception: Exception) {
                Timber.e(exception, "Pre-approval operation failed")
                result.postValue("Error: ${exception.message}")
                emitError(R.string.snackbar_operation_failed, exception.message.orEmpty())
            } finally {
                showProgressBar.postValue(false)
            }
        }
    }

    /** Runs [block] with the stored reference ID, or shows an error when no pre-approval has been created. */
    private fun withReference(block: suspend (String, String) -> Unit) {
        val reference = referenceId.value
        if (reference.isNullOrBlank()) {
            emitError(R.string.snackbar_preapproval_required_first)
            return
        }
        launchOperation { block(reference, Utils.getProductSubscriptionKeys(ProductTypes.COLLECTION, sampleConfig)) }
    }
}
