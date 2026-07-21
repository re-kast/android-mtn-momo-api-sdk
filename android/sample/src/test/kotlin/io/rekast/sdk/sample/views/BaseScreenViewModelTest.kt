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
package io.rekast.sdk.sample.views

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [BaseScreenViewModel] — the abstract base every sample screen ViewModel extends.
 *
 * Because the shared helpers are `protected`, the tests drive them through a minimal concrete
 * subclass ([TestScreenViewModel]) that re-exposes each member publicly. This verifies the shared
 * contract once, centrally, rather than relying on incidental coverage from each subclass:
 * - the progress-bar flag defaults to `false`;
 * - [BaseScreenViewModel.generateUuid] returns unique, well-formed v4 UUID strings;
 * - the snackbar emitters push the correct [SnackBarComponentConfiguration] onto `snackBarStateFlow`;
 * - `awaitTerminal` skips [NetworkResult.Loading] emissions and returns the terminal result (or a
 *   default error when the flow never produces one).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BaseScreenViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    /** Concrete subclass that surfaces the base class's `protected` members for testing. */
    private class TestScreenViewModel : BaseScreenViewModel() {
        fun generateUuidPublic(): String = generateUuid()
        fun emitSuccessPublic(messageResId: Int, vararg args: Any) = emitSuccess(messageResId, *args)
        fun emitErrorPublic(messageResId: Int, vararg args: Any) = emitError(messageResId, *args)
        fun emitSnackBarStatePublic(config: SnackBarComponentConfiguration) = emitSnackBarState(config)
        suspend fun <T> awaitTerminalPublic(flow: Flow<NetworkResult<T>>): NetworkResult<T> = flow.awaitTerminal()
    }

    private lateinit var viewModel: TestScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TestScreenViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** The progress-bar flag starts hidden so screens render their content by default. */
    @Test
    fun `showProgressBar defaults to false`() {
        assertFalse(viewModel.showProgressBar.value!!)
    }

    /** generateUuid produces a standard 8-4-4-4-12 hex UUID string. */
    @Test
    fun `generateUuid returns a value in UUID format`() {
        val uuidRegex = Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        assertTrue(uuidRegex.matches(viewModel.generateUuidPublic()))
    }

    /** Successive calls to generateUuid never collide, so each transaction gets a unique reference. */
    @Test
    fun `generateUuid returns unique values on each call`() {
        assertNotEquals(viewModel.generateUuidPublic(), viewModel.generateUuidPublic())
    }

    /** emitSuccess pushes exactly one SUCCESS snackbar carrying the supplied resource id and args. */
    @Test
    fun `emitSuccess emits a SUCCESS snackbar with the message and args`() = runTest {
        val emissions = mutableListOf<SnackBarComponentConfiguration>()
        val job = launch(testDispatcher) { viewModel.snackBarStateFlow.collect { emissions.add(it) } }

        viewModel.emitSuccessPublic(101, "arg-1")
        job.cancel()

        assertEquals(1, emissions.size)
        assertEquals(SnackBarType.SUCCESS, emissions.first().type)
        assertEquals(101, emissions.first().messageResId)
        assertEquals(listOf<Any>("arg-1"), emissions.first().messageArgs)
    }

    /** emitError pushes exactly one ERROR snackbar carrying the supplied resource id. */
    @Test
    fun `emitError emits an ERROR snackbar with the message`() = runTest {
        val emissions = mutableListOf<SnackBarComponentConfiguration>()
        val job = launch(testDispatcher) { viewModel.snackBarStateFlow.collect { emissions.add(it) } }

        viewModel.emitErrorPublic(202)
        job.cancel()

        assertEquals(1, emissions.size)
        assertEquals(SnackBarType.ERROR, emissions.first().type)
        assertEquals(202, emissions.first().messageResId)
    }

    /** emitSnackBarState forwards an arbitrary configuration unchanged onto the flow. */
    @Test
    fun `emitSnackBarState emits the given configuration`() = runTest {
        val emissions = mutableListOf<SnackBarComponentConfiguration>()
        val job = launch(testDispatcher) { viewModel.snackBarStateFlow.collect { emissions.add(it) } }

        val config = SnackBarComponentConfiguration(messageResId = 303, type = SnackBarType.INFO)
        viewModel.emitSnackBarStatePublic(config)
        job.cancel()

        assertEquals(1, emissions.size)
        assertEquals(config, emissions.first())
    }

    /** awaitTerminal ignores a leading Loading emission and returns the terminal Success. */
    @Test
    fun `awaitTerminal skips loading and returns terminal success`() = runTest {
        val result = viewModel.awaitTerminalPublic(
            flowOf(NetworkResult.Loading<String>(), NetworkResult.Success("done"))
        )

        assertTrue(result is NetworkResult.Success)
        assertEquals("done", (result as NetworkResult.Success).response)
    }

    /** awaitTerminal returns the terminal Error when the flow ends in failure. */
    @Test
    fun `awaitTerminal returns terminal error`() = runTest {
        val result = viewModel.awaitTerminalPublic(
            flowOf(NetworkResult.Loading<String>(), NetworkResult.Error<String>("boom"))
        )

        assertTrue(result is NetworkResult.Error)
        assertEquals("boom", result.message)
    }

    /** A flow that only ever emits Loading yields the default "no response" error rather than hanging. */
    @Test
    fun `awaitTerminal returns default error when only loading is emitted`() = runTest {
        val result = viewModel.awaitTerminalPublic(flowOf(NetworkResult.Loading<String>()))

        assertTrue(result is NetworkResult.Error)
        assertEquals("No response received", result.message)
    }
}
