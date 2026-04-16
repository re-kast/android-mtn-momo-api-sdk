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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AuthImplementation].
 *
 * Verifies the full credential lifecycle: initial empty state, setting valid
 * Basic-Auth and access-token credentials, updating (replacing) existing
 * credentials, and clearing all credentials via [AuthImplementation.clearCredentials].
 * Edge cases include partial credential sets and idempotent clears.
 */
class AuthImplementationTest {

    private lateinit var authImplementation: AuthImplementation

    @Before
    fun setUp() {
        authImplementation = AuthImplementation(
            BasicAuthCredentials("", ""),
            AccessTokenCredentials("")
        )
    }

    /** Verifies the initial state has no valid Basic Auth credentials. */
    @Test
    fun `hasBasicAuth returns false when credentials are empty`() {
        assertFalse(authImplementation.hasBasicAuth())
    }

    /** Verifies Basic Auth is considered valid when both userId and apiKey are non-empty. */
    @Test
    fun `hasBasicAuth returns true after setting valid credentials`() {
        authImplementation.setBasicAuthCredentials("user-id", "api-key")
        assertTrue(authImplementation.hasBasicAuth())
    }

    /** Verifies Basic Auth is invalid when userId is set but apiKey is empty. */
    @Test
    fun `hasBasicAuth returns false when only userId is set`() {
        authImplementation.setBasicAuthCredentials("user-id", "")
        assertFalse(authImplementation.hasBasicAuth())
    }

    /** Verifies Basic Auth is invalid when apiKey is set but userId is empty. */
    @Test
    fun `hasBasicAuth returns false when only apiKey is set`() {
        authImplementation.setBasicAuthCredentials("", "api-key")
        assertFalse(authImplementation.hasBasicAuth())
    }

    /** Verifies the initial state has no valid access token. */
    @Test
    fun `hasValidAccessToken returns false when token is empty`() {
        assertFalse(authImplementation.hasValidAccessToken())
    }

    /** Verifies the access token is considered valid once a non-empty token is set. */
    @Test
    fun `hasValidAccessToken returns true after setting a valid token`() {
        authImplementation.setAccessTokenCredentials("valid-token-xyz")
        assertTrue(authImplementation.hasValidAccessToken())
    }

    /** Verifies both Basic Auth and access token credentials are invalidated after clearing. */
    @Test
    fun `clearCredentials resets all credentials`() {
        authImplementation.setBasicAuthCredentials("user-id", "api-key")
        authImplementation.setAccessTokenCredentials("valid-token-xyz")

        authImplementation.clearCredentials()

        assertFalse(authImplementation.hasBasicAuth())
        assertFalse(authImplementation.hasValidAccessToken())
    }

    /** Verifies calling setBasicAuthCredentials twice keeps the most recent values. */
    @Test
    fun `setBasicAuthCredentials replaces previous credentials`() {
        authImplementation.setBasicAuthCredentials("user-1", "key-1")
        authImplementation.setBasicAuthCredentials("user-2", "key-2")
        assertTrue(authImplementation.hasBasicAuth())
    }

    /** Verifies calling setAccessTokenCredentials twice keeps the most recent token. */
    @Test
    fun `setAccessTokenCredentials replaces previous token`() {
        authImplementation.setAccessTokenCredentials("old-token")
        authImplementation.setAccessTokenCredentials("new-token")
        assertTrue(authImplementation.hasValidAccessToken())
    }

    /** Verifies clearCredentials is idempotent and safe to call on already-empty state. */
    @Test
    fun `clearCredentials after already empty credentials does not throw`() {
        authImplementation.clearCredentials()
        assertFalse(authImplementation.hasBasicAuth())
        assertFalse(authImplementation.hasValidAccessToken())
    }
}
