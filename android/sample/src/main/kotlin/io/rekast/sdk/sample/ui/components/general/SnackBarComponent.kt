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
package io.rekast.sdk.sample.ui.components.general

import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.rekast.sdk.sample.utils.annotation.PreviewExcludeGenerated
import io.rekast.sdk.sample.utils.parseColor

/**
 * Renders a [SnackbarHost] with a custom-themed [Snackbar] whose colors are specified as hex strings.
 *
 * @param modifier Modifier applied to the [Snackbar].
 * @param snackBarHostState State object that controls snackbar visibility and content.
 * @param backgroundColorHex Hex color string (e.g. "#EB9779") for the snackbar background.
 * @param actionColorHex Hex color string for the action button text.
 * @param contentColorHex Hex color string for the message text.
 */
@Composable
fun SnackBarComponent(modifier: Modifier = Modifier, snackBarHostState: SnackbarHostState, backgroundColorHex: String, actionColorHex: String, contentColorHex: String) {
    SnackbarHost(
        hostState = snackBarHostState,
        snackbar = { snackBarData ->
            Snackbar(
                snackbarData = snackBarData,
                backgroundColor = backgroundColorHex.parseColor(),
                contentColor = contentColorHex.parseColor(),
                actionColor = actionColorHex.parseColor(),
                modifier = modifier
            )
        }
    )
}

@PreviewExcludeGenerated
@Composable
fun SnackBarComponentPreview() {
    SnackBarComponent(
        snackBarHostState = remember { SnackbarHostState() },
        backgroundColorHex = "#EB9779",
        actionColorHex = "#FFFFFF",
        contentColorHex = "#FFFFFF"
    )
}
