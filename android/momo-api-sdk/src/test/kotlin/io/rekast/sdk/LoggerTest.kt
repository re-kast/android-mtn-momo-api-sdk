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
package io.rekast.sdk

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import timber.log.Timber

/**
 * Unit tests for the Android `actual` implementation of [Logger].
 *
 * The `src/test/` source set maps to `androidHostTest`, which links against `androidMain` actuals.
 * [Logger] on Android delegates to [Timber], so tests plant a [CapturingTree] before each test
 * to intercept log calls and assert that the correct tag, message, and optional throwable are
 * forwarded to Timber.
 *
 * `isReturnDefaultValues = true` is set in the module's `withHostTestBuilder` configuration,
 * which allows Android framework APIs (including those Timber uses internally) to return safe
 * defaults instead of throwing in the host JVM environment.
 */
class LoggerTest {

    private val tree = CapturingTree()

    /**
     * Plants the [CapturingTree] so that all [Timber] calls during a test are recorded
     * rather than discarded.
     */
    @Before
    fun setUp() {
        Timber.plant(tree)
    }

    /**
     * Uproots the [CapturingTree] after each test to prevent log entries from one test
     * leaking into the next.
     */
    @After
    fun tearDown() {
        Timber.uproot(tree)
    }

    /**
     * Verifies that [Logger.d] forwards the tag and message to Timber as a debug log entry.
     */
    @Test
    fun `d() forwards tag and message to Timber`() {
        Logger.d("MyTag", "debug message")
        assertTrue(tree.contains("MyTag", "debug message"))
    }

    /**
     * Verifies that [Logger.i] forwards the tag and message to Timber as an info log entry.
     */
    @Test
    fun `i() forwards tag and message to Timber`() {
        Logger.i("MyTag", "info message")
        assertTrue(tree.contains("MyTag", "info message"))
    }

    /**
     * Verifies that [Logger.w] forwards the tag and message to Timber as a warning log entry.
     */
    @Test
    fun `w() forwards tag and message to Timber`() {
        Logger.w("MyTag", "warning message")
        assertTrue(tree.contains("MyTag", "warning message"))
    }

    /**
     * Verifies that [Logger.e] without a throwable forwards only the tag and message, with no
     * throwable attached to the Timber log entry.
     */
    @Test
    fun `e() without throwable forwards tag and message with no throwable`() {
        Logger.e("MyTag", "error message")
        assertTrue(tree.contains("MyTag", "error message"))
        assertNull(tree.lastThrowable)
    }

    /**
     * Verifies that [Logger.e] with a non-null throwable attaches the throwable to the Timber
     * log entry. Timber 5.x appends the throwable's stack trace to the formatted message before
     * calling [Timber.Tree.log], so only the throwable reference is asserted here.
     */
    @Test
    fun `e() with throwable forwards tag, message, and throwable to Timber`() {
        val ex = RuntimeException("boom")
        Logger.e("MyTag", "error with exception", ex)
        assertEquals(1, tree.size)
        assertEquals(ex, tree.lastThrowable)
    }

    /**
     * Verifies that [Logger.e] with a `null` throwable does not attach a throwable to the
     * Timber log entry.
     */
    @Test
    fun `e() with null throwable records no throwable`() {
        Logger.e("MyTag", "error message", null)
        assertTrue(tree.contains("MyTag", "error message"))
        assertNull(tree.lastThrowable)
    }

    // ---- tag and message content ----

    /**
     * Verifies that the exact tag and message strings are forwarded unchanged to Timber,
     * confirming no truncation or transformation occurs.
     */
    @Test
    fun `log preserves exact tag and message`() {
        Logger.d("TokenAuthenticator", "token refreshed successfully")
        assertTrue(tree.contains("TokenAuthenticator", "token refreshed successfully"))
    }

    /**
     * Verifies that [Logger.d] with empty tag and message strings does not throw.
     * Timber 5.x silently drops empty messages, so no log entry is recorded.
     */
    @Test
    fun `log handles empty tag and message`() {
        Logger.d("", "")
        assertEquals(0, tree.size)
    }
}

/**
 * A [Timber.Tree] implementation that records every log call so that tests can assert on
 * the tag, message, and optional throwable passed to Timber.
 */
private class CapturingTree : Timber.Tree() {

    private data class Entry(val tag: String?, val message: String, val throwable: Throwable?)

    private val entries = mutableListOf<Entry>()

    /** The number of log entries recorded so far. */
    val size: Int get() = entries.size

    /** The throwable attached to the most recent log entry, or `null` if none was supplied. */
    val lastThrowable: Throwable? get() = entries.lastOrNull()?.throwable

    /**
     * Returns `true` if any recorded entry matches the given [tag] and [message] exactly.
     */
    fun contains(tag: String, message: String): Boolean = entries.any { it.tag == tag && it.message == message }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        entries.add(Entry(tag, message, t))
    }
}
