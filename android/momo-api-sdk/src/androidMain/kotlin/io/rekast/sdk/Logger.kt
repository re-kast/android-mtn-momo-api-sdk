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
 * Android `actual` implementation of [Logger].
 *
 * Delegates to [timber.log.Timber]. Timber must be planted in your `Application.onCreate`
 * before the SDK is used — e.g. `Timber.plant(Timber.DebugTree())`.
 */
actual object Logger {
    actual fun d(tag: String, message: String) = Timber.tag(tag).d(message)

    actual fun i(tag: String, message: String) = Timber.tag(tag).i(message)

    actual fun w(tag: String, message: String) = Timber.tag(tag).w(message)

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Timber.tag(tag).e(throwable, message) else Timber.tag(tag).e(message)
    }
}
