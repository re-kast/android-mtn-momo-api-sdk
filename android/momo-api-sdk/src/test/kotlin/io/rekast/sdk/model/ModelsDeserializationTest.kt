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

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Deserialization tests for core SDK model classes.
 *
 * Verifies that each model's `@SerialName` annotations map correctly from the API JSON
 * keys to Kotlin properties, and that nullable / defaulted fields behave correctly when
 * their corresponding keys are absent from the payload.
 */
class ModelsDeserializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    /** Verifies that [AccountBalance.availableBalance] and [AccountBalance.currency] are mapped correctly. */
    @Test
    fun `AccountBalance fields are mapped correctly from JSON`() {
        val raw = """
            {
              "availableBalance": "1500.00",
              "currency": "EUR"
            }
        """.trimIndent()
        val result = json.decodeFromString<AccountBalance>(raw)
        assertNotNull(result)
        assertEquals("1500.00", result.availableBalance)
        assertEquals("EUR", result.currency)
    }

    /** Verifies that [AccountHolder.partyIdType] and [AccountHolder.partyId] are mapped correctly. */
    @Test
    fun `AccountHolder fields are mapped correctly from JSON`() {
        val raw = """
            {
              "partyIdType": "MSISDN",
              "partyId": "256700000000"
            }
        """.trimIndent()
        val result = json.decodeFromString<AccountHolder>(raw)
        assertNotNull(result)
        assertEquals("MSISDN", result.partyIdType)
        assertEquals("256700000000", result.partyId)
    }

    /** Verifies that [AccountHolderStatus.result] is `true` when the JSON contains `true`. */
    @Test
    fun `AccountHolderStatus result is true when JSON contains true`() {
        val raw = """{ "result": true }"""
        val result = json.decodeFromString<AccountHolderStatus>(raw)
        assertNotNull(result)
        assertEquals(true, result.result)
    }

    /** Verifies that [AccountHolderStatus.result] is `false` when the JSON contains `false`. */
    @Test
    fun `AccountHolderStatus result is false when JSON contains false`() {
        val raw = """{ "result": false }"""
        val result = json.decodeFromString<AccountHolderStatus>(raw)
        assertNotNull(result)
        assertEquals(false, result.result)
    }

    /** Verifies that all [ErrorResponse] fields are mapped correctly from JSON. */
    @Test
    fun `ErrorResponse fields are mapped correctly from JSON`() {
        val raw = """
            {
              "code": "RESOURCE_NOT_FOUND",
              "message": "Requested resource was not found.",
              "error": "404"
            }
        """.trimIndent()
        val result = json.decodeFromString<ErrorResponse>(raw)
        assertNotNull(result)
        assertEquals("RESOURCE_NOT_FOUND", result.code)
        assertEquals("Requested resource was not found.", result.message)
        assertEquals("404", result.error)
    }

    /** Verifies that [MomoNotification.notificationMessage] is mapped correctly from JSON. */
    @Test
    fun `MomoNotification fields are mapped correctly from JSON`() {
        val raw = """{ "notificationMessage": "Payment received successfully." }"""
        val result = json.decodeFromString<MomoNotification>(raw)
        assertNotNull(result)
        assertEquals("Payment received successfully.", result.notificationMessage)
    }

    /** Verifies that [ProviderCallBackHost.providerCallbackHost] is mapped correctly from JSON. */
    @Test
    fun `ProviderCallBackHost fields are mapped correctly from JSON`() {
        val raw = """{ "providerCallbackHost": "https://example.com/callback" }"""
        val result = json.decodeFromString<ProviderCallBackHost>(raw)
        assertNotNull(result)
        assertEquals("https://example.com/callback", result.providerCallbackHost)
    }

    /**
     * Verifies that [ProviderCallBackHost.providerCallbackHost] defaults to `null` when the key
     * is absent, confirming the field is genuinely nullable.
     */
    @Test
    fun `ProviderCallBackHost providerCallbackHost defaults to null when absent`() {
        val raw = """{}"""
        val result = json.decodeFromString<ProviderCallBackHost>(raw)
        assertNotNull(result)
        assertNull(result.providerCallbackHost)
    }

    /** Verifies that all required [MomoTransaction] fields and the nested [AccountHolder] payer are mapped. */
    @Test
    fun `MomoTransaction required fields are mapped correctly from JSON`() {
        val raw = """
            {
              "amount": "500.00",
              "currency": "UGX",
              "externalId": "ext-001",
              "payerMessage": "Payment for goods",
              "payeeNote": "Thank you",
              "payer": {
                "partyIdType": "MSISDN",
                "partyId": "256700000001"
              }
            }
        """.trimIndent()
        val result = json.decodeFromString<MomoTransaction>(raw)
        assertNotNull(result)
        assertEquals("500.00", result.amount)
        assertEquals("UGX", result.currency)
        assertEquals("ext-001", result.externalId)
        assertEquals("Payment for goods", result.payerMessage)
        assertEquals("Thank you", result.payeeNote)
        assertNotNull(result.payer)
        assertEquals("MSISDN", result.payer?.partyIdType)
        assertEquals("256700000001", result.payer?.partyId)
    }

    /**
     * Verifies that optional [MomoTransaction] fields default to empty strings or null when
     * absent from the payload, matching the field default values declared on the model.
     */
    @Test
    fun `MomoTransaction optional fields default correctly when absent`() {
        val raw = """
            {
              "amount": "100.00",
              "currency": "GHS",
              "externalId": "ext-002",
              "payerMessage": "msg",
              "payeeNote": "note"
            }
        """.trimIndent()
        val result = json.decodeFromString<MomoTransaction>(raw)
        assertNotNull(result)
        assertEquals("", result.financialTransactionId)
        assertNull(result.payee)
        assertNull(result.payer)
        assertEquals("", result.status)
        assertEquals("", result.reason)
        assertEquals("", result.referenceIdToRefund)
    }

    /**
     * Verifies that both [MomoTransaction.payee] and [MomoTransaction.payer] are mapped correctly
     * when both are present, along with all other non-nullable fields.
     */
    @Test
    fun `MomoTransaction payee and payer are mapped when both present`() {
        val raw = """
            {
              "amount": "250.00",
              "currency": "XOF",
              "financialTransactionId": "fin-txn-999",
              "externalId": "ext-003",
              "payee": {
                "partyIdType": "MSISDN",
                "partyId": "221700000002"
              },
              "payer": {
                "partyIdType": "MSISDN",
                "partyId": "221700000003"
              },
              "payerMessage": "Invoice #42",
              "payeeNote": "Received",
              "status": "SUCCESSFUL",
              "reason": "",
              "referenceIdToRefund": "ref-001"
            }
        """.trimIndent()
        val result = json.decodeFromString<MomoTransaction>(raw)
        assertNotNull(result)
        assertEquals("fin-txn-999", result.financialTransactionId)
        assertEquals("SUCCESSFUL", result.status)
        assertEquals("ref-001", result.referenceIdToRefund)
        assertEquals("221700000002", result.payee?.partyId)
        assertEquals("221700000003", result.payer?.partyId)
    }

    /** Verifies that all [PaymentResult] fields are mapped correctly from the MTN MoMo response keys. */
    @Test
    fun `PaymentResult fields are mapped correctly from JSON`() {
        val raw = """
            {
              "MerchantRequestID": "mrq-001",
              "CheckoutRequestID": "crq-001",
              "ResponseCode": "0",
              "ResponseDescription": "Success",
              "CustomerMessage": "Payment accepted"
            }
        """.trimIndent()
        val result = json.decodeFromString<PaymentResult>(raw)
        assertNotNull(result)
        assertEquals("mrq-001", result.merchantRequestID)
        assertEquals("crq-001", result.checkoutRequestID)
        assertEquals("0", result.responseCode)
        assertEquals("Success", result.responseDescription)
        assertEquals("Payment accepted", result.customerMessage)
    }
}
