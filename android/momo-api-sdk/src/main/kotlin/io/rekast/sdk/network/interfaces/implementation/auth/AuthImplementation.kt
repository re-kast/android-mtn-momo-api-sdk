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
package io.rekast.sdk.network.interfaces.implementation.auth

import io.rekast.sdk.model.authentication.credentials.AccessTokenCredentials
import io.rekast.sdk.model.authentication.credentials.BasicAuthCredentials
import io.rekast.sdk.network.interfaces.auth.AuthInterface
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the [AuthInterface] for managing authentication credentials.
 *
 * This class handles setting and clearing basic authentication and access token credentials.
 *
 * @param basicAuthCredentials The credentials for basic authentication.
 * @param accessTokenCredentials The credentials for access token authentication.
 */
@Singleton
class AuthImplementation @Inject constructor(
    private var basicAuthCredentials: BasicAuthCredentials,
    private var accessTokenCredentials: AccessTokenCredentials
) : AuthInterface {

    /**
     * Sets the basic authentication credentials.
     *
     * @param apiUserId The API user ID.
     * @param apiKey The API key.
     */
    override fun setBasicAuthCredentials(apiUserId: String, apiKey: String) {
        basicAuthCredentials = BasicAuthCredentials(apiUserId, apiKey)
    }

    /**
     * Sets the access token credentials.
     *
     * @param accessToken The access token.
     */
    override fun setAccessTokenCredentials(accessToken: String) {
        accessTokenCredentials = AccessTokenCredentials(accessToken)
    }

    /**
     * Clears all authentication credentials.
     */
    override fun clearCredentials() {
        basicAuthCredentials = BasicAuthCredentials("", "")
        accessTokenCredentials = AccessTokenCredentials("")
    }

    /**
     * Checks if basic authentication credentials are set.
     *
     * @return Boolean indicating if basic auth is configured.
     */
    override fun hasBasicAuth(): Boolean {
        return basicAuthCredentials.apiUserId.isNotEmpty() &&
            basicAuthCredentials.apiKey.isNotEmpty()
    }

    /**
     * Checks if the access token is set and valid.
     *
     * @return Boolean indicating if the access token is valid.
     */
    override fun hasValidAccessToken(): Boolean {
        return accessTokenCredentials.accessToken.isNotEmpty()
        // TODO: Add expiration check logic
    }
}
