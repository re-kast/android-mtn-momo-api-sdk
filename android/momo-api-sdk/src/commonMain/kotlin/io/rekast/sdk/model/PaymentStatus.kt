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
 * Represents the status of a Collection payment, returned by `getPaymentStatus`.
 *
 * [status] is the typed [StatusTypes] enum, and [reason] reuses [ErrorResponse] (`code` and
 * `message`) rather than redeclaring that shape. Every field is optional so partial payloads never
 * break deserialization.
 *
 * @property referenceId The reference id for this payment.
 * @property status The payment lifecycle status.
 * @property financialTransactionId A transaction id associated with this payment.
 * @property reason The failure reason (`code` and `message`) when the payment did not succeed.
 */
@Serializable
data class PaymentStatus(
    @SerialName("referenceId") val referenceId: String? = null,
    @SerialName("status") val status: StatusTypes? = null,
    @SerialName("financialTransactionId") val financialTransactionId: String? = null,
    @SerialName("reason") val reason: ErrorResponse? = null
)
