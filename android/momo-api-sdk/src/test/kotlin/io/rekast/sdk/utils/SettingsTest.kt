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
package io.rekast.sdk.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [Settings].
 *
 * Covers:
 * - UUID generation: non-emptiness, uniqueness per call, and standard UUID format
 * - OkHttp timeout constants: connect, read, and write timeouts default to 60 seconds
 * - Phone number formatting: blank input, leading-zero replacement, 13-digit pass-through, and `+` stripping
 * - Notification message length validation: blank messages, within-limit, at-limit, over-limit, and custom max
 */
class SettingsTest {

    private lateinit var settings: Settings

    @Before
    fun setUp() {
        settings = Settings()
    }

    /** Sanity check — verifies basic arithmetic is correct. */
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    /** Verifies generateUUID never returns a blank or empty string. */
    @Test
    fun `generateUUID returns a non-empty string`() {
        assertTrue(settings.generateUUID().isNotEmpty())
    }

    /** Verifies two successive calls to generateUUID produce different values. */
    @Test
    fun `generateUUID returns unique values on each call`() {
        assertNotEquals(settings.generateUUID(), settings.generateUUID())
    }

    /** Verifies generateUUID output matches the standard 8-4-4-4-12 hex UUID pattern. */
    @Test
    fun `generateUUID returns a string in UUID format`() {
        val uuid = settings.generateUUID()
        val uuidRegex = Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        assertTrue(uuidRegex.matches(uuid))
    }

    /** Verifies the connect timeout is 60 000 ms (60 seconds). */
    @Test
    fun `connectTimeout is 60 seconds in milliseconds`() {
        assertEquals(60_000L, settings.connectTimeout)
    }

    /** Verifies the read timeout is 60 000 ms (60 seconds). */
    @Test
    fun `readTimeout is 60 seconds in milliseconds`() {
        assertEquals(60_000L, settings.readTimeout)
    }

    /** Verifies the write timeout is 60 000 ms (60 seconds). */
    @Test
    fun `writeTimeout is 60 seconds in milliseconds`() {
        assertEquals(60_000L, settings.writeTimeout)
    }

    /** Verifies formatPhoneNumber returns null when the input is empty or whitespace only. */
    @Test
    fun `formatPhoneNumber returns null for blank input`() {
        assertNull(settings.formatPhoneNumber("", "256"))
        assertNull(settings.formatPhoneNumber("   ", "256"))
    }

    /** Verifies a leading zero is replaced by the supplied country code for sub-13-digit numbers. */
    @Test
    fun `formatPhoneNumber replaces leading zero with country code for short numbers`() {
        val result = settings.formatPhoneNumber("0733123456", "256")
        assertEquals("256733123456", result)
    }

    /** Verifies a 13-digit number without a leading `+` is returned as-is. */
    @Test
    fun `formatPhoneNumber returns number unchanged when already 13 digits without plus`() {
        val result = settings.formatPhoneNumber("2567331234567", "256")
        assertEquals("2567331234567", result)
    }

    /** Verifies the leading `+` is stripped from an otherwise-valid E.164 number. */
    @Test
    fun `formatPhoneNumber strips leading plus from 13-digit number`() {
        val result = settings.formatPhoneNumber("+256733123456", "256")
        assertEquals("256733123456", result)
    }

    /** Verifies a number with no leading zero and no 13-digit length is returned as-is. */
    @Test
    fun `formatPhoneNumber returns number unchanged when no leading zero and not 13 digits`() {
        val result = settings.formatPhoneNumber("256733123456", "256")
        assertEquals("256733123456", result)
    }

    /** Verifies null, empty, and whitespace-only messages fail the length check. */
    @Test
    fun `checkNotificationMessageLength returns false for blank message`() {
        assertFalse(settings.checkNotificationMessageLength(null))
        assertFalse(settings.checkNotificationMessageLength(""))
        assertFalse(settings.checkNotificationMessageLength("   "))
    }

    /** Verifies a short message well within the default length limit passes the check. */
    @Test
    fun `checkNotificationMessageLength returns true for message within limit`() {
        assertTrue(settings.checkNotificationMessageLength("Hello world"))
    }

    /** Verifies a message whose length equals the constant limit is still accepted. */
    @Test
    fun `checkNotificationMessageLength returns true for message exactly at limit`() {
        val maxMessage = "a".repeat(Constants.NOTIFICATION_MESSAGE_LENGTH.toInt())
        assertTrue(settings.checkNotificationMessageLength(maxMessage))
    }

    /** Verifies a message one character over the constant limit is rejected. */
    @Test
    fun `checkNotificationMessageLength returns false for message exceeding limit`() {
        val longMessage = "a".repeat(Constants.NOTIFICATION_MESSAGE_LENGTH.toInt() + 1)
        assertFalse(settings.checkNotificationMessageLength(longMessage))
    }

    /** Verifies a caller-supplied maximum length overrides the default constant. */
    @Test
    fun `checkNotificationMessageLength respects custom max length`() {
        assertTrue(settings.checkNotificationMessageLength("Hello", 10L))
        assertFalse(settings.checkNotificationMessageLength("Hello World", 5L))
    }
}
