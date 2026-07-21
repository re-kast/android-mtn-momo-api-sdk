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
 * Represents the status of a Collection invoice, returned by `getInvoiceStatus`.
 *
 * [status] is the typed [StatusTypes] enum, [errorReason] reuses [ErrorResponse] (`code` and
 * `message`), and [intendedPayer] reuses [Party] rather than redeclaring those shapes. Every field
 * is optional so partial payloads never break deserialization.
 *
 * @property referenceId The reference id for this invoice.
 * @property externalId An external transaction id tied to the payment.
 * @property amount The invoice amount.
 * @property currency The ISO 4217 currency used in this invoice.
 * @property status The invoice status (`CREATED`, `PENDING`, `SUCCESSFUL`, `FAILED`).
 * @property paymentReference A unique id that identifies a pending invoice.
 * @property invoiceId An id for the invoice.
 * @property expiryDateTime When the invoice expires, in `YYYY-MM-DDTHH:mm:ss` format.
 * @property payeeFirstName First name of the payee in this invoice.
 * @property payeeLastName Surname of the payee in this invoice.
 * @property errorReason The failure reason (`code` and `message`) when the invoice did not succeed.
 * @property intendedPayer The [Party] expected to pay the invoice.
 * @property description An optional description of the invoice.
 */
@Serializable
data class InvoiceStatus(
    @SerialName("referenceId") val referenceId: String? = null,
    @SerialName("externalId") val externalId: String? = null,
    @SerialName("amount") val amount: String? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("status") val status: StatusTypes? = null,
    @SerialName("paymentReference") val paymentReference: String? = null,
    @SerialName("invoiceId") val invoiceId: String? = null,
    @SerialName("expiryDateTime") val expiryDateTime: String? = null,
    @SerialName("payeeFirstName") val payeeFirstName: String? = null,
    @SerialName("payeeLastName") val payeeLastName: String? = null,
    @SerialName("errorReason") val errorReason: ErrorResponse? = null,
    @SerialName("intendedPayer") val intendedPayer: Party? = null,
    @SerialName("description") val description: String? = null
)
