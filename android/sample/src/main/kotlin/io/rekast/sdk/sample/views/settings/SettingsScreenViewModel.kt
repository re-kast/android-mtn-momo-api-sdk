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
package io.rekast.sdk.sample.views.settings

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.rekast.sdk.sample.R
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
 * ViewModel for the Settings screen. Exposes the static environment configuration, the product
 * subscription keys, and the host application's build metadata, plus a destructive action to clear
 * all locally-stored credentials.
 *
 * @property config The static app configuration (environment, API versions, product keys).
 */
@HiltViewModel
class SettingsScreenViewModel @Inject constructor(@param:ApplicationContext private val context: Context, private val credentialStorage: CredentialStorage, val config: SampleConfig) : ViewModel() {

    /** Host application build metadata surfaced on the Settings screen. */
    data class AppInfo(val versionName: String, val versionCode: String, val packageName: String)

    /** The host application's version, build number, and package name. */
    val appInfo: AppInfo = readAppInfo()

    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()

    /** Flow of snackbar events to be displayed. */
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()

    /**
     * Clears every stored credential and notifies the user. The SDK bootstrap flow re-provisions
     * the credentials on the next request.
     */
    fun clearCredentials() {
        credentialStorage.clearAll()
        viewModelScope.launch {
            _snackBarStateFlow.emit(
                SnackBarComponentConfiguration(messageResId = R.string.snackbar_credentials_cleared, type = SnackBarType.INFO)
            )
        }
    }

    private fun readAppInfo(): AppInfo = try {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode.toString()
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toString()
        }
        AppInfo(
            versionName = info.versionName.orEmpty(),
            versionCode = versionCode,
            packageName = context.packageName
        )
    } catch (e: PackageManager.NameNotFoundException) {
        AppInfo(versionName = "", versionCode = "", packageName = context.packageName)
    }
}
