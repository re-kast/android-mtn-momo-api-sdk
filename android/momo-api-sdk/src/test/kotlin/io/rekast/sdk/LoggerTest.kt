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
package io.rekast.sdk

import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Unit tests for the JVM `actual` implementation of [Logger].
 *
 * Captures stdout to verify that each log level produces the correct output format
 * (`{LEVEL}/{tag}: {message}`). The Android `actual` is tested indirectly via the
 * instrumented test suite since it requires a running Android runtime.
 */
class LoggerTest {

    private val originalOut = System.out
    private lateinit var captured: ByteArrayOutputStream

    @Before
    fun redirectStdout() {
        captured = ByteArrayOutputStream()
        System.setOut(PrintStream(captured))
    }

    @After
    fun restoreStdout() {
        System.setOut(originalOut)
    }

    private fun output() = captured.toString().trim()

    // ---- d() ----

    @Test
    fun `d() writes D-prefixed line to stdout`() {
        Logger.d("MyTag", "debug message")
        assertTrue(output().contains("D/MyTag: debug message"))
    }

    // ---- i() ----

    @Test
    fun `i() writes I-prefixed line to stdout`() {
        Logger.i("MyTag", "info message")
        assertTrue(output().contains("I/MyTag: info message"))
    }

    // ---- w() ----

    @Test
    fun `w() writes W-prefixed line to stdout`() {
        Logger.w("MyTag", "warning message")
        assertTrue(output().contains("W/MyTag: warning message"))
    }

    // ---- e() ----

    @Test
    fun `e() without throwable writes E-prefixed line to stdout`() {
        Logger.e("MyTag", "error message")
        assertTrue(output().contains("E/MyTag: error message"))
    }

    @Test
    fun `e() with throwable writes E-prefixed line and stack trace`() {
        val ex = RuntimeException("boom")
        Logger.e("MyTag", "error with exception", ex)
        val out = output()
        assertTrue(out.contains("E/MyTag: error with exception"))
        assertTrue(out.contains("RuntimeException"))
    }

    @Test
    fun `e() with null throwable does not throw`() {
        Logger.e("MyTag", "error message", null)
        assertTrue(output().contains("E/MyTag: error message"))
    }

    // ---- tag and message content ----

    @Test
    fun `log output includes the exact tag and message supplied`() {
        Logger.d("TokenAuthenticator", "token refreshed successfully")
        assertTrue(output().contains("D/TokenAuthenticator: token refreshed successfully"))
    }

    @Test
    fun `log handles empty tag and message`() {
        Logger.d("", "")
        assertTrue(output().contains("D/: "))
    }
}
