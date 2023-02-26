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
    const val NOTIFICATION_MESSAGE_LENGTH = 160L
    object Headers {
        const val OCP_APIM_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key"
        const val X_CALLBACK_URL = "X-Callback-Url"
        const val X_REFERENCE_ID = "X-Reference-Id"
        const val X_TARGET_ENVIRONMENT = "X-Target-Environment"
        const val LANGUAGE = "Language"
        const val NOTIFICATION_MESSAGE = "notificationMessage"
        const val CONTENT_TYPE = "Content-Type"
        const val AUTHORIZATION = "Authorization"
    }

    object TokenTypes {
        const val BASIC = "Basic"
        const val BEARER = "Bearer"
    }

    object ProductTypes {
        const val COLLECTION = "collection"
        const val DISBURSEMENTS = "disbursements"
        const val REMITTANCE = "remittance"
    }

    object EndpointPaths {
        const val API_VERSION = "apiVersion"
        const val API_USER = "apiUser"
        const val PRODUCT_TYPE = "productType"
        const val REFERENCE_ID = "referenceId"
        const val ACCOUNT_HOLDER_ID = "accountHolderId"
        const val ACCOUNT_HOLDER_TYPE = "accountHolderIdType"
        const val CURRENCY = "currency"
        object AccountHolderTypes {
            const val MSISDN = "msisdn"
            const val EMAIL = "email"
            const val PARTY_CODE = "party_code"
        }
    }

    object EndPoints {
        const val GET_API_USER = "/{apiVersion}/apiuser/{apiUser}"
        const val GET_API_USER_KEY = "/{apiVersion}/apiuser/{apiUser}/apikey"
        const val GET_ACCESS_TOKEN = "/{productType}/token/"
        const val TRANSFER = "/remittance/{apiVersion}/transfer"
        const val GET_ACCOUNT_BALANCE = "/{productType}/{apiVersion}/account/balance"
        const val GET_BASIC_USER_INFO = "/{productType}/{apiVersion}/accountholder/msisdn/{accountHolderId}/basicuserinfo"
        const val GET_USER_INFO_WITH_CONSENT = "/{productType}/oauth2/{apiVersion}/userinfo"
        const val GET_TRANSFER_STATUS = "/{productType}/{apiVersion}/transfer/{referenceId}"
        const val REQUEST_TO_PAY_DELIVERY_NOTIFICATION =
            "/{productType}/{apiVersion}/requesttopay/{referenceId}/deliverynotification"
        const val VALIDATE_ACCOUNT_HOLDER_STATUS =
            "/{productType}/{apiVersion}/accountholder/{accountHolderIdType}/{accountHolderId}/active"
        const val GET_ACCOUNT_BALANCE_IN_SPECIFIC_CURRENCY = "/{productType}/{apiVersion}/account/balance/{currency}"
    }
}
