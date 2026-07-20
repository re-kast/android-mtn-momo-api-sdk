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

    /** Verifies the default messageResId is 0 (the "no message" sentinel, safe no-op initial state). */
    @Test
    fun `default messageResId is zero`() {
        assertEquals(0, SnackBarComponentConfiguration().messageResId)
    }

    /** Verifies the default messageArgs is empty (no format arguments). */
    @Test
    fun `default messageArgs is empty`() {
        assertEquals(emptyList<Any>(), SnackBarComponentConfiguration().messageArgs)
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

    /** Verifies the default type is INFO. */
    @Test
    fun `default type is INFO`() {
        assertEquals(SnackBarType.INFO, SnackBarComponentConfiguration().type)
    }

    /** Verifies all properties can be supplied with custom values. */
    @Test
    fun `custom values are stored correctly`() {
        val config = SnackBarComponentConfiguration(
            messageResId = 42,
            messageArgs = listOf("Payment", 100),
            actionLabel = "Dismiss",
            duration = SnackbarDuration.Long,
            type = SnackBarType.SUCCESS
        )
        assertEquals(42, config.messageResId)
        assertEquals(listOf("Payment", 100), config.messageArgs)
        assertEquals("Dismiss", config.actionLabel)
        assertEquals(SnackbarDuration.Long, config.duration)
        assertEquals(SnackBarType.SUCCESS, config.type)
    }

    /** Verifies actionLabel can be explicitly set to null. */
    @Test
    fun `actionLabel can be explicitly null`() {
        val config = SnackBarComponentConfiguration(messageResId = 42, actionLabel = null)
        assertNull(config.actionLabel)
    }

    /** Verifies two instances with the same values are equal (data class contract). */
    @Test
    fun `two instances with identical values are equal`() {
        val a = SnackBarComponentConfiguration(messageResId = 42, actionLabel = "OK")
        val b = SnackBarComponentConfiguration(messageResId = 42, actionLabel = "OK")
        assertEquals(a, b)
    }
}
