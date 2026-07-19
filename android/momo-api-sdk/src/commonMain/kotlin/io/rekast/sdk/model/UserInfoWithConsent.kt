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
package io.rekast.sdk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents extended user information returned by the MTN MOMO `GET /oauth2/{version}/userinfo`
 * endpoint after the user grants consent.
 *
 * The API response is a **flat** JSON object — all fields are at the top level.
 * Fields not guaranteed by the API are nullable with a `null` default so that a missing field
 * does not cause a deserialization failure.
 *
 * @property sub The subject identifier for the user.
 * @property name The full name of the user.
 * @property givenName The user's given (first) name.
 * @property familyName The user's family (last) name.
 * @property birthDate The user's date of birth.
 * @property locale The user's locale string.
 * @property gender The user's gender.
 * @property updatedAt The last profile update as a Unix epoch integer (seconds).
 * @property status The current status of the user account.
 * @property middleName The user's middle name.
 * @property email The user's email address.
 * @property emailVerified Whether the user's email address has been verified.
 * @property phonenumber The user's phone number.
 * @property phoneNumberVerified Whether the user's phone number has been verified.
 * @property address The user's physical address as a nested [Address] object.
 * @property creditScore The user's credit score as reported by the MTN MOMO system.
 * @property active Indicates whether the user's account is active.
 * @property countryOfBirth The country where the user was born.
 * @property regionOfBirth The region where the user was born.
 * @property cityOfBirth The city where the user was born.
 * @property occupation The user's occupation.
 * @property employerName The name of the user's employer.
 * @property identificationType The type of identification document provided.
 * @property identificationValue The value/number of the identification document.
 */
@Serializable
data class UserInfoWithConsent(
    @SerialName("sub") var sub: String,
    @SerialName("name") var name: String,
    @SerialName("given_name") var givenName: String? = null,
    @SerialName("family_name") var familyName: String? = null,
    @SerialName("birthdate") var birthDate: String? = null,
    @SerialName("locale") var locale: String? = null,
    @SerialName("gender") var gender: String? = null,
    @SerialName("updated_at") var updatedAt: Int? = null,
    @SerialName("status") var status: String? = null,
    @SerialName("middle_name") var middleName: String? = null,
    @SerialName("email") var email: String? = null,
    @SerialName("email_verified") var emailVerified: Boolean? = null,
    @SerialName("phone_number") var phonenumber: String? = null,
    @SerialName("phone_number_verified") var phoneNumberVerified: Boolean? = null,
    @SerialName("address") var address: Address? = null,
    @SerialName("credit_score") var creditScore: Int? = null,
    @SerialName("active") var active: Boolean? = null,
    @SerialName("country_of_birth") var countryOfBirth: String? = null,
    @SerialName("region_of_birth") var regionOfBirth: String? = null,
    @SerialName("city_of_birth") var cityOfBirth: String? = null,
    @SerialName("occupation") var occupation: String? = null,
    @SerialName("employer_name") var employerName: String? = null,
    @SerialName("identification_type") var identificationType: String? = null,
    @SerialName("identification_value") var identificationValue: String? = null
)
