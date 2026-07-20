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

import androidx.annotation.StringRes
import androidx.compose.material.SnackbarDuration

/**
 * Semantic category of a snackbar message, used to pick its color: green for [SUCCESS],
 * red for [ERROR], and the MTN blue brand color for neutral [INFO] messages.
 */
enum class SnackBarType { SUCCESS, ERROR, INFO }

/**
 * Configuration data for displaying a [Snackbar], encapsulating the message string resource,
 * optional action label, display duration, and a semantic [type] that drives the snackbar color.
 *
 * The message is carried as a [StringRes] id (plus optional [messageArgs] format arguments) rather
 * than a resolved string so that all snackbar copy is localizable and lives in `strings.xml`. It is
 * resolved to a display string at the UI layer (see [SharedFlow.hookSnackBar]), which has a
 * [android.content.Context]. Passed in-memory via [kotlinx.coroutines.flow.SharedFlow] — no
 * serialization needed.
 *
 * @property messageResId String resource id for the snackbar body; `0` means "no message" (skipped).
 * @property messageArgs Format arguments for [messageResId] when it contains placeholders (e.g. `%1$s`).
 * @property actionLabel Optional label for the snackbar action button; null means no action.
 * @property duration How long the snackbar should be visible; defaults to [SnackbarDuration.Short].
 * @property type The semantic type driving the snackbar color; defaults to [SnackBarType.INFO].
 */
data class SnackBarComponentConfiguration(
    @StringRes val messageResId: Int = 0,
    val messageArgs: List<Any> = emptyList(),
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val type: SnackBarType = SnackBarType.INFO
)
