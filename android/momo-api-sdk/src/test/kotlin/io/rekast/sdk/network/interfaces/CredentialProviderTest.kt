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
package io.rekast.sdk.network.interfaces

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the [CredentialProvider] interface.
 *
 * Covers the abstract accessors via a minimal implementation and, importantly, the default
 * [CredentialProvider.getOauthAccessToken] method — implementations that do not use consent flows
 * inherit the empty-string default, and this verifies that contract.
 */
class CredentialProviderTest {

    /** A minimal implementation that overrides only the required accessors, inheriting the default OAuth token. */
    private class NoConsentCredentialProvider : CredentialProvider {
        override fun getApiUserId(): String = "user-id"
        override fun getApiKey(): String = "api-key"
        override fun getAccessToken(): String = "access-token"
    }

    /** Verifies the required accessors return the implementing values. */
    @Test
    fun `required accessors return implementation values`() {
        val provider: CredentialProvider = NoConsentCredentialProvider()
        assertEquals("user-id", provider.getApiUserId())
        assertEquals("api-key", provider.getApiKey())
        assertEquals("access-token", provider.getAccessToken())
    }

    /** Verifies the default getOauthAccessToken implementation returns an empty string when not overridden. */
    @Test
    fun `getOauthAccessToken defaults to empty string`() {
        val provider: CredentialProvider = NoConsentCredentialProvider()
        assertEquals("", provider.getOauthAccessToken())
    }

    /** Verifies an implementation may override getOauthAccessToken to supply a consent token. */
    @Test
    fun `getOauthAccessToken can be overridden`() {
        val provider = object : CredentialProvider {
            override fun getApiUserId(): String = ""
            override fun getApiKey(): String = ""
            override fun getAccessToken(): String = ""
            override fun getOauthAccessToken(): String = "consent-token"
        }
        assertEquals("consent-token", provider.getOauthAccessToken())
    }
}
