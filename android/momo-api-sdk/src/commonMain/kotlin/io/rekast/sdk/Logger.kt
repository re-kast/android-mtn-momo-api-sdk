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

/**
 * Platform-agnostic logging abstraction for the MTN MOMO SDK.
 *
 * Use this in `commonMain` code instead of importing platform-specific logging libraries.
 * Each platform supplies its own `actual` implementation:
 * - **Android** (`androidMain`): delegates to `Timber`, which must be planted in
 *   your `Application.onCreate` (e.g. `Timber.plant(Timber.DebugTree())`).
 * - **JVM** (`jvmMain`): writes to standard output in the format `{LEVEL}/{tag}: {message}`.
 *
 * Usage in `commonMain`:
 * ```kotlin
 * Logger.d("MyClass", "debug message")
 * Logger.i("MyClass", "info message")
 * Logger.w("MyClass", "warning message")
 * Logger.e("MyClass", "error message", throwable)
 * ```
 */
expect object Logger {
    /** Logs a debug-level message. */
    fun d(tag: String, message: String)

    /** Logs an info-level message. */
    fun i(tag: String, message: String)

    /** Logs a warning-level message. */
    fun w(tag: String, message: String)

    /**
     * Logs an error-level message.
     *
     * @param throwable Optional exception to log alongside the message.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null)
}
