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

import timber.log.Timber

/**
 * Android-specific implementation of the platform-agnostic [Logger].
 *
 * Delegates all log output to [timber.log.Timber], which must be planted
 * (e.g. via `Timber.plant(Timber.DebugTree())`) in the application's
 * `onCreate` before any SDK calls are made.
 */
actual object Logger {
    /**
     * Logs a debug message via Timber using the given [tag].
     *
     * @param tag Log tag, typically the calling class name.
     * @param message Human-readable message to log.
     */
    actual fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }
}
