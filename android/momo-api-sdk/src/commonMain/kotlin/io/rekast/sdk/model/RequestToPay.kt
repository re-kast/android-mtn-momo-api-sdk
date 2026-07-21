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
 * Request body for the Collection request-to-pay operation, prompting the [payer] to approve a debit.
 *
 * @property amount The amount that will be debited from the payer account.
 * @property currency The ISO 4217 currency code.
 * @property externalId A caller-assigned reference used for reconciliation; need not be unique.
 * @property payer The sending party prompted to approve the payment.
 * @property payee Unused for this operation.
 * @property payerMessage Message written to the payer's transaction history.
 * @property payeeNote Message written to the payee's transaction history.
 */
@Serializable
data class RequestToPay(
    @SerialName("amount") val amount: String,
    @SerialName("currency") val currency: String,
    @SerialName("externalId") val externalId: String,
    @SerialName("payer") val payer: Party? = null,
    @SerialName("payee") val payee: Party? = null,
    @SerialName("payerMessage") val payerMessage: String,
    @SerialName("payeeNote") val payeeNote: String
)
