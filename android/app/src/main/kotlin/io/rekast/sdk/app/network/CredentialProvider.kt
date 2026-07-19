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
package io.rekast.sdk.app.network

import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.SampleConfig

/**
 * App implementation of [CredentialProvider].
 *
 * Reads credentials from [CredentialStorage] (backed by `EncryptedSharedPreferences`) so that the
 * SDK's interceptors always receive the most recent values without the SDK needing to store or
 * manage credentials itself.
 *
 * The API key is only returned when there is no valid access token, ensuring that Basic Auth
 * headers are attached only during the token-exchange phase and Bearer token is used thereafter.
 */
class CredentialProvider(
    private val storage: CredentialStorage,
    private val sampleConfig: SampleConfig
) : CredentialProvider {
    /** Returns the API user ID from static app configuration. */
    override fun getApiUserId(): String = sampleConfig.apiUserId

    /**
     * Returns the API key for Basic Authentication, or an empty string when a valid Bearer
     * access token is already available.
     *
     * Returning an empty string when a token exists prevents [io.rekast.sdk.network.interceptor.auth.BasicAuthenticationInterceptor]
     * from attaching a Basic Auth header to Bearer-protected requests. Once the access token
     * expires, [CredentialStorage.getAccessToken] returns an empty string, which causes this
     * method to return the stored API key — the next request then goes out without a Bearer
     * header, the server responds with 401, and [TokenAuthenticator] fires to refresh the token.
     */
    override fun getApiKey(): String {
        // Suppress the API key once a valid (non-expired) access token exists.
        // This prevents Basic Auth from being added to Bearer-protected requests.
        return if (storage.getAccessToken().isBlank()) storage.getApiKey() else ""
    }

    /**
     * Returns the current Bearer access token from [CredentialStorage], or an empty string if
     * it has expired or has not yet been obtained.
     */
    override fun getAccessToken(): String = storage.getAccessToken()

    /**
     * Returns the current OAuth2 (consent) access token from [CredentialStorage], or an empty
     * string if it has expired or has not yet been obtained. Used to authenticate OAuth2 endpoints
     * such as `GET /{productType}/oauth2/{apiVersion}/userinfo`.
     */
    override fun getOauthAccessToken(): String = storage.getOauthAccessToken()
}
