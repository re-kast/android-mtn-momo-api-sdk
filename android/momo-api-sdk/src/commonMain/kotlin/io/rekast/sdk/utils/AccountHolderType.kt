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
 * Enum class representing the different types of account holders in the MTN MOMO system.
 *
 * @property accountHolderType The string representation of the account holder type.
 */
enum class AccountHolderType(val accountHolderType: String) {
    /**
     * Represents a mobile number as the account holder type.
     */
    MSISDN("msisdn"),

    /**
     * Represents an email address as the account holder type.
     */
    EMAIL("email"),

    /**
     * Represents a party code as the account holder type.
     */
    PARTY_CODE("party_code")
}
