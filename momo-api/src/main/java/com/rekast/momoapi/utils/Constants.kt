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
package com.rekast.momoapi.utils

object Constants {
    const val TIMESTAMP_FORMAT = "yyyyMMddHHmmss"

    object Url {
        const val REMITTANCE = "remittance"
    }

    object Headers {
        const val OCP_APIM_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key"
        const val X_CALLBACK_URL = "X-Callback-Url"
        const val X_REFERENCE_ID = "X-Reference-Id"
        const val X_TARGET_ENVIRONMENT = "X-Target-Environment"
        const val  LANGUAGE = "Language"
        const val  NOTIFICATION_MESSAGE = "notificationMessage"
        const val  CONTENT_TYPE = "Content-Type"
        const val  AUTHORIZATION = "Authorization"
    }

    object TokenTypes {
        const val BASIC = "Basic"
        const val BEARER = "Bearer"
    }
}
