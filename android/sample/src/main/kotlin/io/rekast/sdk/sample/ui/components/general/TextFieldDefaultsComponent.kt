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

import androidx.compose.material.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import io.rekast.sdk.sample.ui.theme.dividerColor

/**
 * Returns a theme-aware [TextFieldColors] configuration for text fields throughout the sample
 * application. Container and indicator colors resolve from [MaterialTheme] so fields render
 * correctly in both light and dark mode while keeping the MTN accent for focus and cursor.
 *
 * - Focused indicator & cursor: the theme accent (MTN yellow in dark, MTN blue in light)
 * - Unfocused indicator: a muted divider color
 * - Container background: the theme surface
 */
@Composable
fun textFieldDefaultsComponent(): TextFieldColors = TextFieldDefaults.colors(
    focusedIndicatorColor = MaterialTheme.colors.secondary,
    unfocusedIndicatorColor = dividerColor,
    focusedContainerColor = MaterialTheme.colors.surface,
    unfocusedContainerColor = MaterialTheme.colors.surface,
    focusedTextColor = MaterialTheme.colors.onSurface,
    unfocusedTextColor = MaterialTheme.colors.onSurface,
    cursorColor = MaterialTheme.colors.secondary
)
