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

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

/*
 * MTN brand palette. The two anchor colors — MTN yellow and the deep MTN blue — are preserved
 * from the original app (`accent_primary` = #FFCB05, `accent_secondary` = #004F71) and drive the
 * whole theme in both light and dark modes.
 */

/** MTN's signature yellow — the primary accent for buttons, highlights, and progress. */
val MtnYellow = Color(0xFFFFCB05)

/** A slightly deeper yellow used for pressed/variant states. */
val MtnYellowDark = Color(0xFFE6B800)

/** MTN's deep blue — the primary brand surface color (top bar, drawer) in light mode. */
val MtnBlue = Color(0xFF004F71)

/** A darker MTN blue used for the status bar and variant states. */
val MtnBlueDark = Color(0xFF003A54)

/* Neutral text/ink colors used for on-surface content. */
private val InkLight = Color(0xFF1A1D1F)
private val InkDark = Color(0xFFECEEF0)

/* Light-mode neutrals. */
private val LightBackground = Color(0xFFF2F4F7)
private val LightSurface = Color(0xFFFFFFFF)

/* Dark-mode neutrals. */
private val DarkBackground = Color(0xFF121417)
private val DarkSurface = Color(0xFF1D2024)

/** Muted color for secondary/label text; adapts per theme via [io.rekast.sdk.sample.ui.theme.subtleTextColor]. */
val SubtleTextLight = Color(0xFF6B7280)
val SubtleTextDark = Color(0xFF9AA3AD)

/** Hairline divider/border colors per theme. */
val DividerLight = Color(0xFFE4E7EC)
val DividerDark = Color(0xFF2C3036)

/* Semantic status colors — tuned to read acceptably on both light and dark surfaces. */
val SuccessColor = Color(0xFF14A44D)
val DangerColor = Color(0xFFDE0E1A)
val WarningColor = Color(0xFFFF8800)
val InfoColor = Color(0xFF006EB8)

/** Material light color palette anchored on the MTN blue with a yellow secondary accent. */
val LightColors =
    lightColors(
        primary = MtnBlue,
        primaryVariant = MtnBlueDark,
        secondary = MtnYellow,
        secondaryVariant = MtnYellowDark,
        background = LightBackground,
        surface = LightSurface,
        error = DangerColor,
        onPrimary = Color.White,
        onSecondary = InkLight,
        onBackground = InkLight,
        onSurface = InkLight,
        onError = Color.White
    )

/**
 * Material dark color palette. MTN yellow becomes the primary accent (highest contrast on dark
 * surfaces) while backgrounds and surfaces shift to deep neutrals for a modern, low-glare look.
 */
val DarkColors =
    darkColors(
        primary = MtnYellow,
        primaryVariant = MtnYellowDark,
        secondary = MtnYellow,
        secondaryVariant = MtnYellowDark,
        background = DarkBackground,
        surface = DarkSurface,
        error = Color(0xFFFF6B6B),
        onPrimary = InkLight,
        onSecondary = InkLight,
        onBackground = InkDark,
        onSurface = InkDark,
        onError = InkLight
    )
