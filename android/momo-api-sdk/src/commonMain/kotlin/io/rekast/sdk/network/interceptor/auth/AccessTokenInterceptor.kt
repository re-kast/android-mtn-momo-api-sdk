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
package io.rekast.sdk.network.interceptor.auth

import io.rekast.sdk.Logger
import io.rekast.sdk.network.interfaces.CredentialProvider
import io.rekast.sdk.utils.Constants
import java.io.IOException
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor used to add Bearer Token authentication headers to requests.
 *
 * This interceptor adds the access token to the request headers for endpoints
 * that require Bearer Token Authentication.
 *
 * OAuth2 (consent) **resource** endpoints — those whose path contains the
 * [Constants.EndpointPaths.OAUTH2] segment but not the [Constants.EndpointPaths.TOKEN] segment,
 * e.g. `/{productType}/oauth2/{apiVersion}/userinfo` — are authenticated with the OAuth2 consent
 * token from [CredentialProvider.getOauthAccessToken]. All other endpoints, including the OAuth2
 * **token** endpoint (`/{productType}/oauth2/token/`) which mints that consent token, use the
 * regular API-user Bearer token from [CredentialProvider.getAccessToken].
 *
 * The token is fetched from [CredentialProvider] on every request so that
 * the SDK never holds credential state internally.
 *
 * @param credentialProvider Supplies the access token at request time.
 */
class AccessTokenInterceptor @Inject constructor(private val credentialProvider: CredentialProvider) : Interceptor {

    /**
     * Intercepts the request and adds the Bearer Token header.
     *
     * @param chain The interceptor chain.
     * @return The response after adding the authorization header.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val pathSegments = chain.request().url.pathSegments
        val isOauth2ResourceEndpoint = pathSegments.contains(Constants.EndpointPaths.OAUTH2) &&
            !pathSegments.contains(Constants.EndpointPaths.TOKEN)
        val accessToken =
            if (isOauth2ResourceEndpoint) {
                credentialProvider.getOauthAccessToken()
            } else {
                credentialProvider.getAccessToken()
            }

        val request = chain.request().newBuilder()

        if (accessToken.isNotEmpty()) {
            Logger.d("AccessToken", "Access Token: $accessToken")
            return chain.proceed(
                request.header(Constants.Headers.AUTHORIZATION, "${Constants.TokenTypes.BEARER} $accessToken")
                    .build()
            )
        }

        return chain.proceed(request.build())
    }
}
