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

import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.utils.ApiConfig

/**
 * A [CredentialProvider] used exclusively by the token-refresh [AuthenticationService] wired in
 * [NetworkModule.provideTokenRefreshAuthenticationService].
 *
 * It always returns the raw API key and never returns a Bearer access token, ensuring the
 * [io.rekast.sdk.network.interceptor.auth.BasicAuthenticationInterceptor] always attaches the Basic
 * Auth header on token-exchange requests — even when an (expired) access token is still present in
 * [CredentialStorage].
 *
 * @param config Supplies the API user ID.
 * @param storage Supplies the current API key at request time.
 */
internal class TokenRefreshCredentialProvider(
    private val config: ApiConfig,
    private val storage: CredentialStorage
) : CredentialProvider {
    override fun getApiUserId(): String = config.apiUserId

    override fun getApiKey(): String = storage.getApiKey()

    override fun getAccessToken(): String = ""
}
