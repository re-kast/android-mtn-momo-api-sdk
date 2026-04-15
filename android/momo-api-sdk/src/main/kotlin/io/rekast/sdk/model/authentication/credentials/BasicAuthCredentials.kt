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
package io.rekast.sdk.model.authentication.credentials

import javax.inject.Inject

/**
 * Data class representing Basic Authentication credentials.
 *
 * @param apiUserId The API user ID.
 * @param apiKey The API key.
 */
data class BasicAuthCredentials @Inject constructor(
    var apiUserId: String,
    var apiKey: String
)
