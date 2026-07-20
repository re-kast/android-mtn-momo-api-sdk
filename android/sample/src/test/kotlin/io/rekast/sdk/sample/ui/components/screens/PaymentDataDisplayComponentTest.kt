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
package io.rekast.sdk.sample.ui.components.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [PaymentDataDisplayComponent], run on the JVM via Robolectric.
 *
 * Covers the empty (null transaction) branch where every [io.rekast.sdk.sample.ui.components.general.InfoRow]
 * is skipped, the populated branch with a payee (the "Payee" counterparty label), and the
 * payer-only branch (the "Payer" counterparty label).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class PaymentDataDisplayComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setDisplay(transaction: MomoTransaction?) {
        composeRule.setContent {
            AppTheme {
                PaymentDataDisplayComponent(
                    title = "Transaction Summary",
                    momoTransaction = MutableLiveData(transaction)
                )
            }
        }
    }

    /** With no transaction the card title shows but every value row is omitted. */
    @Test
    fun `renders title with no transaction`() {
        setDisplay(null)
        composeRule.onNodeWithText("Transaction Summary").assertIsDisplayed()
        composeRule.onNodeWithText("1500").assertDoesNotExist()
    }

    /** A transaction with a payee renders its fields and the "Payee" counterparty label. */
    @Test
    fun `renders transaction fields with payee`() {
        setDisplay(
            MomoTransaction(
                amount = "1500",
                currency = "EUR",
                externalId = "947354",
                payee = AccountHolder(partyIdType = "msisdn", partyId = "256770000000"),
                payerMessage = "Payment for goods",
                payeeNote = "Monthly subscription",
                status = "SUCCESSFUL"
            )
        )
        composeRule.onNodeWithText("1500").assertIsDisplayed()
        composeRule.onNodeWithText("EUR").assertIsDisplayed()
        composeRule.onNodeWithText("SUCCESSFUL").assertIsDisplayed()
        composeRule.onNodeWithText("Payee:").assertIsDisplayed()
        composeRule.onNodeWithText("256770000000 -- msisdn").assertIsDisplayed()
    }

    /** A payer-only transaction (no payee) renders the "Payer" counterparty label. */
    @Test
    fun `renders payer label when payee absent`() {
        setDisplay(
            MomoTransaction(
                amount = "2000",
                currency = "UGX",
                externalId = "111222",
                payer = AccountHolder(partyIdType = "msisdn", partyId = "256711111111"),
                payerMessage = "Send",
                payeeNote = "Note"
            )
        )
        composeRule.onNodeWithText("Payer:").assertIsDisplayed()
        composeRule.onNodeWithText("256711111111 -- msisdn").assertIsDisplayed()
    }
}
