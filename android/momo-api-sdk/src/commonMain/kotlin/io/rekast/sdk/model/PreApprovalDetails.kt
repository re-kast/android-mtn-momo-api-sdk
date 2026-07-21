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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single pre-approval record returned by the MTN MOMO Collection API, e.g. an entry in
 * the list from `getApprovedPreApprovals`.
 *
 * The fields the API guarantees (per the schema) are non-nullable; every other field is optional and
 * defaults to `null`, so partial payloads never break deserialization.
 *
 * @property preApprovalId The ID of the pre-approval.
 * @property toFri The Financial Resource Identifier of the receiving account.
 * @property fromFri The Financial Resource Identifier of the sending account.
 * @property fromCurrency The ISO 4217 currency of the sending account holder.
 * @property createdTime The ISO 8601 date-time at which the pre-approval was created.
 * @property status The pre-approval status ([StatusTypes]: APPROVED, CANCELLED, EXPIRED, REJECTED, PENDING).
 * @property message The pre-approval message.
 * @property approvedTime The ISO 8601 date-time at which the pre-approval was approved.
 * @property expiryTime The ISO 8601 date-time at which the pre-approval expires.
 * @property frequency How often the pre-approval may be used ([FrequencyTypes]: DAILY, WEEKLY, MONTHLY).
 * @property startDate The start date of the pre-approval.
 * @property lastUsedDate The ISO 8601 date-time the pre-approval was last used.
 * @property offer The offer description.
 * @property externalId The external reference ID.
 * @property maxDebitAmount The maximum debit amount allowed (a non-negative amount).
 */
@Serializable
data class PreApprovalDetails(
    @SerialName("preApprovalId") val preApprovalId: String,
    @SerialName("toFri") val toFri: String,
    @SerialName("fromFri") val fromFri: String,
    @SerialName("fromCurrency") val fromCurrency: String,
    @SerialName("createdTime") val createdTime: String,
    @SerialName("status") val status: StatusTypes,
    @SerialName("message") val message: String,
    @SerialName("approvedTime") val approvedTime: String? = null,
    @SerialName("expiryTime") val expiryTime: String? = null,
    @SerialName("frequency") val frequency: FrequencyTypes? = null,
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("lastUsedDate") val lastUsedDate: String? = null,
    @SerialName("offer") val offer: String? = null,
    @SerialName("externalId") val externalId: String? = null,
    @SerialName("maxDebitAmount") val maxDebitAmount: String? = null
)
