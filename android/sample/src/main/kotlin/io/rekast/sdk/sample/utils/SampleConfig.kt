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
package io.rekast.sdk.sample.utils

/**
 * Runtime configuration values required by the sample app to initialise the MTN MOMO SDK.
 *
 * All values are sourced from [io.rekast.sdk.app.BuildConfig] (populated via the Secrets Gradle
 * Plugin from `local.properties`) and injected by `AppModule` through Hilt.
 *
 * @property apiVersionV1 API version string for v1 endpoints (e.g. `"v1_0"` on sandbox, `"v1"` on production).
 * @property apiVersionV2 API version string for v2 endpoints (e.g. `"v2_0"` on sandbox, `"v2"` on production).
 * @property environment Target environment identifier (e.g. `"sandbox"` or `"production"`).
 * @property providerCallbackHost Host used for provider callback URLs (e.g. `"localhost"` on sandbox).
 * @property apiUserId UUID identifying the API user created on the MTN MOMO Developer portal.
 * @property collectionPrimaryKey Primary subscription key for the Collection product.
 * @property collectionSecondaryKey Secondary subscription key for the Collection product.
 * @property remittancePrimaryKey Primary subscription key for the Remittance product.
 * @property remittanceSecondaryKey Secondary subscription key for the Remittance product.
 * @property disbursementsPrimaryKey Primary subscription key for the Disbursements product.
 * @property disbursementsSecondaryKey Secondary subscription key for the Disbursements product.
 * @property callbackBaseUrl Base URL for transaction callbacks; each initiation operation's path
 *   segment is appended to it to form the `X-Callback-Url`. Empty disables callbacks (the default).
 */
data class SampleConfig(
    val apiVersionV1: String,
    val apiVersionV2: String,
    val environment: String,
    val providerCallbackHost: String,
    val apiUserId: String,
    val collectionPrimaryKey: String,
    val collectionSecondaryKey: String,
    val remittancePrimaryKey: String,
    val remittanceSecondaryKey: String,
    val disbursementsPrimaryKey: String,
    val disbursementsSecondaryKey: String,
    val callbackBaseUrl: String = ""
)
