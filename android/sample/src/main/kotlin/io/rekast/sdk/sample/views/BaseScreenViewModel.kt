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
package io.rekast.sdk.sample.views

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import io.rekast.sdk.utils.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Base class for the sample's screen ViewModels, centralising the state and helpers every screen
 * shares: a progress-bar flag, a snackbar event flow with success/error emitters, and a helper to
 * await a repository [Flow]'s terminal result.
 */
abstract class BaseScreenViewModel : ViewModel() {

    /** Controls whether the circular progress indicator is shown instead of the screen content. */
    val showProgressBar = MutableLiveData(false)

    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()

    /** Flow of [SnackBarComponentConfiguration] events to be displayed as snack bars. */
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()

    private val settings = Settings()

    /** Generates a random v4 UUID string via the SDK's [Settings] helper, for transaction references and external IDs. */
    protected fun generateUuid(): String = settings.generateUUID()

    /**
     * Collects this result [Flow] to completion and returns its terminal (non-[NetworkResult.Loading])
     * emission, so a suspend caller can await the flow's final success or error.
     */
    protected suspend fun <T> Flow<NetworkResult<T>>.awaitTerminal(): NetworkResult<T> {
        var terminal: NetworkResult<T> = NetworkResult.Error("No response received")
        collect { emission -> if (emission !is NetworkResult.Loading) terminal = emission }
        return terminal
    }

    /** Emits a success snack bar with the given string resource and optional format args. */
    protected fun emitSuccess(@StringRes messageResId: Int, vararg args: Any) = emitSnackBarState(SnackBarComponentConfiguration(messageResId = messageResId, messageArgs = args.toList(), type = SnackBarType.SUCCESS))

    /** Emits an error snack bar with the given string resource and optional format args. */
    protected fun emitError(@StringRes messageResId: Int, vararg args: Any) = emitSnackBarState(SnackBarComponentConfiguration(messageResId = messageResId, messageArgs = args.toList(), type = SnackBarType.ERROR))

    /** Emits an arbitrary [SnackBarComponentConfiguration] event to the snack bar flow. */
    protected fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }
}
