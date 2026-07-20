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
package io.rekast.sdk.sample.views.collection.payment

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

/** Compose UI tests for the Collection Payment screen ([PaymentScreen]), via Robolectric. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class PaymentScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val snackFlow = MutableSharedFlow<SnackBarComponentConfiguration>().asSharedFlow()

    private fun viewModel() = PaymentScreenViewModel(
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
                PaymentScreen(navController = null, snackStateFlow = snackFlow, showProgressBar = showProgressBar, viewModel = viewModel())
            }
        }
    }

    private fun setScreen(viewModel: PaymentScreenViewModel?, showProgressBar: Boolean = false) {
        composeRule.setContent {
            AppTheme {
                PaymentScreen(navController = null, snackStateFlow = snackFlow, showProgressBar = showProgressBar, viewModel = viewModel)
            }
        }
    }

    @Test
    fun `renders create form`() {
        setScreen()
        composeRule.onNodeWithText("Create Payment").assertIsDisplayed()
    }

    @Test
    fun `hides form while loading`() {
        setScreen(showProgressBar = true)
        composeRule.onNodeWithText("Create Payment").assertDoesNotExist()
    }

    @Test
    fun `shows loader when view model is null`() {
        setScreen(viewModel = null)
        composeRule.onNodeWithText("Create Payment").assertDoesNotExist()
    }

    @Test
    fun `enables create when amount is set`() {
        val vm = viewModel()
        vm.onAmountChanged("100")
        setScreen(viewModel = vm)
        composeRule.onNodeWithText("Create").assertIsEnabled()
    }

    @Test
    fun `disables create when amount is blank`() {
        setScreen()
        composeRule.onNodeWithText("Create").assertIsNotEnabled()
    }

    @Test
    fun `enables status when reference is set`() {
        val vm = viewModel()
        vm.referenceId.value = "ref-1"
        setScreen(viewModel = vm)
        composeRule.onNodeWithText("Check Status").assertIsEnabled()
    }
}
