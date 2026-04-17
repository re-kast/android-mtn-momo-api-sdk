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

    /**
     * Redirects [System.out] to an in-memory buffer before each test so that
     * [Logger] output can be inspected without writing to the real console.
     */
    @Before
    fun redirectStdout() {
        captured = ByteArrayOutputStream()
        System.setOut(PrintStream(captured))
    }

    /**
     * Restores [System.out] to its original stream after each test to prevent
     * stdout suppression from leaking into other test classes.
     */
    @After
    fun restoreStdout() {
        System.setOut(originalOut)
    }

    /** Returns the captured stdout content trimmed of leading/trailing whitespace. */
    private fun output() = captured.toString().trim()

    // ---- d() ----

    /**
     * Verifies that [Logger.d] writes a line prefixed with `D/` to stdout.
     */
    @Test
    fun `d() writes D-prefixed line to stdout`() {
        Logger.d("MyTag", "debug message")
        assertTrue(output().contains("D/MyTag: debug message"))
    }

    // ---- i() ----

    /**
     * Verifies that [Logger.i] writes a line prefixed with `I/` to stdout.
     */
    @Test
    fun `i() writes I-prefixed line to stdout`() {
        Logger.i("MyTag", "info message")
        assertTrue(output().contains("I/MyTag: info message"))
    }

    // ---- w() ----

    /**
     * Verifies that [Logger.w] writes a line prefixed with `W/` to stdout.
     */
    @Test
    fun `w() writes W-prefixed line to stdout`() {
        Logger.w("MyTag", "warning message")
        assertTrue(output().contains("W/MyTag: warning message"))
    }

    // ---- e() ----

    /**
     * Verifies that [Logger.e] without a throwable writes a line prefixed with `E/` to stdout.
     */
    @Test
    fun `e() without throwable writes E-prefixed line to stdout`() {
        Logger.e("MyTag", "error message")
        assertTrue(output().contains("E/MyTag: error message"))
    }

    /**
     * Verifies that [Logger.e] with a non-null throwable writes the `E/`-prefixed message and
     * includes the exception class name in the stack trace output.
     */
    @Test
    fun `e() with throwable writes E-prefixed line and stack trace`() {
        val ex = RuntimeException("boom")
        Logger.e("MyTag", "error with exception", ex)
        val out = output()
        assertTrue(out.contains("E/MyTag: error with exception"))
        assertTrue(out.contains("RuntimeException"))
    }

    /**
     * Verifies that [Logger.e] with a `null` throwable does not throw and still writes
     * the `E/`-prefixed message to stdout.
     */
    @Test
    fun `e() with null throwable does not throw`() {
        Logger.e("MyTag", "error message", null)
        assertTrue(output().contains("E/MyTag: error message"))
    }

    // ---- tag and message content ----

    /**
     * Verifies that the exact tag and message strings supplied by the caller appear in the output,
     * confirming no truncation or transformation occurs.
     */
    @Test
    fun `log output includes the exact tag and message supplied`() {
        Logger.d("TokenAuthenticator", "token refreshed successfully")
        assertTrue(output().contains("D/TokenAuthenticator: token refreshed successfully"))
    }

    /**
     * Verifies that [Logger.d] handles empty tag and message strings without throwing,
     * producing a `D/: ` line in stdout.
     */
    @Test
    fun `log handles empty tag and message`() {
        Logger.d("", "")
        assertTrue(output().contains("D/: "))
    }
}
