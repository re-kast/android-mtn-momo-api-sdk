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
package io.rekast.sdk.sample.views.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Setup & Config screen. Surfaces the otherwise-invisible credential bootstrap by
 * reading the current provisioning state from [CredentialStorage] and the static [SampleConfig].
 *
 * @property config The static app configuration (environment, API versions, product keys).
 */
@HiltViewModel
class SetupScreenViewModel @Inject constructor(private val credentialStorage: CredentialStorage, val config: SampleConfig) : ViewModel() {

    /** Snapshot of which credentials the bootstrap flow has provisioned so far. */
    data class CredentialStatus(val apiKeyPresent: Boolean, val accessTokenPresent: Boolean, val oauthTokenPresent: Boolean, val authReqIdPresent: Boolean, val loginHintPresent: Boolean)

    private val _status = MutableLiveData(readStatus())

    /** The current credential-provisioning status; call [refresh] to re-read it. */
    val status: LiveData<CredentialStatus> get() = _status

    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()

    /** Flow of snackbar events to be displayed. */
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()

    /** Re-reads the credential status from storage (e.g. after a bootstrap re-run completes). */
    fun refresh() {
        _status.value = readStatus()
    }

    /** Emits a snackbar noting that the bootstrap sequence has been re-triggered. */
    fun notifyRerun() {
        viewModelScope.launch {
            _snackBarStateFlow.emit(
                SnackBarComponentConfiguration(message = "Re-running SDK setup…", type = SnackBarType.INFO)
            )
        }
    }

    private fun readStatus() = CredentialStatus(
        apiKeyPresent = credentialStorage.getApiKey().isNotBlank(),
        accessTokenPresent = credentialStorage.getAccessToken().isNotBlank(),
        oauthTokenPresent = credentialStorage.getOauthAccessToken().isNotBlank(),
        authReqIdPresent = credentialStorage.getBackChannelAuthorizationRequestId().isNotBlank(),
        loginHintPresent = credentialStorage.getLoginHint().isNotBlank()
    )
}
