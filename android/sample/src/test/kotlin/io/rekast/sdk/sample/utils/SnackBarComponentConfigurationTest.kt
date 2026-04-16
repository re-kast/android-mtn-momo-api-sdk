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

import androidx.compose.material.SnackbarDuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [SnackBarComponentConfiguration].
 *
 * Verifies default values and that all properties accept custom inputs.
 * [SnackBarComponentConfiguration] is an in-memory value object passed via SharedFlow;
 * it carries no serialization annotations and has no Android runtime dependencies
 * beyond the [SnackbarDuration] enum.
 */
class SnackBarComponentConfigurationTest {

    /** Verifies the default message is an empty string (safe no-op initial state). */
    @Test
    fun `default message is empty string`() {
        assertEquals("", SnackBarComponentConfiguration().message)
    }

    /** Verifies the default actionLabel is null (no action button rendered by default). */
    @Test
    fun `default actionLabel is null`() {
        assertNull(SnackBarComponentConfiguration().actionLabel)
    }

    /** Verifies the default duration is Short. */
    @Test
    fun `default duration is Short`() {
        assertEquals(SnackbarDuration.Short, SnackBarComponentConfiguration().duration)
    }

    /** Verifies all properties can be supplied with custom values. */
    @Test
    fun `custom values are stored correctly`() {
        val config = SnackBarComponentConfiguration(
            message = "Payment processed",
            actionLabel = "Dismiss",
            duration = SnackbarDuration.Long
        )
        assertEquals("Payment processed", config.message)
        assertEquals("Dismiss", config.actionLabel)
        assertEquals(SnackbarDuration.Long, config.duration)
    }

    /** Verifies actionLabel can be explicitly set to null. */
    @Test
    fun `actionLabel can be explicitly null`() {
        val config = SnackBarComponentConfiguration(message = "Done", actionLabel = null)
        assertNull(config.actionLabel)
    }

    /** Verifies two instances with the same values are equal (data class contract). */
    @Test
    fun `two instances with identical values are equal`() {
        val a = SnackBarComponentConfiguration(message = "Hello", actionLabel = "OK")
        val b = SnackBarComponentConfiguration(message = "Hello", actionLabel = "OK")
        assertEquals(a, b)
    }
}
