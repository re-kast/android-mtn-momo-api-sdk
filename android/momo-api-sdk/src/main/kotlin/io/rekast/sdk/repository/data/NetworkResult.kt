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
package io.rekast.sdk.repository.data

/**
 * A sealed class representing the result of a network operation.
 *
 * This class encapsulates the possible outcomes of a network request,
 * including success, error, and loading states.
 *
 * @param T The type of the response data.
 * @property response The response data if the operation was successful.
 * @property message An optional message providing additional information about the result.
 */
sealed class NetworkResult<T>(
    val response: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful network operation.
     *
     * @param response The successful response data.
     */
    class Success<T>(response: T) : NetworkResult<T>(response)

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
