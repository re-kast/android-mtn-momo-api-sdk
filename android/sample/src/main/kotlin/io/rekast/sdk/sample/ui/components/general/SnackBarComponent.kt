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
import androidx.compose.ui.graphics.Color
import io.rekast.sdk.sample.ui.theme.DangerColor
import io.rekast.sdk.sample.ui.theme.MtnBlue
import io.rekast.sdk.sample.ui.theme.MtnYellow
import io.rekast.sdk.sample.ui.theme.SuccessColor
import io.rekast.sdk.sample.utils.SnackBarType
import io.rekast.sdk.sample.utils.annotation.PreviewExcludeGenerated

/**
 * Renders a [SnackbarHost] whose [Snackbar] is colored by its semantic [type]: green for
 * [SnackBarType.SUCCESS], red for [SnackBarType.ERROR], and MTN blue for [SnackBarType.INFO].
 * The action label keeps the MTN yellow accent in all cases for brand consistency.
 *
 * @param modifier Modifier applied to the [Snackbar].
 * @param snackBarHostState State object that controls snackbar visibility and content.
 * @param type The semantic type of the currently displayed snackbar; drives the background color.
 */
@Composable
fun SnackBarComponent(modifier: Modifier = Modifier, snackBarHostState: SnackbarHostState, type: SnackBarType = SnackBarType.INFO) {
    val backgroundColor = when (type) {
        SnackBarType.SUCCESS -> SuccessColor
        SnackBarType.ERROR -> DangerColor
        SnackBarType.INFO -> MtnBlue
    }
    SnackbarHost(
        hostState = snackBarHostState,
        snackbar = { snackBarData ->
            Snackbar(
                snackbarData = snackBarData,
                backgroundColor = backgroundColor,
                contentColor = Color.White,
                actionColor = MtnYellow,
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
        type = SnackBarType.SUCCESS
    )
}
