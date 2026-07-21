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

import io.rekast.sdk.utils.FrequencyTypes
import io.rekast.sdk.utils.StatusTypes
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PreApprovalDetailsTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullPreApprovalResponse() = PreApprovalDetails(
        preApprovalId = "2132052725",
        toFri = "FRI:3891259513493198424/MM",
        fromFri = "FRI:256774290781/MM",
        fromCurrency = "EUR",
        createdTime = "2026-07-20T21:17:49.68956",
        status = StatusTypes.APPROVED,
        message = "I PAY YOU",
        approvedTime = "2026-07-20T22:17:49.689564",
        expiryTime = "2026-07-20T23:17:49.689577",
        frequency = FrequencyTypes.DAILY,
        startDate = "2026-07-22",
        lastUsedDate = "2026-07-20T21:17:49.689608",
        offer = "New offer",
        externalId = "-7377789948068870662",
        maxDebitAmount = "1000"
    )

    /** Verifies all fields map correctly from a representative API payload. */
    @Test
    fun `PreApprovalResponse fields are mapped correctly from JSON`() {
        val raw = """
            {
              "preApprovalId": "2132052725",
              "toFri": "FRI:3891259513493198424/MM",
              "fromFri": "FRI:256774290781/MM",
              "fromCurrency": "EUR",
              "createdTime": "2026-07-20T21:17:49.68956",
              "approvedTime": "2026-07-20T22:17:49.689564",
              "expiryTime": "2026-07-20T23:17:49.689577",
              "status": "APPROVED",
              "message": "I PAY YOU",
              "startDate": "2026-07-22",
              "lastUsedDate": "2026-07-20T21:17:49.689608",
              "offer": "New offer",
              "externalId": "-7377789948068870662"
            }
        """.trimIndent()
        val result = json.decodeFromString<PreApprovalDetails>(raw)
        assertNotNull(result)
        assertEquals("2132052725", result.preApprovalId)
        assertEquals("EUR", result.fromCurrency)
        assertEquals(StatusTypes.APPROVED, result.status)
        assertEquals("New offer", result.offer)
        // frequency and maxDebitAmount are absent in this payload and default to null.
        assertNull(result.frequency)
        assertNull(result.maxDebitAmount)
    }

    /** A payload with only the required fields deserializes, leaving optional fields null. */
    @Test
    fun `PreApprovalResponse decodes with only required fields`() {
        val raw = """
            {
              "preApprovalId": "1",
              "toFri": "FRI:a/MM",
              "fromFri": "FRI:b/MM",
              "fromCurrency": "EUR",
              "createdTime": "2026-07-20T21:17:49.68956",
              "status": "PENDING",
              "message": "hi"
            }
        """.trimIndent()
        val result = json.decodeFromString<PreApprovalDetails>(raw)
        assertEquals(StatusTypes.PENDING, result.status)
        assertNull(result.approvedTime)
        assertNull(result.expiryTime)
    }

    @Test
    fun `PreApprovalResponse round-trips when fully populated`() {
        val original = fullPreApprovalResponse()
        assertEquals(original, jsonSkipDefaults.decodeFromString<PreApprovalDetails>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<PreApprovalDetails>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `PreApprovalResponse decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<PreApprovalDetails>("{}")
    }
}
