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

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class DataResponseTest {

    private val dataResponse = object : DataResponse() {}

    @Test
    fun `safeApiCall returns Success when response is successful with a body`() = runBlocking {
        val body = "response-data"
        val response = Response.success(body)

        val result = dataResponse.safeApiCall { response }

        assertTrue(result is NetworkResult.Success)
        assertEquals(body, result.response)
    }

    @Test
    fun `safeApiCall returns Error when response is successful but body is null`() = runBlocking {
        val response = Response.success<String>(null)

        val result = dataResponse.safeApiCall { response }

        assertTrue(result is NetworkResult.Error)
        assertNotNull(result.message)
    }

    @Test
    fun `safeApiCall returns Error when response is not successful`() = runBlocking {
        val errorBody = "error".toResponseBody("text/plain".toMediaType())
        val response = Response.error<String>(404, errorBody)

        val result = dataResponse.safeApiCall { response }

        assertTrue(result is NetworkResult.Error)
        assertTrue(result.message!!.contains("404"))
    }

    @Test
    fun `safeApiCall returns Error when an exception is thrown`() = runBlocking {
        val result = dataResponse.safeApiCall<String> { throw RuntimeException("network failure") }

        assertTrue(result is NetworkResult.Error)
        assertTrue(result.message!!.contains("network failure"))
    }

    @Test
    fun `safeApiCall error message contains response code and message`() = runBlocking {
        val errorBody = "not found".toResponseBody("text/plain".toMediaType())
        val response = Response.error<String>(404, errorBody)

        val result = dataResponse.safeApiCall { response }

        assertTrue(result is NetworkResult.Error)
        assertTrue(result.message!!.contains("404"))
    }

    @Test
    fun `safeApiCall with suspend lambda is called exactly once`() = runBlocking {
        val mockCall = mockk<suspend () -> Response<String>>()
        coEvery { mockCall() } returns Response.success("data")

        dataResponse.safeApiCall { mockCall() }

        io.mockk.coVerify(exactly = 1) { mockCall() }
    }
}
