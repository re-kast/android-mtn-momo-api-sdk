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
package io.rekast.sdk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents extended user information returned by the MTN MOMO API after the user grants consent.
 *
 * This combines the basic profile from [BasicUserInfo] with additional verified and financial details.
 *
 * @property userBasicInfo Core profile fields such as name, gender, and locale.
 * @property status The current status of the user account.
 * @property middleName The user's middle name.
 * @property email The user's email address.
 * @property emailVerified Whether the user's email address has been verified.
 * @property phonenumber The user's phone number.
 * @property phoneNumberVerified Whether the user's phone number has been verified.
 * @property address The user's physical address.
 * @property updatedAt The timestamp of the last profile update.
 * @property creditScore The user's credit score as reported by the MTN MOMO system.
 * @property active Indicates whether the user's account is active.
 * @property countryOfBirth The country where the user was born.
 * @property regionOfBirth The region where the user was born.
 * @property cityOfBirth The city where the user was born.
 * @property occupation The user's occupation.
 * @property employerName The name of the user's employer.
 * @property identificationType The type of identification document provided (e.g., passport, national ID).
 * @property identificationValue The value/number of the identification document.
 */
@Serializable
data class UserInfoWithConsent(
    var userBasicInfo: BasicUserInfo,
    @SerialName("status") var status: String,
    @SerialName("middle_name") var middleName: String,
    @SerialName("email") var email: String,
    @SerialName("email_verified") var emailVerified: Boolean,
    @SerialName("phone_number") var phonenumber: String,
    @SerialName("phone_number_verified") var phoneNumberVerified: Boolean,
    @SerialName("address") var address: String,
    @SerialName("updated_at") var updatedAt: String,
    @SerialName("credit_score") var creditScore: String,
    @SerialName("active") var active: String,
    @SerialName("country_of_birth") var countryOfBirth: String,
    @SerialName("region_of_birth") var regionOfBirth: String,
    @SerialName("city_of_birth") var cityOfBirth: String,
    @SerialName("occupation") var occupation: String,
    @SerialName("employer_name") var employerName: String,
    @SerialName("identification_type") var identificationType: String,
    @SerialName("identification_value") var identificationValue: String
)
