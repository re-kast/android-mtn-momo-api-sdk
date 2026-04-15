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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkResultTest {

    @Test
    fun `Success holds the response data`() {
        val result = NetworkResult.Success("hello")
        assertEquals("hello", result.response)
        assertNull(result.message)
    }

    @Test
    fun `Error holds message and no response by default`() {
        val result = NetworkResult.Error<String>("something went wrong")
        assertEquals("something went wrong", result.message)
        assertNull(result.response)
    }

    @Test
    fun `Error can hold both message and response`() {
        val result = NetworkResult.Error("bad request", "partial-data")
        assertEquals("bad request", result.message)
        assertEquals("partial-data", result.response)
    }

    @Test
    fun `Loading has no response and no message`() {
        val result = NetworkResult.Loading<String>()
        assertNull(result.response)
        assertNull(result.message)
    }

    @Test
    fun `Success is an instance of NetworkResult`() {
        val result = NetworkResult.Success(42)
        assertTrue(result is NetworkResult<*>)
        assertTrue(result is NetworkResult.Success<*>)
    }

    @Test
    fun `Error is an instance of NetworkResult`() {
        val result = NetworkResult.Error<Int>("error")
        assertTrue(result is NetworkResult<*>)
        assertTrue(result is NetworkResult.Error<*>)
    }

    @Test
    fun `Loading is an instance of NetworkResult`() {
        val result = NetworkResult.Loading<Int>()
        assertTrue(result is NetworkResult<*>)
        assertTrue(result is NetworkResult.Loading<*>)
    }

    @Test
    fun `Success with null response is still Success`() {
        val result = NetworkResult.Success<String?>(null)
        assertTrue(result is NetworkResult.Success)
        assertNull(result.response)
    }
}
