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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a Collection pre-approval request sent to the MTN MOMO Pre-approval API.
 *
 * A pre-approval authorises the merchant to debit the [payer]'s account up to an implied amount
 * without requiring explicit per-transaction approval. The authorisation expires after [validityTime]
 * seconds. Subsequent charges against the pre-approval use the standard Request-to-Pay flow.
 *
 * @property payer The [Party] granting the pre-approval.
 * @property payerCurrency The ISO 4217 currency in which the payer's account is denominated (e.g., `"EUR"`).
 * @property payerMessage A short message displayed to the payer during the pre-approval consent prompt.
 * @property validityTime Seconds until the pre-approval expires.
 */
@Serializable
data class PreApproval(
    @SerialName("payer") val payer: Party,
    @SerialName("payerCurrency") val payerCurrency: String,
    @SerialName("payerMessage") val payerMessage: String? = null,
    @SerialName("validityTime") val validityTime: Int
)
