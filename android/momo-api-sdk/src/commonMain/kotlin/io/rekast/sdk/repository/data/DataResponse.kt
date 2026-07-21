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

import io.rekast.sdk.model.ErrorResponse
import kotlinx.serialization.json.Json
import retrofit2.Response

/**
 * Abstract class that provides a method for safely making API calls.
 *
 * This class contains a method to handle API requests and return results
 * wrapped in a [NetworkResult]. It handles both successful responses and errors.
 */
abstract class DataResponse {

    private val errorJson = Json { ignoreUnknownKeys = true }

    /**
     * Safely makes an API call and returns the result.
     *
     * This method executes the provided API request and checks if the response
     * is successful. If successful, it returns the response body wrapped in a
     * [NetworkResult.Success]. If the response is not successful or an exception
     * occurs, it returns an error wrapped in a [NetworkResult.Error].
     *
     * @param apiRequest A suspend function that represents the API request to be made.
     * @return A [NetworkResult] containing the result of the API call.
     */
    suspend fun <T> safeApiCall(apiRequest: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiRequest()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
                return error("${response.code()} ${response.message()}")
            }
            return error(errorMessage(response.code(), response.message(), response.errorBody()?.string()))
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    /**
     * Builds a human-readable error string that always includes the HTTP status code and, when the
     * server returned an error body, the server's own message.
     *
     * The MoMo API returns error bodies like `{"code":"...","message":"..."}`. When the body parses
     * as an [ErrorResponse] the `code: message` pair is appended; otherwise the raw body text is used
     * so nothing the server sent is ever silently discarded.
     */
    private fun errorMessage(code: Int, reasonPhrase: String, errorBody: String?): String {
        val status = "$code $reasonPhrase"
        val detail = errorBody?.takeIf { it.isNotBlank() }?.let { body ->
            runCatching { errorJson.decodeFromString(ErrorResponse.serializer(), body) }
                .getOrNull()
                ?.let { parsed -> "${parsed.code}: ${parsed.message}" }
                ?: body
        }
        return if (detail.isNullOrBlank()) status else "$status - $detail"
    }

    /**
     * Creates an error result for the API call.
     *
     * This method wraps the provided error message in a [NetworkResult.Error].
     *
     * @param errorMessage The error message to be returned.
     * @return A [NetworkResult.Error] containing the error message.
     */
    private fun <T> error(errorMessage: String): NetworkResult<T> = NetworkResult.Error(" : $errorMessage")
}
