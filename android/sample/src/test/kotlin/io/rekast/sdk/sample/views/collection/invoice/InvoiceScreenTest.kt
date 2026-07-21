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
package io.rekast.sdk.sample.views.collection.invoice

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.mockk.mockk
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

/** Compose UI tests for the Collection Invoice screen ([InvoiceScreen]), via Robolectric. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class InvoiceScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val snackFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow()

    private fun viewModel() = InvoiceScreenViewModel(
        mockk(relaxed = true),
        mockk(relaxed = true),
        object : DispatcherProvider {
            override fun io(): CoroutineDispatcher = Dispatchers.Unconfined
        },
        mockk(relaxed = true)
    )

    private fun setScreen(showProgressBar: Boolean = false) {
        composeRule.setContent {
            AppTheme {
                InvoiceScreen(navController = null, snackStateFlow = snackFlow, showProgressBar = showProgressBar, viewModel = viewModel())
            }
        }
    }

    private fun setScreen(viewModel: InvoiceScreenViewModel?, showProgressBar: Boolean = false) {
        composeRule.setContent {
            AppTheme {
                InvoiceScreen(navController = null, snackStateFlow = snackFlow, showProgressBar = showProgressBar, viewModel = viewModel)
            }
        }
    }

    @Test
    fun `renders create form`() {
        setScreen()
        composeRule.onNodeWithText("Create Invoice").assertIsDisplayed()
    }

    @Test
    fun `hides form while loading`() {
        setScreen(showProgressBar = true)
        composeRule.onNodeWithText("Create Invoice").assertDoesNotExist()
    }

    @Test
    fun `shows loader when view model is null`() {
        setScreen(viewModel = null)
        composeRule.onNodeWithText("Create Invoice").assertDoesNotExist()
    }

    @Test
    fun `enables create when amount, payer, and payee are set`() {
        val vm = viewModel()
        vm.onAmountChanged("100")
        vm.onPayerMsisdnChanged("256700000000")
        vm.onPayeeMsisdnChanged("256700000001")
        setScreen(viewModel = vm)
        composeRule.onNodeWithText("Create").assertIsEnabled()
    }

    @Test
    fun `disables create when payer is blank`() {
        val vm = viewModel()
        vm.onAmountChanged("100")
        vm.onPayeeMsisdnChanged("256700000001")
        setScreen(viewModel = vm)
        composeRule.onNodeWithText("Create").assertIsNotEnabled()
    }

    @Test
    fun `disables create when payee is blank`() {
        val vm = viewModel()
        vm.onAmountChanged("100")
        vm.onPayerMsisdnChanged("256700000000")
        setScreen(viewModel = vm)
        composeRule.onNodeWithText("Create").assertIsNotEnabled()
    }

    @Test
    fun `enables status and cancel when reference is set`() {
        val vm = viewModel()
        vm.referenceId.value = "ref-1"
        setScreen(viewModel = vm)
        composeRule.onNodeWithText("Check Status").assertIsEnabled()
        composeRule.onNodeWithText("Cancel").assertIsEnabled()
    }
}
