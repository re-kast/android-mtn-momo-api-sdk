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

import io.rekast.sdk.utils.StatusTypes
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApprovedPreApprovalsTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val jsonWithDefaults = Json { encodeDefaults = true }

    /** Verifies the `preApprovalDetails` array maps into a list of [PreApprovalDetails]. */
    @Test
    fun `ApprovedPreApprovals maps the preApprovalDetails list from JSON`() {
        val raw = """
            {
              "preApprovalDetails": [
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
              ]
            }
        """.trimIndent()
        val result = json.decodeFromString<ApprovedPreApprovals>(raw)
        assertEquals(1, result.preApprovalDetails.size)
        assertEquals("2132052725", result.preApprovalDetails.first().preApprovalId)
        assertEquals(StatusTypes.APPROVED, result.preApprovalDetails.first().status)
    }

    /** An empty object yields an empty list via the default, rather than throwing. */
    @Test
    fun `ApprovedPreApprovals defaults to an empty list`() {
        val result = json.decodeFromString<ApprovedPreApprovals>("{}")
        assertTrue(result.preApprovalDetails.isEmpty())
    }

    @Test
    fun `ApprovedPreApprovals round-trips`() {
        val original = ApprovedPreApprovals(
            preApprovalDetails = listOf(
                PreApprovalDetails(
                    preApprovalId = "1",
                    toFri = "FRI:a/MM",
                    fromFri = "FRI:b/MM",
                    fromCurrency = "EUR",
                    createdTime = "2026-07-20T21:17:49.68956",
                    status = StatusTypes.APPROVED,
                    message = "hi"
                )
            )
        )
        assertEquals(original, jsonWithDefaults.decodeFromString<ApprovedPreApprovals>(jsonWithDefaults.encodeToString(original)))
    }
}
