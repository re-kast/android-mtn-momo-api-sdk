/*
 * Copyright 2023-2024, Benjamin Mwalimu
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
 * Identifies a party (payer or payee) in a MTN MOMO transaction.
 *
 * @property partyIdType The type of identifier used, corresponding to an [io.rekast.sdk.utils.AccountHolderType] value (e.g., "msisdn", "email").
 * @property partyId The actual identifier value for the party (e.g., a phone number or email address).
 */
@Serializable
data class AccountHolder(@SerialName("partyIdType") var partyIdType: String, @SerialName("partyId") var partyId: String)
