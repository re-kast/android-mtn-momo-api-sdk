/*
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package io.rekast.sdk.callback

/**
 * Holds the state for Payment status.
 */
sealed class MomoResponse<out T> {
    data class Success<out T>(val value: T) : MomoResponse<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val momoException: MomoException?
    ) : MomoResponse<Nothing>()
}
