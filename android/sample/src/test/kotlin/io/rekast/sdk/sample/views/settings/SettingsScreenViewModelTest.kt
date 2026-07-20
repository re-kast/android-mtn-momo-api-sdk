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
package io.rekast.sdk.sample.views.settings

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [SettingsScreenViewModel].
 *
 * Verifies that the host app build metadata is read from the [PackageManager] (with a graceful
 * fallback), that the static config is exposed, and that [SettingsScreenViewModel.clearCredentials]
 * wipes secure storage and emits an informational snackbar.
 *
 * Runs under Robolectric so `Build.VERSION.SDK_INT` is a real, configurable value: the class default
 * (API 34) exercises the modern `longVersionCode` path, and one test pins API 26 to cover the
 * legacy `versionCode` fallback.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsScreenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockPackageManager = mockk<PackageManager>(relaxed = true)
    private val mockStorage = mockk<CredentialStorage>(relaxed = true)
    private val mockConfig = mockk<SampleConfig>(relaxed = true)

    @Suppress("DEPRECATION")
    private fun packageInfo() = PackageInfo().apply {
        versionName = "1.2.3"
        versionCode = 42
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { mockContext.packageName } returns "io.rekast.sdk.sample"
        every { mockContext.packageManager } returns mockPackageManager
        every { mockPackageManager.getPackageInfo("io.rekast.sdk.sample", 0) } returns packageInfo()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SettingsScreenViewModel(mockContext, mockStorage, mockConfig)

    /** appInfo is populated from the host package metadata read on construction (modern SDK path). */
    @Test
    fun `appInfo reads package metadata`() {
        val viewModel = createViewModel()

        assertEquals("1.2.3", viewModel.appInfo.versionName)
        assertEquals("42", viewModel.appInfo.versionCode)
        assertEquals("io.rekast.sdk.sample", viewModel.appInfo.packageName)
    }

    /** On pre-P devices the legacy `versionCode` field is used to read the build version code. */
    @Test
    @Config(sdk = [26])
    fun `appInfo reads legacy version code on pre-P devices`() {
        val viewModel = createViewModel()

        assertEquals("42", viewModel.appInfo.versionCode)
    }

    /** A missing package degrades gracefully to blank version fields rather than crashing. */
    @Test
    fun `appInfo falls back gracefully when package info is unavailable`() {
        every { mockPackageManager.getPackageInfo("io.rekast.sdk.sample", 0) } throws PackageManager.NameNotFoundException()

        val viewModel = createViewModel()

        assertEquals("", viewModel.appInfo.versionName)
        assertEquals("", viewModel.appInfo.versionCode)
        assertEquals("io.rekast.sdk.sample", viewModel.appInfo.packageName)
    }

    /** The static config is exposed unchanged for the screen to render. */
    @Test
    fun `config is exposed`() {
        assertEquals(mockConfig, createViewModel().config)
    }

    /** clearCredentials wipes secure storage and emits exactly one INFO snackbar. */
    @Test
    fun `clearCredentials clears storage and emits snackbar`() = runTest {
        val viewModel = createViewModel()
        val emissions = mutableListOf<SnackBarComponentConfiguration>()
        val job = launch(testDispatcher) { viewModel.snackBarStateFlow.collect { emissions.add(it) } }

        viewModel.clearCredentials()
        job.cancel()

        verify { mockStorage.clearAll() }
        assertEquals(1, emissions.size)
        assertEquals(SnackBarType.INFO, emissions.first().type)
    }
}
