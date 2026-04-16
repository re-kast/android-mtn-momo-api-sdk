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
import kotlinx.serialization.Transient

/**
 * Data class representing basic user information returned by the MTN MOMO API.
 *
 * @property sub The subject identifier for the user.
 * @property name The full name of the user.
 * @property givenName The given name of the user.
 * @property familyName The family name of the user.
 * @property birthDate The birthdate of the user.
 * @property locale The locale of the user.
 * @property gender The gender of the user.
 * @property updatedAt The last updated timestamp as a Unix epoch integer (seconds).
 * @property displayUpdatedAt A human-readable formatted version of [updatedAt]; computed locally,
 *   not present in the API response.
 */
@Serializable
data class BasicUserInfo(
    @SerialName("sub") var sub: String,
    @SerialName("name") var name: String,
    @SerialName("given_name") var givenName: String,
    @SerialName("family_name") var familyName: String,
    @SerialName("birthdate") var birthDate: String,
    @SerialName("locale") var locale: String,
    @SerialName("gender") var gender: String,
    @SerialName("updated_at") var updatedAt: Int,
    @Transient var displayUpdatedAt: String = ""
)
