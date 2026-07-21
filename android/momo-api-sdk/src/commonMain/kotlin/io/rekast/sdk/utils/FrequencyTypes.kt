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
 * Enum class representing how often a Collection pre-approval may be used.
 *
 * Used as the typed `frequency` field on [io.rekast.sdk.model.PreApprovalDetails].
 */
@Serializable
enum class FrequencyTypes {
    /**
     * The pre-approval may be used once per day.
     */
    DAILY,

    /**
     * The pre-approval may be used once per week.
     */
    WEEKLY,

    /**
     * The pre-approval may be used once per month.
     */
    MONTHLY
}
