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
package io.rekast.sdk.network.interfaces

/**
 * Supplies runtime credentials to the SDK's authentication interceptors.
 *
 * The SDK never stores or manages credentials itself. Implement this interface
 * in your application and provide it via DI so that the interceptors can
 * retrieve the current API user ID, API key, and access token on every request.
 *
 * Implementations are responsible for storage (e.g. SharedPreferences, DataStore)
 * and for any expiry logic.
 */
interface CredentialProvider {
    /** Returns the API user ID, or an empty string if not available. */
    fun getApiUserId(): String

    /**
     * Returns the API key used for Basic Authentication, or an empty string if not available.
     *
     * Return an empty string once a valid access token exists so that Basic Auth
     * headers are no longer attached to non-token-exchange requests.
     */
    fun getApiKey(): String

    /** Returns the current Bearer access token, or an empty string if not available or expired. */
    fun getAccessToken(): String
}
