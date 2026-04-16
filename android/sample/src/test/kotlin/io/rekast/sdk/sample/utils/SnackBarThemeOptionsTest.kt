/*
 * Copyright 2023-2024, Benjamin Mwalimu
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

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [SnackBarThemeOptions].
 *
 * Verifies that the default colors match the MTN MoMo brand palette that was applied
 * to the theme: white message text, MTN yellow (#FFCB05) action text, and MTN blue
 * (#004F71) background.
 */
class SnackBarThemeOptionsTest {

    /** Verifies the default message text color is white. */
    @Test
    fun `default messageTextColor is white`() {
        assertEquals("#FFFFFF", SnackBarThemeOptions().messageTextColor)
    }

    /** Verifies the default action text color matches MTN yellow. */
    @Test
    fun `default actionTextColor is MTN yellow`() {
        assertEquals("#FFCB05", SnackBarThemeOptions().actionTextColor)
    }

    /** Verifies the default background color matches MTN blue. */
    @Test
    fun `default backgroundColor is MTN blue`() {
        assertEquals("#004F71", SnackBarThemeOptions().backgroundColor)
    }

    /** Verifies all three properties can be overridden with custom hex strings. */
    @Test
    fun `custom values override all defaults`() {
        val custom = SnackBarThemeOptions(
            messageTextColor = "#000000",
            actionTextColor = "#FF0000",
            backgroundColor = "#00FF00"
        )
        assertEquals("#000000", custom.messageTextColor)
        assertEquals("#FF0000", custom.actionTextColor)
        assertEquals("#00FF00", custom.backgroundColor)
    }

    /** Verifies two instances constructed with the same values are equal (data class contract). */
    @Test
    fun `two instances with identical values are equal`() {
        val a = SnackBarThemeOptions()
        val b = SnackBarThemeOptions()
        assertEquals(a, b)
    }
}
