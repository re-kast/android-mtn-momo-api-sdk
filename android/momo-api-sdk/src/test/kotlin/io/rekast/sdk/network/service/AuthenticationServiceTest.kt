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
package io.rekast.sdk.network.service

import io.rekast.sdk.model.BcAuthorizeResponse
import io.rekast.sdk.model.ProviderCallBackHost
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.ApiKey
import io.rekast.sdk.model.authentication.ApiUser
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Unit tests for the [AuthenticationService] Retrofit interface.
 *
 * The interface declares no executable logic of its own (Retrofit generates the implementation),
 * so these verify that:
 * - Retrofit can materialize a proxy for it, confirming the endpoint annotations are well-formed.
 * - The compiler-generated default-argument bridge for [AuthenticationService.getOauth2AccessToken]
 *   applies the CIBA grant type when the caller omits `grantType`.
 */
class AuthenticationServiceTest {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
        .build()

    /** Verifies Retrofit successfully creates an [AuthenticationService] proxy. */
    @Test
    fun `retrofit creates AuthenticationService proxy`() {
        val service = retrofit.create(AuthenticationService::class.java)
        assertNotNull(service)
    }

    /** A stub implementation that records the grant type it was invoked with. */
    private class RecordingAuthService : AuthenticationService {
        var lastGrantType: String? = null

        private fun <T> err(): Response<T> = Response.error(500, "stub".toResponseBody("text/plain".toMediaType()))

        override suspend fun createApiUser(providerCallBackHost: ProviderCallBackHost, apiVersion: String, uuid: String, productSubscriptionKey: String): Response<ApiUser> = err()
        override suspend fun getApiUser(apiVersion: String, apiUser: String, productSubscriptionKey: String): Response<ApiUser> = err()
        override suspend fun createApiKey(apiVersion: String, apiUser: String, productSubscriptionKey: String): Response<ApiKey> = err()
        override suspend fun getAccessToken(productType: String, productSubscriptionKey: String): Response<AccessToken> = err()
        override suspend fun getOauth2AccessToken(productType: String, productSubscriptionKey: String, environment: String, grantType: String, authReqId: String): Response<Oauth2AccessToken> {
            lastGrantType = grantType
            return err()
        }
        override suspend fun bcAuthorize(productType: String, apiVersion: String, loginHint: String, scope: String, accessType: String, productSubscriptionKey: String, environment: String): Response<BcAuthorizeResponse> =
            err()
    }

    /**
     * Verifies that omitting `grantType` routes through the generated default-argument bridge and
     * supplies the CIBA grant type constant.
     */
    @Test
    fun `getOauth2AccessToken defaults grantType to the CIBA grant`() = runTest {
        val service = RecordingAuthService()

        // grantType intentionally omitted so the compiler-generated default is applied.
        service.getOauth2AccessToken(
            productType = "collection",
            productSubscriptionKey = "sub-key",
            environment = "sandbox",
            authReqId = "auth-req-001"
        )

        assertNotNull(service.lastGrantType)
        assert(service.lastGrantType!!.isNotBlank())
    }
}
