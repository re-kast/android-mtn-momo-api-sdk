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
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.utils.ApiConfig
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [TokenRefreshCredentialProvider], the Basic-Auth-only credential provider used by
 * the token-refresh [io.rekast.sdk.network.service.AuthenticationService].
 */
class TokenRefreshCredentialProviderTest {
    private val config =
        ApiConfig(
            baseUrl = "https://sandbox.momodeveloper.mtn.com/",
            apiUserId = "test-user-id",
            environment = "sandbox"
        )
    private val storage: CredentialStorage = mockk(relaxed = true)

    private val provider = TokenRefreshCredentialProvider(config, storage)

    /** Verifies the API user ID is read from [ApiConfig]. */
    @Test
    fun `getApiUserId returns the configured api user id`() {
        assertEquals("test-user-id", provider.getApiUserId())
    }

    /** Verifies the API key is read from [CredentialStorage] at call time. */
    @Test
    fun `getApiKey returns the stored api key`() {
        every { storage.getApiKey() } returns "stored-api-key"
        assertEquals("stored-api-key", provider.getApiKey())
    }

    /** Verifies the access token is always empty so Basic Auth is always attached on refresh calls. */
    @Test
    fun `getAccessToken always returns empty string`() {
        every { storage.getAccessToken() } returns "some-bearer-token"
        assertEquals("", provider.getAccessToken())
    }
}
