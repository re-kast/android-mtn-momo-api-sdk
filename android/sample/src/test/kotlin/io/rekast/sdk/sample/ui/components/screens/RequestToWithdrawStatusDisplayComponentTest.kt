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
import io.rekast.sdk.model.ErrorResponse
import io.rekast.sdk.model.Party
import io.rekast.sdk.model.RequestToWithdrawStatus
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.utils.PartyTypes
import io.rekast.sdk.utils.StatusTypes
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for [RequestToWithdrawStatusDisplayComponent], run on the JVM via Robolectric.
 *
 * Covers the empty (null status) branch where every [io.rekast.sdk.sample.ui.components.general.InfoRow]
 * is skipped, and the populated branch rendering the payer, amount, status, and failure reason.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class RequestToWithdrawStatusDisplayComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setDisplay(status: RequestToWithdrawStatus?) {
        composeRule.setContent {
            AppTheme {
                RequestToWithdrawStatusDisplayComponent(
                    title = "Request to Withdraw Status",
                    status = MutableLiveData(status)
                )
            }
        }
    }

    /** With no status the card title shows but every value row is omitted. */
    @Test
    fun `renders title with no status`() {
        setDisplay(null)
        composeRule.onNodeWithText("Request to Withdraw Status").assertIsDisplayed()
        composeRule.onNodeWithText("1500").assertDoesNotExist()
    }

    /** A populated status renders the payer, amount, status, and failure reason. */
    @Test
    fun `renders status fields with payer and reason`() {
        setDisplay(
            RequestToWithdrawStatus(
                amount = "1500",
                currency = "EUR",
                externalId = "947354",
                payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
                payerMessage = "Payment for goods",
                payeeNote = "Monthly subscription",
                status = StatusTypes.FAILED,
                reason = ErrorResponse(code = "PAYER_NOT_FOUND", message = "Payer could not be reached")
            )
        )
        composeRule.onNodeWithText("1500").assertIsDisplayed()
        composeRule.onNodeWithText("EUR").assertIsDisplayed()
        composeRule.onNodeWithText("FAILED").assertIsDisplayed()
        composeRule.onNodeWithText("Payer:").assertIsDisplayed()
        composeRule.onNodeWithText("256770000000 -- msisdn").assertIsDisplayed()
        composeRule.onNodeWithText("Payer could not be reached").assertIsDisplayed()
    }
}
