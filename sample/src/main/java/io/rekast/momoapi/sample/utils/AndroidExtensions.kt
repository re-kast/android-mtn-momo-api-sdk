/*
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package io.rekast.momoapi.sample.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import io.rekast.momoapi.sample.ui.theme.DangerColor
import io.rekast.momoapi.sample.ui.theme.DefaultColor
import io.rekast.momoapi.sample.ui.theme.InfoColor
import io.rekast.momoapi.sample.ui.theme.LightColors
import io.rekast.momoapi.sample.ui.theme.SuccessColor
import io.rekast.momoapi.sample.ui.theme.WarningColor
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.graphics.Color as ComposeColor

const val ERROR_COLOR = "errorColor"
const val PRIMARY_COLOR = "primaryColor"
const val PRIMARY_VARIANT_COLOR = "primaryVariantColor"
const val DEFAULT_COLOR = "defaultColor"
const val SUCCESS_COLOR = "successColor"
const val WARNING_COLOR = "warningColor"
const val DANGER_COLOR = "dangerColor"
const val INFO_COLOR = "infoColor"

fun Activity.refresh() {
    finish()
    startActivity(Intent(this, this.javaClass))
    finishAffinity()
}

/**
 * Parse this [String] to a color code to be used in compose. Color code must either a). begin with
 * pound sign ('#') and should be of 6 valid characters or b). be equal to 'primaryColor',
 * 'primaryVariantColor' or 'errorColor'
 */
fun String?.parseColor(): androidx.compose.ui.graphics.Color {
    if (this.isNullOrEmpty()) {
        return ComposeColor.Unspecified
    } else if (this.startsWith("#")) {
        return ComposeColor(Color.parseColor(this))
    } else {
        when {
            this.equals(PRIMARY_COLOR, ignoreCase = true) -> return LightColors.primary
            this.equals(PRIMARY_VARIANT_COLOR, ignoreCase = true) -> return LightColors.primaryVariant
            this.equals(ERROR_COLOR, ignoreCase = true) -> return LightColors.error
            this.equals(DANGER_COLOR, ignoreCase = true) -> return DangerColor
            this.equals(WARNING_COLOR, ignoreCase = true) -> return WarningColor
            this.equals(INFO_COLOR, ignoreCase = true) -> return InfoColor
            this.equals(SUCCESS_COLOR, ignoreCase = true) -> return SuccessColor
            this.equals(DEFAULT_COLOR, ignoreCase = true) -> return DefaultColor
        }
    }
    return ComposeColor.Unspecified
}

fun Context.getActivity(): AppCompatActivity? =
    when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }

/**
 * This is required to fix keyboard overlapping content in a Composable screen. This functionality
 * is applied after the setContent function of the activity is called.
 */
fun Activity.applyWindowInsetListener() {
    ViewCompat.setOnApplyWindowInsetsListener(this.findViewById(android.R.id.content)) { view, insets
        ->
        val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
        view.updatePadding(bottom = bottom)
        insets
    }
}

/** This function checks if the device is online */
fun Activity.isDeviceOnline(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    // Device can be connected to the internet through any of these NetworkCapabilities
    val transports: List<Int> =
        listOf(
            NetworkCapabilities.TRANSPORT_ETHERNET,
            NetworkCapabilities.TRANSPORT_CELLULAR,
            NetworkCapabilities.TRANSPORT_WIFI,
            NetworkCapabilities.TRANSPORT_VPN
        )
    return transports.any { capabilities.hasTransport(it) }
}

suspend fun SharedFlow<SnackBarComponentConfiguration>.hookSnackBar(
    scaffoldState: ScaffoldState,
    action: () -> Unit = {}
) {
    this.collectLatest { snackBarState ->
        if (snackBarState.message.isNotEmpty()) {
            val snackBarResult =
                scaffoldState.snackbarHostState.showSnackbar(
                    message = snackBarState.message,
                    actionLabel = snackBarState.actionLabel,
                    duration = snackBarState.duration
                )
            when (snackBarResult) {
                SnackbarResult.ActionPerformed -> {
                    /**/
                }
                SnackbarResult.Dismissed -> {
                    /* Do nothing (for now) when snackBar is dismissed */
                }
            }
        }
    }
}
