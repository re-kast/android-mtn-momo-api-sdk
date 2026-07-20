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
 * Represents a payment request to the MTN MOMO API. Reuses [Money] for the [money] amount.
 *
 * Every field is optional so partial payloads never break deserialization.
 *
 * @property externalTransactionId An external transaction id to tie to the payment.
 * @property money The amount and currency of the payment.
 * @property customerReference A customer reference for a provider (e.g., +46070911111).
 * @property serviceProviderUserName A service provider name (e.g., "Electricity Inc.").
 * @property couponId A coupon the user would like to redeem as part of this payment.
 * @property productId Optional id of a product, used if paying for a product.
 * @property productOfferingId Optional id of a product offering, used when paying for a particular offering of a product.
 * @property receiverMessage A descriptive note for the receiver's transaction history.
 * @property senderNote A descriptive note for the sender's transaction history.
 * @property maxNumberOfRetries The maximum number of retries.
 * @property includeSenderCharges Whether sender charges (fee and tax paid by the sender) are included
 *   in the transaction amount. When true, charges are deducted from the amount before it is
 *   transferred to the receiver; the default (false) charges them on top of the transaction amount.
 */
@Serializable
data class Payment(
    @SerialName("externalTransactionId") var externalTransactionId: String? = null,
    @SerialName("money") var money: Money? = null,
    @SerialName("customerReference") var customerReference: String? = null,
    @SerialName("serviceProviderUserName") var serviceProviderUserName: String? = null,
    @SerialName("couponId") var couponId: String? = null,
    @SerialName("productId") var productId: String? = null,
    @SerialName("productOfferingId") var productOfferingId: String? = null,
    @SerialName("receiverMessage") var receiverMessage: String? = null,
    @SerialName("senderNote") var senderNote: String? = null,
    @SerialName("maxNumberOfRetries") var maxNumberOfRetries: Int? = null,
    @SerialName("includeSenderCharges") var includeSenderCharges: Boolean? = null
)
