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
 * Enum of the identification-document types accepted for the payer of a Remittance cash transfer.
 *
 * Used as the typed `payerIdentificationType` on [io.rekast.sdk.model.CashTransfer] and serialized by
 * constant name (e.g. `PASS`) to match the API body format.
 */
@Serializable
enum class PayerIdentificationType {
    /** Passport. */
    PASS,

    /** Corporate identification. */
    CPFA,

    /** Social security number issued in the SSA region. */
    SRSSA,

    /** National registration identification number. */
    NRIN,

    /** Other identification not covered by the remaining types. */
    OTHR,

    /** Driver's licence. */
    DRLC,

    /** Social security number. */
    SOCS,

    /** Alien registration number. */
    AREG,

    /** Identity card. */
    IDCD,

    /** Employer-issued identification. */
    EMID
}
