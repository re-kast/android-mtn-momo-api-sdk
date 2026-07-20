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

class ErrorResponseTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullErrorResponse() = ErrorResponse(
        code = "500",
        message = "Internal server error occurred",
        error = "INTERNAL_ERROR"
    )

    /** Verifies that all [ErrorResponse] fields are mapped correctly from JSON. */
    @Test
    fun `ErrorResponse fields are mapped correctly from JSON`() {
        val raw = """
            {
              "code": "RESOURCE_NOT_FOUND",
              "message": "Requested resource was not found.",
              "error": "404"
            }
        """.trimIndent()
        val result = json.decodeFromString<ErrorResponse>(raw)
        assertNotNull(result)
        assertEquals("RESOURCE_NOT_FOUND", result.code)
        assertEquals("Requested resource was not found.", result.message)
        assertEquals("404", result.error)
    }

    @Test
    fun `ErrorResponse round-trips when fully populated`() {
        val original = fullErrorResponse()
        assertEquals(original, jsonSkipDefaults.decodeFromString<ErrorResponse>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<ErrorResponse>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `ErrorResponse decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<ErrorResponse>("{}")
    }
}
