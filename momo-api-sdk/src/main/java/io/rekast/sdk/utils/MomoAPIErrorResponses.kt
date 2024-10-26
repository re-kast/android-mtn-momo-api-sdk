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
package io.rekast.sdk.utils

enum class MomoAPIErrorResponses {
    PAYER_NOT_FOUND,
    PAYER_LIMIT_REACHED,
    NOT_ENOUGH_FUNDS,
    NOT_ALLOWED,
    NOT_ALLOWED_TARGET_ENVIRONMENT,
    INVALID_CALLBACK_URL_HOST,
    INVALID_CURRENCY,
    SERVICE_UNAVAILABLE,
    PAYEE_NOT_ALLOWED_TO_RECEIVE,
    PAYMENT_NOT_APPROVED,
    RESOURCE_NOT_FOUND,
    APPROVAL_REJECTED,
    EXPIRED,
    TRANSACTION_CANCELED,
    RESOURCE_ALREADY_EXIST,
    TRANSACTION_NOT_COMPLETED,
    TRANSACTION_NOT_FOUND,
    INFORMATIONAL_SCOPE_INSTRUCTION,
    MISSING_SCOPE_INSTRUCTION,
    MORE_THAN_ONE_FINANCIAL_SCOPE_NOT_SUPPORTED,
    UNSUPPORTED_SCOPE_COMBINATION,
    CONSENT_MISMATCH,
    UNSUPPORTED_SCOPE,
    NOT_FOUND,
    PAYEE_NOT_FOUND,
    INTERNAL_PROCESSING_ERROR,
    COULD_NOT_PERFORM_TRANSACTION
}
