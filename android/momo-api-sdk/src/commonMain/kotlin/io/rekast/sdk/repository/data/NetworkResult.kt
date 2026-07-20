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
package io.rekast.sdk.repository.data

/**
 * A sealed class representing the result of a network operation.
 *
 * This class encapsulates the possible outcomes of a network request,
 * including success, error, and loading states.
 *
 * @param T The type of the response data.
 * @property response The response data if the operation was successful.
 * @property message A message providing additional information about the result; empty by
 * default for [Success] and [Loading], and always populated for [Error].
 */
sealed class NetworkResult<T>(val response: T? = null, val message: String = "") {
    /**
     * Represents a successful network operation.
     *
     * @param response The successful response data.
     * @property data The successful response data as a non-null value. Because a [Success] is only
     * ever constructed with a non-null response, this offers null-free access without the
     * defensive checks a nullable [NetworkResult.response] would otherwise require.
     */
    class Success<T>(response: T) : NetworkResult<T>(response) {
        val data: T = response
    }

    /**
     * Represents an error that occurred during a network operation.
     *
     * @param message A message describing the error.
     * @param response The response data, if any, associated with the error.
     */
    class Error<T>(message: String, response: T? = null) : NetworkResult<T>(response, message)

    /**
     * Represents a loading state for a network operation.
     *
     * This can be used to indicate that a network request is in progress.
     */
    class Loading<T> : NetworkResult<T>()
}
