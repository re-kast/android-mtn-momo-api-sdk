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
package io.rekast.sdk.model.authentication

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * This class represents the MTN MOMO API Key that is received by using the
 * Subscription Key and the API User ID
 */
@Serializable
data class ApiUserKey(
    @SerializedName("apiKey") val apiKey: String
)
