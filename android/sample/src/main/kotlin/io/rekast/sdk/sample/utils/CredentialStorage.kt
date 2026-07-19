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
package io.rekast.sdk.sample.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val PREFS_FILE_NAME = "momo_sdk_secure_prefs"
private const val KEY_API_KEY = "apiKey"
private const val KEY_ACCESS_TOKEN = "accessToken"
private const val KEY_ACCESS_TOKEN_EXPIRY = "accessTokenExpiry"
private const val KEY_ACCESS_TOKEN_TYPE = "accessTokenType"
private const val KEY_OAUTH_ACCESS_TOKEN = "oauthAccessToken"
private const val KEY_OAUTH_ACCESS_TOKEN_EXPIRY = "oauthAccessTokenExpiry"
private const val KEY_OAUTH_ACCESS_TOKEN_TYPE = "oauthAccessTokenType"
private const val KEY_OAUTH_SCOPE = "oauthScope"
private const val KEY_OAUTH_REFRESH_TOKEN = "oauthRefreshToken"
private const val KEY_OAUTH_REFRESH_TOKEN_EXPIRY = "oauthRefreshTokenExpiry"
private const val KEY_BACK_CHANNEL_AUTHORIZATION_REQUEST_ID = "backChannelAuthorizationRequestId"
private const val KEY_BACK_CHANNEL_AUTHORIZATION_REQUEST_ID_EXPIRY = "backChannelAuthorizationRequestIdExpiry"
private const val KEY_LOGIN_HINT = "loginHint"

/**
 * Secure credential storage backed by [EncryptedSharedPreferences].
 *
 * Keys and values are encrypted using AES-256 with the master key stored in
 * the Android Keystore — plain-text credentials never touch disk.
 *
 * Inject this class wherever credentials need to be saved or read.  The SDK
 * itself never touches this class; it reads credentials on demand via
 * [io.rekast.sdk.network.interfaces.CredentialProvider].
 */
@Singleton
class CredentialStorage @Inject constructor(@param:ApplicationContext private val context: Context) {

    init {
        // EncryptedSharedPreferences.create() + MasterKey.Builder.build() are @WorkerThread
        // operations that hit the Android Keystore and can take 1-3 s on first run.
        // Pre-warming here ensures the lock is released before any ViewModel or OkHttp
        // interceptor needs it, preventing an ANR on slow devices.
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch { prefs }
    }

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Persists the API key obtained from the MTN MoMo sandbox/production portal.
     *
     * @param apiKey The raw API key string to store.
     */
    fun saveApiKey(apiKey: String) {
        prefs.edit { putString(KEY_API_KEY, apiKey) }
    }

    /**
     * Returns the stored API key, or an empty string if it was never set.
     */
    fun getApiKey(): String = prefs.getString(KEY_API_KEY, "").orEmpty()

    /**
     * Persists the Bearer access token along with its expiry time and token type.
     *
     * The expiry is calculated from [AccessToken.expiresIn] (seconds) and stored as an absolute
     * epoch millisecond value so that [getAccessToken] can check it without needing to know when
     * the token was first saved.
     *
     * @param token The [AccessToken] received from the MTN MoMo token endpoint, or `null`
     *              (in which case this call is a no-op).
     */
    fun saveAccessToken(token: AccessToken?) {
        token ?: return
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, token.accessToken)
            putLong(KEY_ACCESS_TOKEN_EXPIRY, expiryInSeconds(token.expiresIn))
            putString(KEY_ACCESS_TOKEN_TYPE, token.tokenType)
        }
    }

    /**
     * Returns the stored access token, or an empty string if it has expired or was never set.
     */
    fun getAccessToken(): String {
        val expiry = prefs.getLong(KEY_ACCESS_TOKEN_EXPIRY, 0L)
        return if (isExpired(expiry)) "" else prefs.getString(KEY_ACCESS_TOKEN, "").orEmpty()
    }

    /**
     * Removes all persisted access token fields.
     *
     * Call this to force the next request to go through a fresh token exchange (e.g. after
     * an explicit sign-out or when the server invalidates the token server-side).
     */
    fun clearAccessToken() {
        prefs.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_ACCESS_TOKEN_EXPIRY)
            remove(KEY_ACCESS_TOKEN_TYPE)
        }
    }

    /**
     * Persists the OAuth2 access token, its expiry, token type, scope, and refresh token.
     *
     * Both the access token and the refresh token expiry are stored as absolute epoch
     * millisecond values derived from their respective `expiresIn` fields (seconds).
     *
     * @param token The [Oauth2AccessToken] received from the MTN MoMo OAuth2 token endpoint,
     *              or `null` (in which case this call is a no-op).
     */
    fun saveOauthAccessToken(token: Oauth2AccessToken?) {
        token ?: return
        prefs.edit {
            putString(KEY_OAUTH_ACCESS_TOKEN, token.accessToken)
            putLong(KEY_OAUTH_ACCESS_TOKEN_EXPIRY, expiryInSeconds(token.expiresIn))
            putString(KEY_OAUTH_ACCESS_TOKEN_TYPE, token.tokenType)
            putString(KEY_OAUTH_SCOPE, token.scope)
            putString(KEY_OAUTH_REFRESH_TOKEN, token.refreshToken)
            putLong(KEY_OAUTH_REFRESH_TOKEN_EXPIRY, expiryInSeconds(token.refreshTokenExpiredIn ?: 0))
        }
    }

    /**
     * Returns the stored OAuth2 access token, or an empty string if it has expired or was never set.
     */
    fun getOauthAccessToken(): String {
        val expiry = prefs.getLong(KEY_OAUTH_ACCESS_TOKEN_EXPIRY, 0L)
        return if (isExpired(expiry)) "" else prefs.getString(KEY_OAUTH_ACCESS_TOKEN, "").orEmpty()
    }

    /**
     * Persists the `auth_req_id` from a backchannel authorization response along with its expiry.
     *
     * @param authReqId The authorization request ID returned by the bc-authorize endpoint.
     * @param expiresIn Seconds until the `auth_req_id` expires (from [io.rekast.sdk.model.BackChannelAuthorize.expiresIn]).
     */
    fun saveBackChannelAuthorizationRequestId(authReqId: String, expiresIn: Int) {
        prefs.edit {
            putString(KEY_BACK_CHANNEL_AUTHORIZATION_REQUEST_ID, authReqId)
            putLong(KEY_BACK_CHANNEL_AUTHORIZATION_REQUEST_ID_EXPIRY, expiryInSeconds(expiresIn))
        }
    }

    /**
     * Returns the stored `auth_req_id`, or an empty string if it has expired or was never set.
     */
    fun getBackChannelAuthorizationRequestId(): String {
        val expiry = prefs.getLong(KEY_BACK_CHANNEL_AUTHORIZATION_REQUEST_ID_EXPIRY, 0L)
        return if (isExpired(expiry)) "" else prefs.getString(KEY_BACK_CHANNEL_AUTHORIZATION_REQUEST_ID, "").orEmpty()
    }

    /**
     * Persists the login hint used in backchannel (CIBA) authorization requests.
     *
     * @param loginHint The account identifier in the format `ID:{msisdn}/MSISDN` (e.g. `ID:563667/MSISDN`).
     */
    fun saveLoginHint(loginHint: String) {
        prefs.edit { putString(KEY_LOGIN_HINT, loginHint) }
    }

    /**
     * Returns the stored login hint, or an empty string if it was never set.
     */
    fun getLoginHint(): String = prefs.getString(KEY_LOGIN_HINT, "").orEmpty()

    /**
     * Removes every persisted credential (API key, tokens, auth-request ID, and login hint).
     *
     * The next request will trigger a full re-bootstrap, re-provisioning all credentials from
     * scratch. Intended for the sample app's "clear stored credentials" settings action.
     */
    fun clearAll() {
        prefs.edit { clear() }
    }

    /**
     * Returns the absolute epoch-millisecond timestamp that is [seconds] from now.
     *
     * @param seconds Number of seconds from the current time (standard OAuth2 `expires_in` unit).
     * @return The corresponding epoch millisecond value.
     */
    private fun expiryInSeconds(seconds: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.SECOND, seconds)
        return cal.timeInMillis
    }

    /**
     * Returns `true` if the given epoch-millisecond expiry timestamp is in the past.
     *
     * A value of `0L` (the default when no expiry has been stored) is treated as already
     * expired so that un-initialised tokens are never returned as valid.
     *
     * @param expiryMillis Absolute epoch-millisecond expiry timestamp.
     */
    private fun isExpired(expiryMillis: Long): Boolean = Calendar.getInstance().timeInMillis > expiryMillis
}
