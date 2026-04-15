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

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Represents the callback host for the MTN MOMO API.
 *
 * This class is used to define the provider's callback host URL,
 * which is necessary for handling responses from the MTN MOMO API.
 *
 * @property providerCallbackHost The callback host for the provider, represented as a nullable String.
 */
@Serializable
data class ProviderCallBackHost(
    @SerializedName("providerCallbackHost") val providerCallbackHost: String? = null
)
