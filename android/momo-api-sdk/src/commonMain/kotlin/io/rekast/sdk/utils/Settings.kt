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
package io.rekast.sdk.utils

import io.rekast.sdk.model.MomoTransaction
import java.util.UUID
import javax.inject.Inject
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Contains general settings used in the library.
 *
 * This class provides methods for generating UUIDs, formatting phone numbers,
 * retrieving product subscription keys, and checking notification message lengths.
 */
class Settings @Inject constructor() {

    /**
     * Generates a new UUID as a String.
     *
     * @return A randomly generated UUID.
     */
    fun generateUUID(): String = UUID.randomUUID().toString()

    /**
     * Connection timeout duration in milliseconds.
     */
    val connectTimeout: Long = 60 * 1000

    /**
     * Connection read timeout duration in milliseconds.
     */
    val readTimeout: Long = 60 * 1000

    /**
     * Connection write timeout duration in milliseconds.
     */
    val writeTimeout: Long = 60 * 1000

    /**
     * Formats the phone number based on the provided country code.
     *
     * @param phoneNumber The phone number to format.
     * @param countryCode The country code to prepend if the phone number starts with "0".
     * @return A formatted phone number as a String, or null if the input is blank.
     */
    fun formatPhoneNumber(phoneNumber: String, countryCode: String): String? {
        if (phoneNumber.isBlank()) return null
        if (phoneNumber.length < 11 && phoneNumber.startsWith("0")) {
            return phoneNumber.replaceFirst("^0".toRegex(), countryCode)
        }
        return if (phoneNumber.length == 13 && phoneNumber.startsWith("+")) {
            phoneNumber.replaceFirst("^\\+".toRegex(), "")
        } else {
            phoneNumber
        }
    }

    /**
     * Generates a MomoTransaction object from the given response.
     *
     * @param response The response containing the transaction data.
     * @return A MomoTransaction object, or null if the response body is empty.
     */
    fun generateTransactionFromResponse(response: Response<ResponseBody?>): MomoTransaction? {
        val responseData: String = response.body()!!.source().readUtf8()
        return Json.decodeFromString(responseData)
    }

    /**
     * Checks if the length of the notification message is within the allowed limit.
     *
     * @param notificationMessage The notification message to check.
     * @param notificationMessageMaxLength The maximum allowed length for the notification message.
     * @return True if the message length is within the limit, false otherwise.
     */
    fun checkNotificationMessageLength(notificationMessage: String?, notificationMessageMaxLength: Long = Constants.NOTIFICATION_MESSAGE_LENGTH): Boolean {
        if (!notificationMessage.isNullOrBlank()) {
            return notificationMessage.length <= notificationMessageMaxLength
        }
        return false
    }
}
