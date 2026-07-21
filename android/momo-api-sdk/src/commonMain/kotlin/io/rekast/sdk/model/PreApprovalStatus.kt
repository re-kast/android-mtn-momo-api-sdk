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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the status of a single Collection pre-approval, returned by `getPreApprovalStatus`.
 *
 * [status] is the typed [StatusTypes] enum (`PENDING`, `SUCCESSFUL`, `FAILED`). Reuses
 * [Party] for [payer] and [ErrorResponse] for the [reason] object (both `code` and
 * `message`) rather than redeclaring those shapes. Every field is optional so partial payloads never
 * break deserialization.
 *
 * @property payer The account holder that authorised the pre-approval.
 * @property payerCurrency The ISO 4217 currency of the payer's account.
 * @property payerMessage The message shown to the payer.
 * @property status The pre-approval status (`PENDING`, `SUCCESSFUL`, or `FAILED`).
 * @property expirationDateTime The expiry as an epoch value; `0` when not set.
 * @property reason The failure reason (`code` and `message`) when the pre-approval was not approved.
 */
@Serializable
data class PreApprovalStatus(
    @SerialName("payer") val payer: Party? = null,
    @SerialName("payerCurrency") val payerCurrency: String? = null,
    @SerialName("payerMessage") val payerMessage: String? = null,
    @SerialName("status") val status: StatusTypes? = null,
    @SerialName("expirationDateTime") val expirationDateTime: Long? = null,
    @SerialName("reason") val reason: ErrorResponse? = null
)
