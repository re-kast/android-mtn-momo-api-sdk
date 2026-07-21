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
package io.rekast.sdk.utils

import kotlinx.serialization.Serializable

/**
 * Enum class representing the different party identifier types in the MTN MOMO system.
 *
 * Used as the typed `partyIdType` on [io.rekast.sdk.model.Party]. Serialized by constant name
 * (e.g. `MSISDN`) to match the API body format; the lowercase [partyType] is used for URL path
 * segments such as the account-holder validation endpoint.
 *
 * @property partyType The lowercase string representation used in URL path segments.
 */
@Serializable
enum class PartyTypes(val partyType: String) {
    /**
     * Represents a mobile number as the account holder type.
     */
    MSISDN("msisdn"),

    /**
     * Represents an email address as the account holder type.
     */
    EMAIL("email"),

    /**
     * Represents a party code as the account holder type.
     */
    PARTY_CODE("party_code")
}
