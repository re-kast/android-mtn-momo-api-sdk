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

/**
 * Request parameters for a backchannel (CIBA) authorization call.
 *
 * @property loginHint The account identifier hint in the format `ID:{msisdn}/MSISDN` (e.g. `ID:563667/MSISDN`).
 * @property scope The OAuth2 scope being requested (e.g., `profile openid`).
 * @property accessType The access type for the token (`online` or `offline`).
 */
data class BcAuthorizeRequest(val loginHint: String, val scope: String, val accessType: String)
