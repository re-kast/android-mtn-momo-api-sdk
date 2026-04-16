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
 * Enum class representing the possible statuses of a transaction in the MTN MOMO system.
 *
 * Each constant corresponds to a specific state of a transaction.
 */
enum class TransactionStatus {
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
    FAILED
}
