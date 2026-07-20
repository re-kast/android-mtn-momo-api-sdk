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
package io.rekast.sdk.model

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MomoNotificationTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullMomoNotification() = MomoNotification(
        notificationMessage = "Your payment of 100 KES was successful"
    )

    /** Verifies that [MomoNotification.notificationMessage] is mapped correctly from JSON. */
    @Test
    fun `MomoNotification fields are mapped correctly from JSON`() {
        val raw = """{ "notificationMessage": "Payment received successfully." }"""
        val result = json.decodeFromString<MomoNotification>(raw)
        assertNotNull(result)
        assertEquals("Payment received successfully.", result.notificationMessage)
    }

    @Test
    fun `MomoNotification round-trips when fully populated`() {
        val original = fullMomoNotification()
        assertEquals(original, jsonSkipDefaults.decodeFromString<MomoNotification>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<MomoNotification>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `MomoNotification decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<MomoNotification>("{}")
    }
}
