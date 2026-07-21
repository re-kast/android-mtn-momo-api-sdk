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
 * Wraps the response of the MTN MOMO Collection `getApprovedPreApprovals` endpoint, which returns a
 * `preApprovalDetails` array of [PreApprovalDetails] entries for the queried account holder.
 *
 * @property preApprovalDetails The list of approved pre-approvals; empty when none are returned.
 */
@Serializable
data class ApprovedPreApprovals(@SerialName("preApprovalDetails") val preApprovalDetails: List<PreApprovalDetails> = emptyList())
