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
 * JVM `actual` implementation of [Logger].
 *
 * Writes to standard output in the format `{LEVEL}/{tag}: {message}`.
 * For error logs, any supplied [Throwable] stack trace is printed via [Throwable.printStackTrace].
 */
actual object Logger {
    actual fun d(tag: String, message: String) = println("D/$tag: $message")

    actual fun i(tag: String, message: String) = println("I/$tag: $message")

    actual fun w(tag: String, message: String) = println("W/$tag: $message")

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        println("E/$tag: $message")
        throwable?.printStackTrace()
    }
}
