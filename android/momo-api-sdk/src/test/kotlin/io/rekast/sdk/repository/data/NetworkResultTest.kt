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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the [NetworkResult] sealed class hierarchy.
 *
 * Verifies construction and property access for each subtype:
 * - [NetworkResult.Success] — carries a non-null or null response, no message
 * - [NetworkResult.Error] — carries a message and an optional partial response
 * - [NetworkResult.Loading] — carries neither message nor response
 *
 * Also confirms that each subtype is a proper instance of [NetworkResult].
 */
class NetworkResultTest {

    /** Verifies Success stores the response and leaves message empty. */
    @Test
    fun `Success holds the response data`() {
        val result = NetworkResult.Success("hello")
        assertEquals("hello", result.response)
        assertEquals("", result.message)
    }

    /** Verifies Success exposes its response through the non-null [NetworkResult.Success.data] accessor. */
    @Test
    fun `Success exposes response through non-null data accessor`() {
        val result = NetworkResult.Success("hello")
        assertEquals("hello", result.data)
    }

    /** Verifies Error stores the message and leaves response null when not provided. */
    @Test
    fun `Error holds message and no response by default`() {
        val result = NetworkResult.Error<String>("something went wrong")
        assertEquals("something went wrong", result.message)
        assertNull(result.response)
    }

    /** Verifies Error can carry both a message and a partial response simultaneously. */
    @Test
    fun `Error can hold both message and response`() {
        val result = NetworkResult.Error("bad request", "partial-data")
        assertEquals("bad request", result.message)
        assertEquals("partial-data", result.response)
    }

    /** Verifies Loading is constructed with a null response and an empty message. */
    @Test
    fun `Loading has no response and no message`() {
        val result = NetworkResult.Loading<String>()
        assertNull(result.response)
        assertEquals("", result.message)
    }

    /** Verifies Success is a subtype of NetworkResult by confirming smart-cast to the sealed parent works. */
    @Test
    fun `Success is an instance of NetworkResult`() {
        val result: NetworkResult<Int> = NetworkResult.Success(42)
        assertTrue(result is NetworkResult.Success<*>)
    }

    /** Verifies Error is a subtype of NetworkResult by confirming smart-cast to the sealed parent works. */
    @Test
    fun `Error is an instance of NetworkResult`() {
        val result: NetworkResult<Int> = NetworkResult.Error("error")
        assertTrue(result is NetworkResult.Error<*>)
    }

    /** Verifies Loading is a subtype of NetworkResult by confirming smart-cast to the sealed parent works. */
    @Test
    fun `Loading is an instance of NetworkResult`() {
        val result: NetworkResult<Int> = NetworkResult.Loading()
        assertTrue(result is NetworkResult.Loading<*>)
    }

    /** Verifies Success accepts a null response and stores it correctly. */
    @Test
    fun `Success with null response stores null`() {
        val result = NetworkResult.Success<String?>(null)
        assertNull(result.response)
        assertEquals("", result.message)
    }
}
