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

/**
 * Enum class representing the various error responses from the MTN MOMO API.
 *
 * Each constant corresponds to a specific error that can occur during API interactions.
 */
enum class MomoAPIErrorResponses {
    /**
     * Indicates that the payer was not found.
     */
    PAYER_NOT_FOUND,

    /**
     * Indicates that the payer has reached their limit for transactions.
     */
    PAYER_LIMIT_REACHED,

    /**
     * Indicates that there are not enough funds for the transaction.
     */
    NOT_ENOUGH_FUNDS,

    /**
     * Indicates that the action is not allowed.
     */
    NOT_ALLOWED,

    /**
     * Indicates that the target environment is not allowed.
     */
    NOT_ALLOWED_TARGET_ENVIRONMENT,

    /**
     * Indicates that the callback URL host is invalid.
     */
    INVALID_CALLBACK_URL_HOST,

    /**
     * Indicates that the currency provided is invalid.
     */
    INVALID_CURRENCY,

    /**
     * Indicates that the service is currently unavailable.
     */
    SERVICE_UNAVAILABLE,

    /**
     * Indicates that the payee is not allowed to receive the payment.
     */
    PAYEE_NOT_ALLOWED_TO_RECEIVE,

    /**
     * Indicates that the payment has not been approved.
     */
    PAYMENT_NOT_APPROVED,

    /**
     * Indicates that the requested resource was not found.
     */
    RESOURCE_NOT_FOUND,

    /**
     * Indicates that the approval for the transaction was rejected.
     */
    APPROVAL_REJECTED,

    /**
     * Indicates that the transaction has expired.
     */
    EXPIRED,

    /**
     * Indicates that the transaction has been canceled.
     */
    TRANSACTION_CANCELED,

    /**
     * Indicates that the resource already exists.
     */
    RESOURCE_ALREADY_EXIST,

    /**
     * Indicates that the transaction has not been completed.
     */
    TRANSACTION_NOT_COMPLETED,

    /**
     * Indicates that the transaction was not found.
     */
    TRANSACTION_NOT_FOUND,

    /**
     * Indicates that there is an informational scope instruction.
     */
    INFORMATIONAL_SCOPE_INSTRUCTION,

    /**
     * Indicates that a required scope instruction is missing.
     */
    MISSING_SCOPE_INSTRUCTION,

    /**
     * Indicates that more than one financial scope is not supported.
     */
    MORE_THAN_ONE_FINANCIAL_SCOPE_NOT_SUPPORTED,

    /**
     * Indicates that the combination of scopes is unsupported.
     */
    UNSUPPORTED_SCOPE_COMBINATION,

    /**
     * Indicates that there is a consent mismatch.
     */
    CONSENT_MISMATCH,

    /**
     * Indicates that the requested scope is unsupported.
     */
    UNSUPPORTED_SCOPE,

    /**
     * Indicates that a general not found error occurred.
     */
    NOT_FOUND,

    /**
     * Indicates that the payee was not found.
     */
    PAYEE_NOT_FOUND,

    /**
     * Indicates that an internal processing error occurred.
     */
    INTERNAL_PROCESSING_ERROR,

    /**
     * Indicates that the transaction could not be performed.
     */
    COULD_NOT_PERFORM_TRANSACTION
}
