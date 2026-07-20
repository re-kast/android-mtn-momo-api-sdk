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
package io.rekast.sdk.sample.views.disbursement.deposit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import io.mockk.mockk
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Compose UI tests for the Disbursement Deposit screen ([DisbursementScreen]), via Robolectric. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class DisbursementDepositScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val snackFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow()

    private fun viewModel() = DisbursementDepositScreenViewModel(
        mockk(relaxed = true),
        mockk(relaxed = true),
        object : DispatcherProvider {
            override fun io(): CoroutineDispatcher = Dispatchers.Unconfined
        },
        mockk(relaxed = true)
    )

    private fun setScreen(showProgressBar: Boolean = false, momoTransaction: MutableLiveData<MomoTransaction?> = MutableLiveData(null)) {
        composeRule.setContent {
            AppTheme {
                DisbursementScreen(null, snackFlow, showProgressBar, viewModel(), momoTransaction)
            }
        }
    }

    @Test
    fun `renders capture form`() {
        setScreen()
        composeRule.onNodeWithText("Request to Deposit").assertIsDisplayed()
    }

    @Test
    fun `hides form while loading`() {
        setScreen(showProgressBar = true)
        composeRule.onNodeWithText("Request to Deposit").assertDoesNotExist()
    }

    /** When a transaction result is present, the result summary (amount) is shown instead of the form. */
    @Test
    fun `renders result view when transaction present`() {
        val sampleTransaction = MomoTransaction(
            amount = "100",
            currency = "EUR",
            externalId = "ext-1",
            payerMessage = "msg",
            payeeNote = "note"
        )
        setScreen(momoTransaction = MutableLiveData(sampleTransaction))
        composeRule.onNodeWithText("100").assertIsDisplayed()
    }
}
