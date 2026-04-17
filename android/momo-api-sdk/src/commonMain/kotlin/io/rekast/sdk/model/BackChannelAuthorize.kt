/*
 * Copyright 2026, Benjamin Mwalimu
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
 * Represents the response from an account authorization request.
 *
 * This is returned when initiating a BC (Backchannel) authorization flow,
 * containing the details needed to poll for the authorization result.
 *
 * @property authReqId The unique authorization request ID used to poll for the access token.
 * @property interval The minimum polling interval in seconds before checking authorization status.
 * @property expiresIn The number of seconds until the authorization request expires.
 */
@Serializable
data class BackChannelAuthorize(@SerialName("auth_req_id") var authReqId: String, @SerialName("interval") var interval: Int, @SerialName("expires_in") var expiresIn: Int)
