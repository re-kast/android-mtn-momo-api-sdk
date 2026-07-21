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

import kotlinx.serialization.Serializable

/**
 * Enum class representing the possible statuses in the MTN MOMO system.
 *
 * Used as the typed `status` field for transaction-style responses such as
 * [io.rekast.sdk.model.PaymentStatus] and the operation status models, and for
 * pre-approval responses ([io.rekast.sdk.model.PreApprovalDetails],
 * [io.rekast.sdk.model.PreApprovalStatus]). Each constant corresponds to a specific state.
 */
@Serializable
enum class StatusTypes {
    /**
     * Indicates that the transaction was successful.
     */
    SUCCESSFUL,

    /**
     * Indicates that the transaction is currently pending.
     */
    PENDING,

    /**
     * Indicates that the transaction has failed.
     */
    FAILED,

    /**
     * Indicates that the transaction has been created but not yet processed.
     */
    CREATED,

    /**
     * Indicates that a pre-approval has been approved.
     */
    APPROVED,

    /**
     * Indicates that a pre-approval has been cancelled.
     */
    CANCELLED,

    /**
     * Indicates that a pre-approval has expired.
     */
    EXPIRED,

    /**
     * Indicates that a pre-approval has been rejected.
     */
    REJECTED
}
