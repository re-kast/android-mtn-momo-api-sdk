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
package io.rekast.sdk.sample.views.collection.approval.approved

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.PreApprovalDetails
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
 * ViewModel for the Collection Approved Pre-Approvals screen. Fetches an account holder's approved
 * pre-approvals via the `getApprovedPreApprovals` query and exposes them as a list so each
 * one can be cancelled individually by its own `preApprovalId` via `cancelPreApproval`.
 */
@HiltViewModel
class ApprovedPreApprovalsScreenViewModel @Inject constructor(
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

    /** Console output describing the outcome of the last operation. */
    val result = MutableLiveData<String?>(null)

    private val _approvals = MutableLiveData<List<PreApprovalDetails>>(emptyList())

    /** The approved pre-approvals fetched for the current account holder; one cancellable card each. */
    val approvals: LiveData<List<PreApprovalDetails>> get() = _approvals

    private val _accountHolderId = MutableLiveData(Constants.EMPTY_STRING)
    val accountHolderId: LiveData<String> get() = _accountHolderId
    fun onAccountHolderIdChanged(value: String) {
        _accountHolderId.value = value
    }

    /** Fetches the approved pre-approvals for the entered account holder and populates [approvals]. */
    fun getApprovedPreApprovals() = launchOperation {
        val subscriptionKey = Utils.getProductSubscriptionKeys(ProductTypes.COLLECTION, sampleConfig)
        when (
            val response = defaultRepository.getApprovedPreApprovals(
                sampleConfig.apiVersionV1,
                PartyTypes.MSISDN.partyType,
                accountHolderId.valueOrEmpty(),
                subscriptionKey,
                sampleConfig.environment
            ).awaitTerminal()
        ) {
            is NetworkResult.Success -> {
                val list = response.response?.preApprovalDetails.orEmpty()
                _approvals.postValue(list)
                result.postValue(if (list.isEmpty()) "No approved pre-approvals returned." else "${list.size} approved pre-approval(s) found.")
                emitSuccess(R.string.snackbar_approved_preapprovals_fetched)
            }

            else -> {
                Timber.e("Approved pre-approvals failed: %s", response.message)
                _approvals.postValue(emptyList())
                result.postValue("Fetch failed: ${response.message}")
                emitError(R.string.snackbar_approved_preapprovals_failed, response.message)
            }
        }
    }

    /** Cancels a single pre-approval by its [preApprovalId] and drops it from [approvals] on success. */
    fun cancelPreApproval(preApprovalId: String) = launchOperation {
        val subscriptionKey = Utils.getProductSubscriptionKeys(ProductTypes.COLLECTION, sampleConfig)
        when (val response = defaultRepository.cancelPreApproval(sampleConfig.apiVersionV1, preApprovalId, subscriptionKey, sampleConfig.environment).awaitTerminal()) {
            is NetworkResult.Success -> {
                _approvals.postValue(_approvals.value.orEmpty().filterNot { it.preApprovalId == preApprovalId })
                result.postValue("Pre-approval $preApprovalId cancelled.")
                emitSuccess(R.string.snackbar_preapproval_cancelled)
            }

            else -> {
                Timber.e("Cancel pre-approval failed: %s", response.message)
                result.postValue("Cancel failed: ${response.message}")
                emitError(R.string.snackbar_preapproval_not_cancelled, response.message)
            }
        }
    }

    /** Runs [block] inside a coroutine with a token guard, progress toggle, and error handling. */
    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Approved pre-approvals operation skipped: access token is blank")
                emitError(R.string.snackbar_token_expired)
                return@launch
            }
            showProgressBar.postValue(true)
            try {
                block()
            } catch (exception: Exception) {
                Timber.e(exception, "Approved pre-approvals operation failed")
                result.postValue("Error: ${exception.message}")
                emitError(R.string.snackbar_operation_failed, exception.message.orEmpty())
            } finally {
                showProgressBar.postValue(false)
            }
        }
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
