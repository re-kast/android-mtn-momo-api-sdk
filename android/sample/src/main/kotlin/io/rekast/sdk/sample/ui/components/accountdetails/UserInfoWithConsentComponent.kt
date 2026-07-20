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
package io.rekast.sdk.sample.ui.components.accountdetails

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.Address
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.InfoRow
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.ui.components.general.RowDivider

/**
 * Renders a card displaying the extended, consent-granted user profile returned by the MTN MOMO
 * `userinfo` endpoint — identity, contact, address, birthplace, employment, and identification
 * details. Every field is optional and omitted from the layout when absent.
 *
 * @param modifier Modifier applied to the card.
 * @param userInfoWithConsent LiveData holding the [UserInfoWithConsent] to display.
 */
@Composable
fun UserInfoWithConsentComponent(modifier: Modifier = Modifier, userInfoWithConsent: MutableLiveData<UserInfoWithConsent?>) {
    val info by userInfoWithConsent.observeAsState()
    MomoCard(modifier = modifier) {
        CardTitle(title = stringResource(id = R.string.card_verified_profile))
        Spacer(modifier = Modifier.height(12.dp))

        InfoRow(label = stringResource(id = R.string.label_given_name), value = info?.givenName)
        InfoRow(label = stringResource(id = R.string.label_family_name), value = info?.familyName)
        InfoRow(label = stringResource(id = R.string.label_middle_name), value = info?.middleName)
        InfoRow(label = stringResource(id = R.string.label_birth_date), value = info?.birthDate)
        InfoRow(label = stringResource(id = R.string.label_gender), value = info?.gender)
        InfoRow(label = stringResource(id = R.string.label_email), value = info?.email)
        InfoRow(label = stringResource(id = R.string.label_phone), value = info?.phonenumber)
        InfoRow(label = stringResource(id = R.string.label_locale), value = info?.locale)

        val addressLine = info?.address?.readable().orEmpty()
        if (addressLine.isNotBlank()) {
            RowDivider()
            InfoRow(label = stringResource(id = R.string.label_address), value = addressLine)
        }

        val hasBirthplace = !info?.countryOfBirth.isNullOrBlank() ||
            !info?.regionOfBirth.isNullOrBlank() ||
            !info?.cityOfBirth.isNullOrBlank()
        if (hasBirthplace) {
            RowDivider()
            InfoRow(label = stringResource(id = R.string.label_country_of_birth), value = info?.countryOfBirth)
            InfoRow(label = stringResource(id = R.string.label_region_of_birth), value = info?.regionOfBirth)
            InfoRow(label = stringResource(id = R.string.label_city_of_birth), value = info?.cityOfBirth)
        }

        val hasEmployment = !info?.occupation.isNullOrBlank() || !info?.employerName.isNullOrBlank()
        if (hasEmployment) {
            RowDivider()
            InfoRow(label = stringResource(id = R.string.label_occupation), value = info?.occupation)
            InfoRow(label = stringResource(id = R.string.label_employer), value = info?.employerName)
        }

        val hasIdentification = !info?.identificationType.isNullOrBlank() ||
            !info?.identificationValue.isNullOrBlank() ||
            info?.creditScore != null
        if (hasIdentification) {
            RowDivider()
            InfoRow(label = stringResource(id = R.string.label_identification_type), value = info?.identificationType)
            InfoRow(label = stringResource(id = R.string.label_identification_value), value = info?.identificationValue)
            InfoRow(label = stringResource(id = R.string.label_credit_score), value = info?.creditScore?.toString())
        }

        InfoRow(label = stringResource(id = R.string.label_sub), value = info?.sub)
    }
}

/** Collapses an [Address] into a single, comma-separated line, skipping blank parts. */
private fun Address.readable(): String = listOfNotNull(streetAddress, locality, postalCode, region, country)
    .filter { it.isNotBlank() }
    .joinToString(", ")
    .ifBlank { formatted?.replace("\n", ", ").orEmpty() }

@Preview(showBackground = true)
@Composable
fun UserInfoWithConsentComponentPreview() {
    UserInfoWithConsentComponent(
        userInfoWithConsent = MutableLiveData(
            UserInfoWithConsent(
                sub = "0",
                name = "Sand Box",
                givenName = "Sand",
                familyName = "Box",
                birthDate = "1976-08-13",
                gender = "MALE",
                email = "email@domain.test",
                phonenumber = "46123456789",
                address = Address(
                    streetAddress = "Street 17",
                    postalCode = "123 45",
                    locality = "Karlskrona",
                    region = "Blekinge",
                    country = "Sweden"
                ),
                creditScore = 123,
                occupation = "Manager",
                employerName = "Ericsson",
                identificationType = "PASS",
                identificationValue = "S1234567"
            )
        )
    )
}
