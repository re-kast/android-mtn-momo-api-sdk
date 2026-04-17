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
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for [ApiErrorResponses].
 *
 * Verifies that every enum constant is present and addressable by name, and that the enum
 * contains exactly the expected number of error codes so that accidental additions or removals
 * are caught at test time.
 */
class ApiErrorResponsesTest {

    /**
     * Verifies that [ApiErrorResponses] contains exactly 27 error constants — one for each
     * documented MTN MOMO API error code. A count mismatch means a constant was added or removed
     * without updating this test.
     */
    @Test
    fun `entries contains exactly 27 error codes`() {
        assertEquals(27, ApiErrorResponses.entries.size)
    }

    /**
     * Verifies that [ApiErrorResponses.valueOf] resolves each constant by its declared name,
     * ensuring no constant has been renamed or removed.
     */
    @Test
    fun `valueOf resolves all expected error code names`() {
        val expectedNames = listOf(
            "PAYER_NOT_FOUND",
            "PAYER_LIMIT_REACHED",
            "NOT_ENOUGH_FUNDS",
            "NOT_ALLOWED",
            "NOT_ALLOWED_TARGET_ENVIRONMENT",
            "INVALID_CALLBACK_URL_HOST",
            "INVALID_CURRENCY",
            "SERVICE_UNAVAILABLE",
            "PAYEE_NOT_ALLOWED_TO_RECEIVE",
            "PAYMENT_NOT_APPROVED",
            "RESOURCE_NOT_FOUND",
            "APPROVAL_REJECTED",
            "EXPIRED",
            "TRANSACTION_CANCELED",
            "RESOURCE_ALREADY_EXIST",
            "TRANSACTION_NOT_COMPLETED",
            "TRANSACTION_NOT_FOUND",
            "INFORMATIONAL_SCOPE_INSTRUCTION",
            "MISSING_SCOPE_INSTRUCTION",
            "MORE_THAN_ONE_FINANCIAL_SCOPE_NOT_SUPPORTED",
            "UNSUPPORTED_SCOPE_COMBINATION",
            "CONSENT_MISMATCH",
            "UNSUPPORTED_SCOPE",
            "NOT_FOUND",
            "PAYEE_NOT_FOUND",
            "INTERNAL_PROCESSING_ERROR",
            "COULD_NOT_PERFORM_TRANSACTION"
        )

        for (name in expectedNames) {
            val constant = ApiErrorResponses.valueOf(name)
            assertNotNull("Expected $name to exist", constant)
            assertEquals(name, constant.name)
        }
    }

    /**
     * Spot-checks a selection of [ApiErrorResponses] constants that are directly referenced
     * in the SDK's error handling logic to confirm their ordinal positions have not shifted.
     */
    @Test
    fun `spot-check ordinals of key error codes`() {
        assertEquals(0, ApiErrorResponses.PAYER_NOT_FOUND.ordinal)
        assertEquals(6, ApiErrorResponses.INVALID_CURRENCY.ordinal)
        assertEquals(26, ApiErrorResponses.COULD_NOT_PERFORM_TRANSACTION.ordinal)
    }

    /**
     * Verifies that [ApiErrorResponses.entries] returns all 27 constants and that each entry
     * round-trips correctly through [ApiErrorResponses.valueOf].
     */
    @Test
    fun `all entries round-trip through valueOf`() {
        for (entry in ApiErrorResponses.entries) {
            assertEquals(entry, ApiErrorResponses.valueOf(entry.name))
        }
    }
}
