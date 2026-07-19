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
package io.rekast.sdk.sample.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

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
    }
    return ComposeColor.Unspecified
}

/**
 * This is required to fix keyboard overlapping content in a Composable screen. This functionality
 * is applied after the setContent function of the activity is called.
 */
fun Activity.applyWindowInsetListener() {
    ViewCompat.setOnApplyWindowInsetsListener(this.findViewById(android.R.id.content)) { view, insets ->
        val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
        view.updatePadding(bottom = bottom)
        insets
    }
}

/** This function checks if the device is online */
fun isDeviceOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

/**
 * Collects this [SharedFlow] and shows a [Snackbar] via [scaffoldState] for each emitted
 * [SnackBarComponentConfiguration] that contains a non-empty message.
 *
 * @param scaffoldState [ScaffoldState] used to display the snackbar.
 * @param onDisplay Callback invoked with each configuration immediately before its snackbar is
 *   shown; use it to drive per-message styling (e.g. success/error colors). Defaults to no-op.
 * @param action Optional callback invoked when the snackbar action is performed; defaults to no-op.
 */
suspend fun SharedFlow<SnackBarComponentConfiguration>.hookSnackBar(scaffoldState: ScaffoldState, onDisplay: (SnackBarComponentConfiguration) -> Unit = {}, action: () -> Unit = {}) {
    this.collectLatest { snackBarState ->
        if (snackBarState.message.isNotEmpty()) {
            onDisplay(snackBarState)
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
