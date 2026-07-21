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
import kotlinx.serialization.Transient

/**
 * Data class representing basic user information (KYC payload) returned by the MTN MOMO API.
 *
 * This is a superset covering both response shapes the SDK encounters:
 * - The Collection/Disbursement `basicuserinfo` response, which is OIDC-style and includes
 *   [sub], [name], [gender], and [updatedAt].
 * - The Remittance `.../accountholder/msisdn/{accountHolderMSISDN}/basicuserinfo` response,
 *   which is the KYC payload of [givenName], [familyName], [birthDate], [locale], and [status]
 *   and omits [sub]/[name].
 *
 * Every server-supplied field is therefore nullable, so a payload that omits any of them
 * deserializes without failure.
 *
 * @property sub The subject identifier for the user. Absent in the Remittance KYC response.
 * @property name The full name of the user. Absent in the Remittance KYC response.
 * @property givenName Given name(s) or first name(s) of the End-User. Some cultures allow multiple
 *   given names; all can be present, separated by space characters.
 * @property familyName Surname(s) or last name(s) of the End-User. Some cultures allow multiple
 *   family names or none; all present names are separated by space characters.
 * @property birthDate The account holder's birth date.
 * @property locale The End-User's locale as a BCP47 [RFC5646] language tag — typically an ISO 639-1
 *   Alpha-2 language code in lowercase and an ISO 3166-1 Alpha-2 country code in uppercase, separated
 *   by a dash (e.g. `en-US` or `fr-CA`). Some implementations use an underscore instead (e.g. `en_US`).
 * @property gender The gender of the user.
 * @property status The account holder status.
 * @property updatedAt The last updated timestamp as a Unix epoch integer (seconds).
 * @property displayUpdatedAt A human-readable formatted version of [updatedAt]; computed locally,
 *   not present in the API response.
 */
@Serializable
data class BasicUserInfo(
    @SerialName("sub") var sub: String? = null,
    @SerialName("name") var name: String? = null,
    @SerialName("given_name") var givenName: String? = null,
    @SerialName("family_name") var familyName: String? = null,
    @SerialName("birthdate") var birthDate: String? = null,
    @SerialName("locale") var locale: String? = null,
    @SerialName("gender") var gender: String? = null,
    @SerialName("status") var status: String? = null,
    @SerialName("updated_at") var updatedAt: Int? = null,
    @Transient var displayUpdatedAt: String = ""
)
