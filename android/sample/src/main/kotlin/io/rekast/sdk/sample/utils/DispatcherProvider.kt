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
package io.rekast.sdk.sample.utils

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Abstraction over the standard [CoroutineDispatcher] set to allow dispatcher injection in tests.
 */
interface DispatcherProvider {
    /** Returns the [Dispatchers.Main] dispatcher for UI-thread work. */
    fun main(): CoroutineDispatcher = Dispatchers.Main

    /** Returns the [Dispatchers.Default] dispatcher for CPU-intensive work. */
    fun default(): CoroutineDispatcher = Dispatchers.Default

    /** Returns the [Dispatchers.IO] dispatcher for I/O-bound work. */
    fun io(): CoroutineDispatcher = Dispatchers.IO

    /** Returns the [Dispatchers.Unconfined] dispatcher that is not confined to any thread. */
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}

/**
 * Production [DispatcherProvider] implementation that delegates to the real [Dispatchers].
 */
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider
