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
package io.rekast.sdk.utils

/**
 * Central repository of constants used throughout the MTN MOMO SDK.
 *
 * Contains timestamp formats, message length limits, and nested objects grouping
 * HTTP header names, token type prefixes, URL path parameters, and API endpoint templates.
 */
object Constants {
    /**
     * The format used for timestamps in the SDK.
     */
    const val TIMESTAMP_FORMAT = "yyyyMMddHHmmss"

    /**
     * The maximum length of notification messages.
     */
    const val NOTIFICATION_MESSAGE_LENGTH = 160L

    /**
     * Contains HTTP header constants used in API requests.
     */
    object Headers {
        /**
         * The header key for the OCP APIM subscription key.
         */
        const val OCP_APIM_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key"

        /**
         * The header key for the optional callback URL to notify once the operation completes.
         */
        const val X_CALLBACK_URL = "X-Callback-Url"

        /**
         * The header key for the reference ID.
         */
        const val X_REFERENCE_ID = "X-Reference-Id"

        /**
         * The header key for the target environment.
         */
        const val X_TARGET_ENVIRONMENT = "X-Target-Environment"

        /**
         * The header key for the language.
         */
        const val LANGUAGE = "Language"

        /**
         * The header key for the notification message.
         */
        const val NOTIFICATION_MESSAGE = "notificationMessage"

        /**
         * The header key for the content type.
         */
        const val CONTENT_TYPE = "Content-Type"

        /**
         * The header key for authorization.
         */
        const val AUTHORIZATION = "Authorization"
    }

    /**
     * Contains token type constants used in API requests.
     */
    object TokenTypes {
        /**
         * The token type for basic authentication.
         */
        const val BASIC = "Basic"

        /**
         * The token type for bearer authentication.
         */
        const val BEARER = "Bearer"
    }

    /**
     * Contains endpoint path constants used in API requests.
     */
    object EndpointPaths {
        /**
         * The path parameter for the API version.
         */
        const val API_VERSION = "apiVersion"

        /**
         * The path parameter for the API user ID (the `{apiUser}` path segment).
         */
        const val X_REFERENCE_ID = "apiUser"

        /**
         * The path parameter for the product type.
         */
        const val PRODUCT_TYPE = "productType"

        /**
         * The path parameter for the reference ID.
         */
        const val REFERENCE_ID = "referenceId"

        /**
         * The path parameter for the account holder ID.
         */
        const val ACCOUNT_HOLDER_ID = "accountHolderId"

        /**
         * The path parameter for the account holder type.
         */
        const val ACCOUNT_HOLDER_TYPE = "accountHolderIdType"

        /**
         * The path parameter for the currency.
         */
        const val CURRENCY = "currency"

        /**
         * The literal path segment that identifies OAuth2 (consent) endpoints, e.g.
         * `/{productType}/oauth2/{apiVersion}/userinfo`. OAuth2 *resource* endpoints (those that
         * contain [OAUTH2] but not [TOKEN]) must be authenticated with the OAuth2 consent access
         * token rather than the regular API-user Bearer token.
         */
        const val OAUTH2 = "oauth2"

        /**
         * The literal path segment that identifies token-issuance endpoints, e.g.
         * `/{productType}/oauth2/token/` and `/{productType}/token/`. The OAuth2 **token** endpoint
         * mints the consent token and must therefore be authenticated with the regular API-user
         * Bearer token, *not* the (not-yet-issued) consent token — so it is excluded from OAuth2
         * consent-token routing even though its path contains [OAUTH2].
         */
        const val TOKEN = "token"
    }

    /**
     * Contains endpoint constants for various API calls.
     */
    object EndPoints {
        /**
         * Endpoint for creating an API user.
         */
        const val CREATE_API_USER = "/{apiVersion}/apiuser"

        /**
         * Endpoint for retrieving an API user.
         */
        const val GET_API_USER = "/{apiVersion}/apiuser/{apiUser}"

        /**
         * Endpoint for creating an API key.
         */
        const val CREATE_API_KEY = "/{apiVersion}/apiuser/{apiUser}/apikey"

        /**
         * Endpoint for retrieving an access token.
         */
        const val GET_ACCESS_TOKEN = "/{productType}/token/"

        /**
         * Endpoint for retrieving an OAuth2 access token (CIBA consent flow).
         */
        const val GET_OAUTH2_ACCESS_TOKEN = "/{productType}/oauth2/token/"

        /**
         * Endpoint for retrieving basic user information.
         */
        const val GET_BASIC_USER_INFO = "/{productType}/{apiVersion}/accountholder/msisdn/{accountHolderId}/basicuserinfo"

        /**
         * Endpoint for retrieving user information with consent.
         */
        const val GET_USER_INFO_WITH_CONSENT = "/{productType}/oauth2/{apiVersion}/userinfo"

        /**
         * Endpoint for retrieving account balance.
         */
        const val GET_ACCOUNT_BALANCE = "/{productType}/{apiVersion}/account/balance"

        /**
         * Endpoint for retrieving account balance in a specific currency.
         */
        const val GET_ACCOUNT_BALANCE_IN_SPECIFIC_CURRENCY = "/{productType}/{apiVersion}/account/balance/{currency}"

        /**
         * Endpoint for transferring funds.
         */
        const val TRANSFER = "/{productType}/{apiVersion}/transfer"

        /**
         * Endpoint for retrieving transfer status.
         */
        const val GET_TRANSFER_STATUS = "/{productType}/{apiVersion}/transfer/{referenceId}"

        /**
         * Endpoint for requesting a payment delivery notification.
         */
        const val REQUEST_TO_PAY_DELIVERY_NOTIFICATION =
            "/{productType}/{apiVersion}/requesttopay/{referenceId}/deliverynotification"

        /**
         * Endpoint for validating account holder status.
         */
        const val VALIDATE_ACCOUNT_HOLDER_STATUS =
            "/{productType}/{apiVersion}/accountholder/{accountHolderIdType}/{accountHolderId}/active"

        /**
         * Endpoint for requesting to pay.
         */
        const val REQUEST_TO_PAY = "/collection/{apiVersion}/requesttopay"

        /**
         * Endpoint for checking the status of a payment request.
         */
        const val REQUEST_TO_PAY_STATUS = "/collection/{apiVersion}/requesttopay/{referenceId}"

        /**
         * Endpoint for requesting to withdraw.
         */
        const val REQUEST_TO_WITHDRAW = "/collection/{apiVersion}/requesttowithdraw"

        /**
         * Endpoint for checking the status of a withdrawal request.
         */
        const val REQUEST_TO_WITHDRAW_STATUS = "/collection/{apiVersion}/requesttowithdraw/{referenceId}"

        /**
         * Endpoint for making a deposit.
         */
        const val DEPOSIT = "/disbursement/{apiVersion}/deposit"

        /**
         * Endpoint for checking the status of a deposit.
         */
        const val DEPOSIT_STATUS = "/disbursement/{apiVersion}/deposit/{referenceId}"

        /**
         * Endpoint for making a refund.
         */
        const val REFUND = "/disbursement/{apiVersion}/refund"

        /**
         * Endpoint for checking the status of a refund.
         */
        const val REFUND_STATUS = "/disbursement/{apiVersion}/refund/{referenceId}"

        /**
         * Endpoint for initiating a backchannel authorization request.
         */
        const val BC_AUTHORIZE = "/{productType}/{apiVersion}/bc-authorize"

        /**
         * Endpoint for initiating a Remittance cash transfer (V2) with extended KYC fields.
         */
        const val CASH_TRANSFER = "/remittance/{apiVersion}/cashtransfer"

        /**
         * Endpoint for retrieving the status of a Remittance cash transfer by reference ID.
         */
        const val CASH_TRANSFER_STATUS = "/remittance/{apiVersion}/cashtransfer/{referenceId}"

        /**
         * Endpoint for creating a Collection invoice.
         */
        const val INVOICE = "/collection/{apiVersion}/invoice"

        /**
         * Endpoint for retrieving or cancelling a Collection invoice by reference ID.
         * Used with GET to retrieve status and DELETE to cancel.
         */
        const val INVOICE_STATUS = "/collection/{apiVersion}/invoice/{referenceId}"

        /**
         * Endpoint for creating a Collection pre-approval authorisation.
         */
        const val PRE_APPROVAL = "/collection/{apiVersion}/preapproval"

        /**
         * Endpoint for retrieving the status of a Collection pre-approval by reference ID.
         * Used with GET to retrieve status and DELETE to cancel.
         */
        const val PRE_APPROVAL_STATUS = "/collection/{apiVersion}/preapproval/{referenceId}"

        /**
         * Endpoint for retrieving the list of approved pre-approvals for a given account holder.
         */
        const val GET_APPROVED_PRE_APPROVALS =
            "/collection/{apiVersion}/preapprovals/{accountHolderIdType}/{accountHolderId}"

        /**
         * Endpoint for creating a Collection payment (V2).
         */
        const val CREATE_PAYMENT = "/collection/{apiVersion}/payment"

        /**
         * Endpoint for retrieving the status of a Collection payment by reference ID.
         */
        const val PAYMENT_STATUS = "/collection/{apiVersion}/payment/{referenceId}"

        /**
         * Endpoint for sending a delivery notification for a request-to-withdraw transaction.
         */
        const val REQUEST_TO_WITHDRAW_DELIVERY_NOTIFICATION = "/collection/{apiVersion}/requesttowithdraw/{referenceId}/deliverynotification"
    }

    /**
     * Contains form field constants used in form-encoded API requests.
     */
    object FormFields {
        /** Form field for the login hint (`ID:{msisdn}/MSISDN`) sent to the bc-authorize endpoint. */
        const val LOGIN_HINT = "login_hint"

        /** Form field for the requested OAuth2 scope. */
        const val SCOPE = "scope"

        /** Form field for the access type (`online` or `offline`). */
        const val ACCESS_TYPE = "access_type"

        /** Form field for the OAuth2 grant type. */
        const val GRANT_TYPE = "grant_type"

        /** The CIBA grant type value used when exchanging an `auth_req_id` for an OAuth2 token. */
        const val CIBA_GRANT_TYPE = "urn:openid:params:grant-type:ciba"

        /** Form field for the backchannel authorization request ID (`auth_req_id`). */
        const val BACK_CHANNEL_AUTHORIZATION_REQUEST_ID = "auth_req_id"

        /** Default OAuth2 scope requested for the CIBA consent flow. */
        const val CIBA_SCOPE = "profile openid"

        /** Default access type requested for the CIBA consent flow. */
        const val CIBA_ACCESS_TYPE = "online"
    }
}
