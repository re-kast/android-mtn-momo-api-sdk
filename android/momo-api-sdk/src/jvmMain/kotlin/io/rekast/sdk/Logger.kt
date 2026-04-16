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

/**
 * JVM-specific implementation of the platform-agnostic [Logger].
 *
 * Writes log output to standard output using the format `D/<tag>: <message>`,
 * suitable for server-side or desktop JVM use cases where Timber is unavailable.
 */
actual object Logger {
    /**
     * Prints a debug message to standard output.
     *
     * @param tag Log tag, typically the calling class name.
     * @param message Human-readable message to log.
     */
    actual fun d(tag: String, message: String) {
        println("D/$tag: $message")
    }
}
