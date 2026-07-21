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
package io.rekast.sdk.network.interceptor.headers

import io.rekast.sdk.utils.Constants
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that attaches the `X-Target-Environment` header to every request, sourced once from
 * configuration ([io.rekast.sdk.utils.ApiConfig.environment]) rather than being threaded through
 * every service method as a parameter.
 *
 * The header is required by all product endpoints and by the OAuth2 (`oauth2/token`, `bc-authorize`)
 * endpoints, but **not** by the API-user provisioning and access-token bootstrap endpoints
 * (`createApiUser`, `getApiUser`, `createApiKey`, `getAccessToken`), which are therefore skipped:
 * - any path containing an `apiuser` segment (create/get API user, create API key), and
 * - the plain access-token endpoint `/{productType}/token/` (identified by a final `token` segment
 *   with no `oauth2` segment — the OAuth2 token endpoint `/{productType}/oauth2/token/` still receives it).
 *
 * @param environment The target environment (e.g., `"sandbox"` or `"production"`).
 */
class EnvironmentInterceptor(private val environment: String) : Interceptor {

    /**
     * Adds the `X-Target-Environment` header unless the request targets an auth-bootstrap endpoint
     * that does not accept it.
     *
     * @param chain The interceptor chain.
     * @return The response after conditionally adding the environment header.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val segments = request.url.pathSegments.filter { it.isNotEmpty() }
        val isBootstrapEndpoint = segments.contains("apiuser") ||
            (segments.lastOrNull() == "token" && !segments.contains("oauth2"))

        if (environment.isNotBlank() && !isBootstrapEndpoint) {
            return chain.proceed(
                request.newBuilder()
                    .header(Constants.Headers.X_TARGET_ENVIRONMENT, environment)
                    .build()
            )
        }
        return chain.proceed(request)
    }
}
