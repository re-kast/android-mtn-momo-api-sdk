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
package io.rekast.sdk.sample.utils

import io.rekast.sdk.repository.data.NetworkResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests for [bodyText]. Exercises both branches of the single nullable [NetworkResult.response] read.
 */
class NetworkResultExtensionsTest {

    @Test
    fun `bodyText returns the decoded UTF-8 body when a response is present`() {
        val result = NetworkResult.Success("""{"status":"SUCCESSFUL"}""".toResponseBody("application/json".toMediaType()))
        assertEquals("""{"status":"SUCCESSFUL"}""", result.bodyText())
    }

    @Test
    fun `bodyText returns null when the response body is absent`() {
        @Suppress("UNCHECKED_CAST")
        val result = NetworkResult.Success(null) as NetworkResult<ResponseBody>
        assertNull(result.bodyText())
    }

    @Test
    fun `bodyText returns null for an error result`() {
        val result = NetworkResult.Error<ResponseBody>("boom")
        assertNull(result.bodyText())
    }
}
