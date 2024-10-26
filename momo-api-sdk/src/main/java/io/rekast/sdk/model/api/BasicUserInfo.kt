/*
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package io.rekast.sdk.model.api

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class BasicUserInfo(
    @SerializedName("sub") var sub: String,
    @SerializedName("name") var name: String,
    @SerializedName("given_name") var givenName: String,
    @SerializedName("family_name") var familyName: String,
    @SerializedName("birthdate") var birthDate: String,
    @SerializedName("locale") var locale: String,
    @SerializedName("gender") var gender: String,
    @SerializedName("updated_at") var updatedAt: String
)
