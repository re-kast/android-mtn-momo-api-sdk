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

import androidx.compose.material.SnackbarDuration

/**
 * Semantic category of a snackbar message, used to pick its color: green for [SUCCESS],
 * red for [ERROR], and the MTN blue brand color for neutral [INFO] messages.
 */
enum class SnackBarType { SUCCESS, ERROR, INFO }

/**
 * Configuration data for displaying a [Snackbar], encapsulating message text, optional action
 * label, display duration, and a semantic [type] that drives the snackbar color.
 *
 * Passed in-memory via [kotlinx.coroutines.flow.SharedFlow] — no serialization needed.
 *
 * @property message The text to display in the snackbar body.
 * @property actionLabel Optional label for the snackbar action button; null means no action.
 * @property duration How long the snackbar should be visible; defaults to [SnackbarDuration.Short].
 * @property type The semantic type driving the snackbar color; defaults to [SnackBarType.INFO].
 */
data class SnackBarComponentConfiguration(val message: String = "", val actionLabel: String? = null, val duration: SnackbarDuration = SnackbarDuration.Short, val type: SnackBarType = SnackBarType.INFO)
