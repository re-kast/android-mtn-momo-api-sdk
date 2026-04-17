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
 * Represents a MTN MOMO transaction used for payments, withdrawals, deposits, transfers, and refunds.
 *
 * @property amount The transaction amount as a string.
 * @property currency The ISO currency code for the transaction (e.g., EUR, UGX).
 * @property financialTransactionId The unique financial transaction ID assigned by the MTN MOMO system, populated after the transaction completes.
 * @property externalId A caller-assigned external reference ID for correlating the transaction on the integrator side.
 * @property payee The account holder receiving the funds; populated for disbursement and transfer operations.
 * @property payer The account holder sending the funds; populated for collection operations.
 * @property payerMessage A message visible to the payer describing the purpose of the transaction.
 * @property payeeNote A note visible to the payee describing the purpose of the transaction.
 * @property status The current status of the transaction (see [io.rekast.sdk.utils.TransactionStatus]).
 * @property reason The failure reason when the transaction status is FAILED.
 * @property referenceIdToRefund The reference ID of the original transaction to refund; used only in refund operations.
 */
@Serializable
data class MomoTransaction(
    @SerialName("amount") var amount: String,
    @SerialName("currency") var currency: String,
    @SerialName("financialTransactionId") var financialTransactionId: String? = "",
    @SerialName("externalId") var externalId: String,
    @SerialName("payee") var payee: AccountHolder? = null,
    @SerialName("payer") var payer: AccountHolder? = null,
    @SerialName("payerMessage") var payerMessage: String,
    @SerialName("payeeNote") var payeeNote: String,
    @SerialName("status") var status: String? = "",
    @SerialName("reason") var reason: String? = "",
    @SerialName("referenceIdToRefund") var referenceIdToRefund: String? = ""
)
