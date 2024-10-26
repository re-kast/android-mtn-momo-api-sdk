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
package io.rekast.sdk.model.api

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoWithConsent(
    var userBasicInfo: BasicUserInfo,
    @SerializedName("status") var status: String,
    @SerializedName("middle_name") var middleName: String,
    @SerializedName("email") var email: String,
    @SerializedName("email_verified") var emailVerified: Boolean,
    @SerializedName("phone_number") var phonenumber: String,
    @SerializedName("phone_number_verified") var phoneNumberVerified: Boolean,
    @SerializedName("address") var address: String,
    @SerializedName("updated_at") var updatedAt: String,
    @SerializedName("credit_score") var creditScore: String,
    @SerializedName("active") var active: String,
    @SerializedName("country_of_birth") var countryOfBirth: String,
    @SerializedName("region_of_birth") var regionOfBirth: String,
    @SerializedName("city_of_birth") var cityOfBirth: String,
    @SerializedName("occupation") var occupation: String,
    @SerializedName("employer_name") var employerName: String,
    @SerializedName("identification_type") var identificationType: String,
    @SerializedName("identification_value") var identificationValue: String
)
