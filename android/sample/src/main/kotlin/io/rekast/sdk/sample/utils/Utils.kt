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
package io.rekast.sdk.sample.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import io.rekast.sdk.model.authentication.AccessToken
import io.rekast.sdk.model.authentication.Oauth2AccessToken
import io.rekast.sdk.utils.ProductType
import java.text.SimpleDateFormat
import java.util.Calendar

private const val API_KEY = "apiKey"

/** SharedPreferences keys used for storing and retrieving the basic [AccessToken]. */
object AccessTokenConstants {
    const val ACCESS_TOKEN = "accessToken"
    const val EXPIRY_DATE = "expiryDate"
    const val TOKEN_TYPE = "tokenType"
}

/** SharedPreferences keys used for storing and retrieving the OAuth 2.0 [Oauth2AccessToken]. */
object Oauth2AccessTokenConstants {
    const val ACCESS_TOKEN = "oauthAccessToken"
    const val EXPIRY_DATE = "oauthExpiryDate"
    const val TOKEN_TYPE = "oauthTokenType"
    const val SCOPE = "scope"
    const val REFRESH_TOKEN = "refreshToken"
    const val REFRESH_TOKEN_EXPIRED_IN = "refreshTokenExpiredIn"
}

/**
 * Utility object providing various helper functions for the MTN MOMO SDK sample application.
 *
 * This object contains methods for saving and retrieving API keys and access tokens,
 * as well as checking token expiration and formatting dates.
 */
object Utils {
    /**
     * Saves the provided API key in the shared preferences.
     *
     * @param context The context used to access shared preferences.
     * @param apiKey The API key to be saved.
     */
    fun saveApiKey(context: Context, apiKey: String) {
        val mSettings = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
        mSettings.edit {
            putString(API_KEY, apiKey)
        }
    }

    /**
     * Retrieves the saved API key from shared preferences.
     *
     * @param context The context used to access shared preferences.
     * @return The saved API key as a String, or an empty string if not found.
     */
    fun getApiKey(context: Context): String {
        val mSettings = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
        return mSettings.getString(API_KEY, "").toString()
    }

    /**
     * Saves the provided access token in the shared preferences along with its expiry date.
     *
     * @param context The context used to access shared preferences.
     * @param accessToken The access token to be saved.
     */
    fun saveAccessToken(context: Context, accessToken: AccessToken?) {
        val tokenExpiry = if (!accessToken!!.expiresIn.isNullOrBlank()) {
            accessToken.expiresIn.toIntOrNull()
        } else {
            1
        }

        val mSettings = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
        mSettings.edit {
            putString(AccessTokenConstants.ACCESS_TOKEN, accessToken.accessToken)
            putLong(AccessTokenConstants.EXPIRY_DATE, returnExpiryINMilliseconds(tokenExpiry))
            putString(AccessTokenConstants.TOKEN_TYPE, accessToken.tokenType)
        }
    }

    /**
     * Saves the provided OAuth 2.0 access token in shared preferences along with its expiry dates.
     *
     * @param context The context used to access shared preferences.
     * @param oauth2AccessToken The OAuth 2.0 access token to be saved.
     */
    fun saveOauth2AccessToken(context: Context, oauth2AccessToken: Oauth2AccessToken?) {
        val accessTokenExpiry = if (!oauth2AccessToken!!.expiresIn.isNullOrBlank()) {
            oauth2AccessToken.expiresIn.toIntOrNull()
        } else {
            1
        }
        val refreshTokenExpiry = if (!oauth2AccessToken.refreshTokenExpiredIn.isNullOrBlank()) {
            oauth2AccessToken.refreshTokenExpiredIn.toIntOrNull()
        } else {
            1
        }

        val mSettings = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
        mSettings.edit {
            putString(Oauth2AccessTokenConstants.ACCESS_TOKEN, oauth2AccessToken.accessToken)
            putLong(Oauth2AccessTokenConstants.EXPIRY_DATE, returnExpiryINMilliseconds(accessTokenExpiry))
            putString(Oauth2AccessTokenConstants.TOKEN_TYPE, oauth2AccessToken.tokenType)
            putString(Oauth2AccessTokenConstants.SCOPE, oauth2AccessToken.scope)
            putString(Oauth2AccessTokenConstants.REFRESH_TOKEN, oauth2AccessToken.refreshToken)
            putLong(Oauth2AccessTokenConstants.REFRESH_TOKEN_EXPIRED_IN, returnExpiryINMilliseconds(refreshTokenExpiry))
        }
    }

    /**
     * Retrieves the saved access token from shared preferences.
     *
     * @param context The context used to access shared preferences.
     * @return The saved access token as a String, or an empty string if expired or not found.
     */
    fun getAccessToken(context: Context): String {
        val mSettings = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
        val expiryTime = mSettings.getLong(AccessTokenConstants.EXPIRY_DATE, 0)

        return if (expired(expiryTime)) {
            ""
        } else {
            mSettings.getString(AccessTokenConstants.ACCESS_TOKEN, "").toString()
        }
    }

    /**
     * Retrieves the saved OAuth 2.0 access token from shared preferences.
     *
     * @param context The context used to access shared preferences.
     * @return The saved OAuth 2.0 access token as a String, or an empty string if expired or not found.
     */
    fun getOauthAccessToken(context: Context): String {
        val mSettings = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
        val expiryTime = mSettings.getLong(Oauth2AccessTokenConstants.EXPIRY_DATE, 0)

        return if (expired(expiryTime)) {
            ""
        } else {
            mSettings.getString(Oauth2AccessTokenConstants.ACCESS_TOKEN, "").toString()
        }
    }

    private fun returnExpiryINMilliseconds(date: Int?): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, date!!)
        return calendar.timeInMillis
    }

    /**
     * Checks if the saved access token has expired.
     *
     * @param context The context used to access shared preferences.
     * @return True if the token is expired, false otherwise.
     */
    private fun expired(expiryTime: Long): Boolean {
        val currentTime = Calendar.getInstance().timeInMillis
        return currentTime > expiryTime
    }

    /**
     * Converts the given milliseconds to a formatted date string.
     *
     * @param milliseconds The time in milliseconds to convert.
     * @return A formatted date string in "yyyy-MM-dd" format.
     */
    fun convertToDate(milliseconds: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return simpleDateFormat.format(milliseconds)
    }

    /**
     * Retrieves the product subscription keys based on the specified product
     *
     * @param productType The MTN MOMO API product type.
     * @return The corresponding product key as a String.
     */
    fun getProductSubscriptionKeys(productType: ProductType, config: SampleConfig): String {
        val productKey: String = when (productType) {
            ProductType.COLLECTION -> {
                config.collectionPrimaryKey.ifBlank {
                    config.collectionSecondaryKey
                }
            }

            ProductType.REMITTANCE -> {
                if (config.remittancePrimaryKey.isNotBlank()) {
                    config.remittancePrimaryKey
                } else {
                    config.remittanceSecondaryKey
                }
            }

            ProductType.DISBURSEMENTS -> {
                if (config.disbursementsPrimaryKey.isNotBlank()) {
                    config.disbursementsPrimaryKey
                } else {
                    config.disbursementsSecondaryKey
                }
            }
        }
        return productKey
    }
}
