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
package io.io.rekast.momoapi.utils

import com.google.gson.Gson
import io.rekast.sdk.BuildConfig
import io.rekast.sdk.model.api.MomoTransaction
import io.rekast.sdk.utils.MomoConstants
import io.rekast.sdk.utils.ProductType
import okhttp3.ResponseBody
import org.apache.commons.lang3.StringUtils
import retrofit2.Response
import java.util.*

/**
 * Contains General Settings used in the library.
 */
object Settings {

    fun generateUUID(): String = UUID.randomUUID().toString()

    /**
     * Connection timeout duration
     */
    const val CONNECT_TIMEOUT: Long = 60 * 1000

    /**
     * Connection Read timeout duration
     */
    const val READ_TIMEOUT: Long = 60 * 1000

    /**
     * Connection write timeout duration
     */
    const val WRITE_TIMEOUT: Long = 60 * 1000

    /**
     * The MSISDN sending the funds
     *
     * @param [phoneNumber] The Phonenumber the money is sent to.
     * @param [countryCode] The country code the Money is to be sent from.
     * @return a formatted String
     */
    fun formatPhoneNumber(phoneNumber: String, countryCode: String): String? {
        if (phoneNumber.isBlank()) return null
        if (phoneNumber.length < 11 && phoneNumber.startsWith("0")) {
            // here we can just remove the inline variable instead of the p. Like you did with the rest
            // String p = phoneNumber.replaceFirst("^0", "254");
            // return p
            return phoneNumber.replaceFirst("^0".toRegex(), countryCode)
        }
        return if (phoneNumber.length == 13 && phoneNumber.startsWith("+")) {
            phoneNumber.replaceFirst("^+".toRegex(), "")
        } else {
            phoneNumber
        }
    }

    /**
     * @param [productType] this is the MTN MOMO API product of choice
     * Return the [productKey] based on the [productType]. It gets the product keys defined on the `local.properties` file.
     * It gets them through the [BuildConfig] generated file.
     */
    fun getProductSubscriptionKeys(productType: ProductType): String {
        val productKey: String = when (productType) {
            ProductType.COLLECTION -> {
                if ((StringUtils.isNotBlank(BuildConfig.MOMO_COLLECTION_PRIMARY_KEY))) {
                    BuildConfig.MOMO_COLLECTION_PRIMARY_KEY
                } else {
                    BuildConfig.MOMO_COLLECTION_SECONDARY_KEY
                }
            }
            ProductType.REMITTANCE -> {
                if (StringUtils.isNotBlank(BuildConfig.MOMO_REMITTANCE_PRIMARY_KEY)) {
                    BuildConfig.MOMO_REMITTANCE_PRIMARY_KEY
                } else {
                    BuildConfig.MOMO_REMITTANCE_SECONDARY_KEY
                }
            }
            ProductType.DISBURSEMENTS -> {
                if (StringUtils.isNotBlank(BuildConfig.MOMO_DISBURSEMENTS_PRIMARY_KEY)) {
                    BuildConfig.MOMO_DISBURSEMENTS_PRIMARY_KEY
                } else {
                    BuildConfig.MOMO_DISBURSEMENTS_SECONDARY_KEY
                }
            }
        }
        return productKey
    }

    fun generateTransactionFromResponse(response: Response<ResponseBody?>): MomoTransaction? {
        val data: String = response.body()!!.source().readUtf8()
        return Gson().fromJson(data, MomoTransaction::class.java)
    }

    fun checkNotificationMessageLength(
        notificationMessage: String?,
        notificationMessageMaxLength: Long = MomoConstants.NOTIFICATION_MESSAGE_LENGTH
    ): Boolean {
        if (StringUtils.isNotBlank(notificationMessage)) {
            return notificationMessage!!.length <= notificationMessageMaxLength
        }
        return false
    }
}
