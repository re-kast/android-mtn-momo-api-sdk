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
 * Represents the status of a Disbursements refund, returned by `getRefundStatus`
 * (`GET /disbursement/{apiVersion}/refund/{referenceId}`).
 *
 * [status] is the typed [StatusTypes] enum (`PENDING`, `SUCCESSFUL`, or `FAILED` for this endpoint),
 * [payee] reuses the [Party] model, and [reason] reuses [ErrorResponse] (`code` and `message`) rather
 * than redeclaring those shapes. Every field is optional so partial payloads never break
 * deserialization.
 *
 * @property amount The amount that was refunded.
 * @property currency The ISO 4217 currency code for the transaction.
 * @property financialTransactionId The financial transaction id from the mobile money manager,
 *   connecting to the specific financial transaction made in the account.
 * @property externalId The external id used as a reference for reconciliation; need not be unique.
 * @property payee The [Party] credited by the refund.
 * @property payerMessage The message written to the payer's transaction history.
 * @property payeeNote The note written to the payee's transaction history.
 * @property status The refund lifecycle status.
 * @property reason The failure reason (`code` and `message`) when the refund did not succeed.
 */
@Serializable
data class RefundStatus(
    @SerialName("amount") val amount: String? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("financialTransactionId") val financialTransactionId: String? = null,
    @SerialName("externalId") val externalId: String? = null,
    @SerialName("payee") val payee: Party? = null,
    @SerialName("payerMessage") val payerMessage: String? = null,
    @SerialName("payeeNote") val payeeNote: String? = null,
    @SerialName("status") val status: StatusTypes? = null,
    @SerialName("reason") val reason: ErrorResponse? = null
)
