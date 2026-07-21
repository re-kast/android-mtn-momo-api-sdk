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
 * Represents a monetary amount in a given currency, matching the MTN MOMO API `Money` schema.
 *
 * Both fields are optional so partial payloads never break deserialization.
 *
 * @property amount The amount as a string (e.g., "100.00").
 * @property currency The ISO 4217 currency code (e.g., EUR, UGX).
 */
@Serializable
data class Money(@SerialName("amount") var amount: String? = null, @SerialName("currency") var currency: String? = null)
