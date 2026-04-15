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
package io.rekast.sdk.model.authentication

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * This class represents the OAuth2 access token used for authentication with the MTN MOMO API.
 *
 * @property accessToken The access token string used to authenticate API requests.
 * @property tokenType The type of the token (e.g., Bearer).
 * @property expiresIn The duration in seconds for which the access token is valid.
 * @property scope The scope of access granted by the token.
 * @property refreshToken The token used to obtain a new access token when the current one expires.
 * @property refreshTokenExpiredIn The duration in seconds for which the refresh token is valid.
 */
@Serializable
data class Oauth2AccessToken(
    @SerializedName("access_token") var accessToken: String,
    @SerializedName("token_type") var tokenType: String,
    @SerializedName("expires_in") var expiresIn: String,
    @SerializedName("scope") var scope: String,
    @SerializedName("refresh_token") var refreshToken: String,
    @SerializedName("refresh_token_expired_in") var refreshTokenExpiredIn: String
)
