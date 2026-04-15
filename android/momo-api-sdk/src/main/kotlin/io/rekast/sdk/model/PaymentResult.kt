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

/**
 * Represents the result returned after initiating a payment request.
 *
 * @property merchantRequestID The merchant-assigned request identifier for the payment.
 * @property checkoutRequestID The checkout request identifier assigned by the payment gateway.
 * @property responseCode The response code indicating the outcome of the payment request.
 * @property responseDescription A human-readable description of the response code.
 * @property customerMessage A message intended to be displayed to the customer.
 */
data class PaymentResult(var merchantRequestID: String, var checkoutRequestID: String, var responseCode: String, var responseDescription: String, var customerMessage: String)
