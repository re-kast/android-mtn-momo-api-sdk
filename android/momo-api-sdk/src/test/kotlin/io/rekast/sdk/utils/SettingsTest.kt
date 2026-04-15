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
package io.rekast.sdk.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsTest {

    private lateinit var settings: Settings

    @Before
    fun setUp() {
        settings = Settings()
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `generateUUID returns a non-empty string`() {
        assertTrue(settings.generateUUID().isNotEmpty())
    }

    @Test
    fun `generateUUID returns unique values on each call`() {
        assertNotEquals(settings.generateUUID(), settings.generateUUID())
    }

    @Test
    fun `generateUUID returns a string in UUID format`() {
        val uuid = settings.generateUUID()
        val uuidRegex = Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        assertTrue(uuidRegex.matches(uuid))
    }

    @Test
    fun `connectTimeout is 60 seconds in milliseconds`() {
        assertEquals(60_000L, settings.connectTimeout)
    }

    @Test
    fun `readTimeout is 60 seconds in milliseconds`() {
        assertEquals(60_000L, settings.readTimeout)
    }

    @Test
    fun `writeTimeout is 60 seconds in milliseconds`() {
        assertEquals(60_000L, settings.writeTimeout)
    }

    @Test
    fun `formatPhoneNumber returns null for blank input`() {
        assertNull(settings.formatPhoneNumber("", "256"))
        assertNull(settings.formatPhoneNumber("   ", "256"))
    }

    @Test
    fun `formatPhoneNumber replaces leading zero with country code for short numbers`() {
        val result = settings.formatPhoneNumber("0733123456", "256")
        assertEquals("256733123456", result)
    }

    @Test
    fun `formatPhoneNumber returns number unchanged when already 13 digits without plus`() {
        val result = settings.formatPhoneNumber("2567331234567", "256")
        assertEquals("2567331234567", result)
    }

    @Test
    fun `formatPhoneNumber strips leading plus from 13-digit number`() {
        val result = settings.formatPhoneNumber("+256733123456", "256")
        assertEquals("256733123456", result)
    }

    @Test
    fun `formatPhoneNumber returns number unchanged when no leading zero and not 13 digits`() {
        val result = settings.formatPhoneNumber("256733123456", "256")
        assertEquals("256733123456", result)
    }

    @Test
    fun `checkNotificationMessageLength returns false for blank message`() {
        assertFalse(settings.checkNotificationMessageLength(null))
        assertFalse(settings.checkNotificationMessageLength(""))
        assertFalse(settings.checkNotificationMessageLength("   "))
    }

    @Test
    fun `checkNotificationMessageLength returns true for message within limit`() {
        assertTrue(settings.checkNotificationMessageLength("Hello world"))
    }

    @Test
    fun `checkNotificationMessageLength returns true for message exactly at limit`() {
        val maxMessage = "a".repeat(MomoConstants.NOTIFICATION_MESSAGE_LENGTH.toInt())
        assertTrue(settings.checkNotificationMessageLength(maxMessage))
    }

    @Test
    fun `checkNotificationMessageLength returns false for message exceeding limit`() {
        val longMessage = "a".repeat(MomoConstants.NOTIFICATION_MESSAGE_LENGTH.toInt() + 1)
        assertFalse(settings.checkNotificationMessageLength(longMessage))
    }

    @Test
    fun `checkNotificationMessageLength respects custom max length`() {
        assertTrue(settings.checkNotificationMessageLength("Hello", 10L))
        assertFalse(settings.checkNotificationMessageLength("Hello World", 5L))
    }
}
