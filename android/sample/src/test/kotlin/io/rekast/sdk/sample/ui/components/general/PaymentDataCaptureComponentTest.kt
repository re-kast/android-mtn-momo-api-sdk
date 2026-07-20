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
package io.rekast.sdk.sample.ui.components.general

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.rekast.sdk.sample.ui.theme.AppTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI tests for the general-package [PaymentDataScreenComponent] capture form, run on the JVM
 * via Robolectric.
 *
 * Covers the rendered title/submit label, the enabled submit path (all fields populated) with its
 * click callback, and the disabled state when a required field is empty.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// A tall viewport so the whole (non-scrolling) capture form — including the submit button at the
// bottom — fits on screen and is reported as displayed / is clickable.
@Config(sdk = [34], qualifiers = "w411dp-h3000dp")
class PaymentDataCaptureComponentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setForm(phoneNumber: String = "", financialId: String = "", amount: String = "", paymentMessage: String = "", paymentNote: String = "", onSubmit: () -> Unit = {}) {
        composeRule.setContent {
            AppTheme {
                PaymentDataScreenComponent(
                    title = "Capture Title",
                    submitButtonText = "Submit Now",
                    phoneNumber = phoneNumber,
                    financialId = financialId,
                    amount = amount,
                    paymentMessage = paymentMessage,
                    paymentNote = paymentNote,
                    onRequestPayButtonClicked = onSubmit,
                    onPhoneNumberUpdated = {},
                    onFinancialIdUpdated = {},
                    onAmountUpdated = {},
                    onPayerMessageUpdated = {},
                    onPayerNoteUpdated = {}
                )
            }
        }
    }

    /** The form renders its section title and submit button label. */
    @Test
    fun `renders title and submit label`() {
        setForm()
        composeRule.onNodeWithText("Capture Title").assertIsDisplayed()
        composeRule.onNodeWithText("Submit Now").assertIsDisplayed()
    }

    /** When every field is populated the submit button is enabled and invokes its callback. */
    @Test
    fun `submit enabled and clickable when all fields populated`() {
        var clicked = false
        setForm(
            phoneNumber = "256770000000",
            financialId = "fin-1",
            amount = "1000",
            paymentMessage = "msg",
            paymentNote = "note",
            onSubmit = { clicked = true }
        )
        composeRule.onNodeWithText("Submit Now").assertIsEnabled().performClick()
        assertTrue(clicked)
    }

    /** With a required field empty the submit button is disabled and never fires its callback. */
    @Test
    fun `submit disabled when a field is empty`() {
        var clicked = false
        setForm(
            phoneNumber = "256770000000",
            financialId = "fin-1",
            amount = "1000",
            paymentMessage = "msg",
            paymentNote = "",
            onSubmit = { clicked = true }
        )
        composeRule.onNodeWithText("Submit Now").assertIsNotEnabled()
        assertFalse(clicked)
    }

    /** An empty phone number disables submit (drives the first `isNotEmpty()` false branch). */
    @Test
    fun `submit disabled when phone number is empty`() {
        setForm(
            phoneNumber = "",
            financialId = "fin-1",
            amount = "1000",
            paymentMessage = "msg",
            paymentNote = "note"
        )
        composeRule.onNodeWithText("Submit Now").assertIsNotEnabled()
    }

    /** An empty financial ID disables submit (drives the second `isNotEmpty()` false branch). */
    @Test
    fun `submit disabled when financial id is empty`() {
        setForm(
            phoneNumber = "256770000000",
            financialId = "",
            amount = "1000",
            paymentMessage = "msg",
            paymentNote = "note"
        )
        composeRule.onNodeWithText("Submit Now").assertIsNotEnabled()
    }

    /** An empty amount disables submit (drives the third `isNotEmpty()` false branch). */
    @Test
    fun `submit disabled when amount is empty`() {
        setForm(
            phoneNumber = "256770000000",
            financialId = "fin-1",
            amount = "",
            paymentMessage = "msg",
            paymentNote = "note"
        )
        composeRule.onNodeWithText("Submit Now").assertIsNotEnabled()
    }

    /** An empty payment message disables submit (drives the fourth `isNotEmpty()` false branch). */
    @Test
    fun `submit disabled when payment message is empty`() {
        setForm(
            phoneNumber = "256770000000",
            financialId = "fin-1",
            amount = "1000",
            paymentMessage = "",
            paymentNote = "note"
        )
        composeRule.onNodeWithText("Submit Now").assertIsNotEnabled()
    }
}
