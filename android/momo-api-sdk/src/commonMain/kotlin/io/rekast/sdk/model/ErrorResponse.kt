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
 * Represents an error response returned by the MTN MOMO API.
 *
 * @property code The error code returned by the API (maps to [io.rekast.sdk.utils.ApiErrorResponses]).
 * @property message A human-readable description of the error.
 * @property error The raw error identifier string from the API. Optional — many MoMo error bodies
 *   contain only `code` and `message`.
 */
@Serializable
data class ErrorResponse(@SerialName("code") var code: String, @SerialName("message") var message: String, @SerialName("error") var error: String? = null)
