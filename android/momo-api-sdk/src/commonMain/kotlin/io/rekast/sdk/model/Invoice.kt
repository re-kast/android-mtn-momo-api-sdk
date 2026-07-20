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
 * Represents a Collection invoice request sent to the MTN MOMO Invoice API.
 *
 * An invoice is a payment request linked to a specific intended payer that expires after
 * [validityDuration] seconds. The payer is prompted to approve the invoice from their mobile wallet.
 *
 * @property externalId Merchant-assigned reference used to correlate the invoice on the integrator side.
 * @property amount The invoice amount as a string.
 * @property currency The ISO 4217 currency code (e.g., `"EUR"`, `"UGX"`). Use `"EUR"` on sandbox.
 * @property validityDuration Seconds until the invoice expires; defaults to the product's configured TTL when null.
 * @property intendedPayer The [Party] that is expected to pay the invoice; optional.
 * @property payerMessage A short message visible to the payer in their wallet notification.
 * @property payeeNote A note to the payee describing the invoice purpose.
 * @property description Human-readable description of the goods or services being invoiced.
 */
@Serializable
data class Invoice(
    @SerialName("externalId") val externalId: String,
    @SerialName("amount") val amount: String,
    @SerialName("currency") val currency: String,
    @SerialName("validityDuration") val validityDuration: String? = null,
    @SerialName("intendedPayer") val intendedPayer: Party? = null,
    @SerialName("payerMessage") val payerMessage: String? = null,
    @SerialName("payeeNote") val payeeNote: String? = null,
    @SerialName("description") val description: String? = null
)
