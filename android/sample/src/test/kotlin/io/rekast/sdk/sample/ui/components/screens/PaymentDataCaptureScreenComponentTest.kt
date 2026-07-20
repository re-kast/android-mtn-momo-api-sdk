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
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for the screens-package card-based [PaymentDataScreenComponent], run on the JVM
 * via Robolectric.
 *
 * Covers the three optional-field toggles (financial ID, reference-to-refund, delivery note) in both
 * their shown and hidden states, plus the enabled submit path (with click callback) and the disabled
 * state when a required field is empty.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// A tall viewport so the whole (non-scrolling) capture form fits on screen and every optional
// field plus the submit button is reported as displayed / is clickable.
@Config(sdk = [34], qualifiers = "w411dp-h3000dp")
class PaymentDataCaptureScreenComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setForm(
        phoneNumber: String = "256770000000",
        amount: String = "1000",
        paymentMessage: String = "msg",
        paymentNote: String = "note",
        showFinancialId: Boolean = true,
        showReferenceIdToRefund: Boolean = true,
        showDeliveryTextField: Boolean = true,
        onSubmit: () -> Unit = {}
    ) {
        composeRule.setContent {
            AppTheme {
                PaymentDataScreenComponent(
                    title = "Screen Title",
                    submitButtonText = "Pay Now",
                    phoneNumber = phoneNumber,
                    financialId = "",
                    showFinancialId = showFinancialId,
                    referenceIdToRefund = "",
                    showReferenceIdToRefund = showReferenceIdToRefund,
                    amount = amount,
                    paymentMessage = paymentMessage,
                    paymentNote = paymentNote,
                    deliveryNote = "",
                    showDeliveryTextField = showDeliveryTextField,
                    onRequestPayButtonClicked = onSubmit,
                    onPhoneNumberUpdated = {},
                    onFinancialIdUpdated = {},
                    onReferenceIdToRefundUpdated = {},
                    onAmountUpdated = {},
                    onPayerMessageUpdated = {},
                    onPayerNoteUpdated = {},
                    onDeliveryNoteUpdated = {}
                )
            }
        }
    }

    /** With all toggles on, the optional fields render and the populated form enables submit. */
    @Test
    fun `renders optional fields and enables submit`() {
        var clicked = false
        setForm(onSubmit = { clicked = true })
        composeRule.onNodeWithText("Screen Title").assertIsDisplayed()
        composeRule.onNodeWithText("Financial Id *").assertIsDisplayed()
        composeRule.onNodeWithText("Reference Id To Refund *").assertIsDisplayed()
        composeRule.onNodeWithText("Delivery note *").assertIsDisplayed()
        composeRule.onNodeWithText("Pay Now").assertIsEnabled().performClick()
        assertTrue(clicked)
    }

    /** With all toggles off, the optional fields are omitted while the form still renders. */
    @Test
    fun `hides optional fields when toggled off`() {
        setForm(
            showFinancialId = false,
            showReferenceIdToRefund = false,
            showDeliveryTextField = false
        )
        composeRule.onNodeWithText("Screen Title").assertIsDisplayed()
        composeRule.onNodeWithText("Financial Id *").assertDoesNotExist()
        composeRule.onNodeWithText("Reference Id To Refund *").assertDoesNotExist()
        composeRule.onNodeWithText("Delivery note *").assertDoesNotExist()
    }

    /** The submit button is disabled while a required field is empty. */
    @Test
    fun `disables submit when required field empty`() {
        setForm(paymentNote = "")
        composeRule.onNodeWithText("Pay Now").assertIsNotEnabled()
    }
}
