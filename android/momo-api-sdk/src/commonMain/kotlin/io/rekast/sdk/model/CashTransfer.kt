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

import io.rekast.sdk.utils.PayerIdentificationType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a Remittance cash transfer request sent to the MTN MOMO Cash Transfer V2 API.
 *
 * The cash transfer endpoint (`/remittance/v2_0/cashtransfer`) replaces the legacy V1 transfer
 * for remittance use cases. It extends the standard transfer payload with optional KYC fields
 * about the sending party, enabling cross-border compliance requirements where the payer is not
 * a registered MTN mobile money subscriber.
 *
 * @property amount The transfer amount as a string.
 * @property currency The ISO 4217 currency code for the transaction (e.g., `"EUR"`, `"UGX"`).
 * @property externalId Merchant-assigned reference used to correlate the transfer on the integrator side.
 * @property payee The [Party] receiving the funds.
 * @property payerMessage A message visible to the payer describing the purpose of the transfer.
 * @property payeeNote A note visible to the payee describing the purpose of the transfer.
 * @property payerIdentificationType Type of identification document (see [PayerIdentificationType]).
 * @property payerIdentificationNumber The identification document number matching [payerIdentificationType].
 * @property payerIdentity The MSISDN of the sending party (payer).
 * @property payerFirstName First name of the sending party.
 * @property payerSurName Surname of the sending party.
 * @property payerLanguageCode ISO 639-1 two-letter language code for the payer (e.g., `"en"`).
 * @property payerEmail Email address of the sending party.
 * @property payerMsisdn Phone number of the sending party.
 * @property payerGender Gender code of the sending party (per ISO 20022).
 * @property originatingCountry ISO country code of the country from which the funds originate.
 *   Serialized as `orginatingCountry` to match the (misspelled) MTN MOMO API field name.
 * @property originalAmount The amount in the originating currency before conversion.
 * @property originalCurrency ISO 4217 currency code of the originating amount.
 */
@Serializable
data class CashTransfer(
    @SerialName("amount") val amount: String,
    @SerialName("currency") val currency: String,
    @SerialName("externalId") val externalId: String,
    @SerialName("payee") val payee: Party,
    @SerialName("payerMessage") val payerMessage: String,
    @SerialName("payeeNote") val payeeNote: String,
    @SerialName("payerIdentificationType") val payerIdentificationType: PayerIdentificationType? = null,
    @SerialName("payerIdentificationNumber") val payerIdentificationNumber: String? = null,
    @SerialName("payerIdentity") val payerIdentity: String? = null,
    @SerialName("payerFirstName") val payerFirstName: String? = null,
    @SerialName("payerSurName") val payerSurName: String? = null,
    @SerialName("payerLanguageCode") val payerLanguageCode: String? = null,
    @SerialName("payerEmail") val payerEmail: String? = null,
    @SerialName("payerMsisdn") val payerMsisdn: String? = null,
    @SerialName("payerGender") val payerGender: String? = null,
    @SerialName("orginatingCountry") val originatingCountry: String? = null,
    @SerialName("originalAmount") val originalAmount: String? = null,
    @SerialName("originalCurrency") val originalCurrency: String? = null
)
