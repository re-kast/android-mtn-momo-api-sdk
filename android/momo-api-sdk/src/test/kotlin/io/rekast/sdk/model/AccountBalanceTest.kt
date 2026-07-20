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

class AccountBalanceTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonSkipDefaults = Json { encodeDefaults = false }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    private fun fullAccountBalance() = AccountBalance(
        availableBalance = "1000.00",
        currency = "EUR"
    )

    /** Verifies that [AccountBalance.availableBalance] and [AccountBalance.currency] are mapped correctly. */
    @Test
    fun `AccountBalance fields are mapped correctly from JSON`() {
        val raw = """
            {
              "availableBalance": "1500.00",
              "currency": "EUR"
            }
        """.trimIndent()
        val result = json.decodeFromString<AccountBalance>(raw)
        assertNotNull(result)
        assertEquals("1500.00", result.availableBalance)
        assertEquals("EUR", result.currency)
    }

    @Test
    fun `AccountBalance round-trips when fully populated`() {
        val original = fullAccountBalance()
        assertEquals(original, jsonSkipDefaults.decodeFromString<AccountBalance>(jsonSkipDefaults.encodeToString(original)))
        assertEquals(original, jsonWithDefaults.decodeFromString<AccountBalance>(jsonWithDefaults.encodeToString(original)))
    }

    @Test(expected = SerializationException::class)
    fun `AccountBalance decoding an empty object throws`() {
        jsonSkipDefaults.decodeFromString<AccountBalance>("{}")
    }
}
