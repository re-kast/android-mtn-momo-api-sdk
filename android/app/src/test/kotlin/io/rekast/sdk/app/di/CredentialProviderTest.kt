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
package io.rekast.sdk.app.di

import io.mockk.every
import io.mockk.mockk
import io.rekast.sdk.app.network.CredentialProvider
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.SampleConfig
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CredentialProvider].
 *
 * Verifies the credential-selection logic:
 * - [CredentialProvider.getApiUserId] always delegates to [SampleConfig.apiUserId].
 * - [CredentialProvider.getApiKey] suppresses the API key (returns `""`) once a valid
 *   access token exists, so Basic Auth is not added to Bearer-protected requests.
 * - [CredentialProvider.getAccessToken] always delegates to [CredentialStorage.getAccessToken].
 */
class CredentialProviderTest {
    private val mockStorage: CredentialStorage = mockk(relaxed = true)

    private val sampleConfig =
        SampleConfig(
            apiVersionV1 = "v1_0",
            apiVersionV2 = "v2_0",
            environment = "sandbox",
            providerCallbackHost = "example.com",
            apiUserId = "test-user-id",
            collectionPrimaryKey = "col-primary",
            collectionSecondaryKey = "col-secondary",
            remittancePrimaryKey = "rem-primary",
            remittanceSecondaryKey = "rem-secondary",
            disbursementsPrimaryKey = "dis-primary",
            disbursementsSecondaryKey = "dis-secondary"
        )

    private lateinit var credentialProvider: CredentialProvider

    @Before
    fun setUp() {
        credentialProvider = CredentialProvider(mockStorage, sampleConfig)
    }

    /** Verifies that [CredentialProvider.getApiUserId] returns the value from [SampleConfig]. */
    @Test
    fun `getApiUserId returns value from sampleConfig`() {
        assertEquals("test-user-id", credentialProvider.getApiUserId())
    }

    /**
     * Verifies that [CredentialProvider.getApiKey] returns an empty string when a valid
     * (non-blank) access token already exists in storage, preventing Basic Auth from being
     * added to Bearer-protected requests.
     */
    @Test
    fun `getApiKey returns empty string when access token is not blank`() {
        every { mockStorage.getAccessToken() } returns "valid-token"

        assertEquals("", credentialProvider.getApiKey())
    }

    /**
     * Verifies that [CredentialProvider.getApiKey] returns the stored API key when the access
     * token is blank, enabling Basic Auth for the token-exchange phase.
     */
    @Test
    fun `getApiKey returns stored API key when access token is blank`() {
        every { mockStorage.getAccessToken() } returns ""
        every { mockStorage.getApiKey() } returns "stored-api-key"

        assertEquals("stored-api-key", credentialProvider.getApiKey())
    }

    /**
     * Verifies that a whitespace-only access token is treated as blank and the stored API key
     * is returned, matching the [String.isBlank] contract used in the implementation.
     */
    @Test
    fun `getApiKey returns stored API key when access token is whitespace only`() {
        every { mockStorage.getAccessToken() } returns "   "
        every { mockStorage.getApiKey() } returns "stored-api-key-2"

        assertEquals("stored-api-key-2", credentialProvider.getApiKey())
    }

    /** Verifies that [CredentialProvider.getAccessToken] delegates to [CredentialStorage]. */
    @Test
    fun `getAccessToken delegates to storage`() {
        every { mockStorage.getAccessToken() } returns "bearer-token-value"

        assertEquals("bearer-token-value", credentialProvider.getAccessToken())
    }

    /** Verifies that [CredentialProvider.getAccessToken] returns empty string when storage has no token. */
    @Test
    fun `getAccessToken returns empty string when storage has no token`() {
        every { mockStorage.getAccessToken() } returns ""

        assertEquals("", credentialProvider.getAccessToken())
    }
}
