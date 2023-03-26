/*
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package io.rekast.momoapi.sample.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import io.rekast.momoapi.model.authentication.AccessToken
import io.rekast.momoapi.sample.BuildConfig
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.Calendar

object Utils {
    fun saveApiKey(context: Context, apiKey: String) {
        val mSettings = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        val editor = mSettings.edit()

        editor.putString("apiKey", apiKey)
        editor.apply()
    }

    fun getApiKey(context: Context): String {
        val mSettings = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        return mSettings.getString("apiKey", "").toString()
    }

    fun saveAccessToken(context: Context, accessToken: AccessToken) {
        val tokenExpiry = if (StringUtils.isNotBlank(accessToken.expiresIn)) {
            accessToken.expiresIn.toIntOrNull()
        } else { 1 }
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, tokenExpiry!!)
        val oneHourAfter = cal.timeInMillis

        val mSettings = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        val editor = mSettings.edit()

        editor.putString("accessToken", accessToken.accessToken)
        editor.putLong("expiryDate", oneHourAfter)
        editor.putString("tokenType", accessToken.tokenType)
        editor.apply()
    }

    fun getAccessToken(context: Context): String {
        return if (expired(context)) {
            ""
        } else {
            val mSettings = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
            mSettings.getString("accessToken", "").toString()
        }
    }

    private fun expired(context: Context): Boolean {
        val mSettings = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        val expiryTime = mSettings.getLong("expiryDate", 0)
        val currentTime = Calendar.getInstance().timeInMillis
        return currentTime > expiryTime
    }

    fun convertToDate(milliseconds: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return simpleDateFormat.format(milliseconds)
    }
}
