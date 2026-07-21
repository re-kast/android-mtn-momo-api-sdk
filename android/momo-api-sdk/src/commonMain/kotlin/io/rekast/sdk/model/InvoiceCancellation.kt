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
 * Request body sent when cancelling a Collection invoice.
 *
 * MTN's cancel-invoice endpoint is a `DELETE` that carries a JSON body identifying the
 * cancellation, e.g. `{ "externalId": "..." }`.
 *
 * @property externalId A client-supplied correlation ID for the cancellation request.
 */
@Serializable
data class InvoiceCancellation(@SerialName("externalId") val externalId: String)
