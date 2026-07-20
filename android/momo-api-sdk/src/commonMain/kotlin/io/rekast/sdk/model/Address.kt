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
 * Represents the nested `address` object returned within [UserInfoWithConsent] by the MTN MOMO
 * `GET /oauth2/{version}/userinfo` endpoint.
 *
 * All fields are nullable with a `null` default so that a missing field does not cause a
 * deserialization failure.
 *
 * @property formatted The full address rendered as a single, newline-separated string.
 * @property streetAddress The street address portion (e.g. "Street 17").
 * @property postalCode The postal / ZIP code.
 * @property locality The city or locality.
 * @property region The region, state, or province.
 * @property country The country name.
 */
@Serializable
data class Address(
    @SerialName("formatted") var formatted: String? = null,
    @SerialName("street_address") var streetAddress: String? = null,
    @SerialName("postal_code") var postalCode: String? = null,
    @SerialName("locality") var locality: String? = null,
    @SerialName("region") var region: String? = null,
    @SerialName("country") var country: String? = null
)
