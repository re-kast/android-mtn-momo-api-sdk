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
package io.rekast.sdk.sample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Applies the application's Material theme to the provided [content], switching between
 * [LightColors] and [DarkColors].
 *
 * By default the theme follows the device's system dark-mode preference via
 * [isSystemInDarkTheme]. Pass an explicit [darkTheme] value to override.
 *
 * TODO fix issue with ktfmt formatting annotated high order functions. Current workaround below:
 * lambda in this format content: (@Composable() () -> Unit) to allow spotlessApply
 *
 * @param darkTheme Whether to use the dark color palette; defaults to the system setting.
 * @param content The composable content to render inside the theme.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content:
    (
    @Composable () -> Unit
    )
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

/**
 * Muted color for secondary / label text, resolved for the current (light or dark) theme.
 * Use for captions, field labels, and other de-emphasized copy.
 */
val subtleTextColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colors.isLight) SubtleTextLight else SubtleTextDark

/**
 * Hairline divider / border color resolved for the current (light or dark) theme.
 */
val dividerColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colors.isLight) DividerLight else DividerDark
