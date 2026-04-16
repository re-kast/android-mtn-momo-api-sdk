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
package io.rekast.sdk.network.interfaces.auth

/**
 * Interface for managing authentication credentials.
 *
 * This interface defines methods for setting, clearing, and checking the status of
 * basic authentication and access token credentials.
 */
interface AuthInterface {
    /**
     * Set the basic authentication credentials.
     *
     * @param apiUserId The API user ID.
     * @param apiKey The API key.
     */
    fun setBasicAuthCredentials(apiUserId: String, apiKey: String)

    /**
     * Set the access token credentials.
     *
     * @param accessToken The access token.
     */
    fun setAccessTokenCredentials(accessToken: String)

    /**
     * Clear all authentication credentials.
     */
    fun clearCredentials()

    /**
     * Check if basic auth credentials are set.
     *
     * @return Boolean indicating if basic auth is configured.
     */
    fun hasBasicAuth(): Boolean

    /**
     * Check if access token is set and valid.
     *
     * @return Boolean indicating if access token is valid.
     */
    fun hasValidAccessToken(): Boolean
}
