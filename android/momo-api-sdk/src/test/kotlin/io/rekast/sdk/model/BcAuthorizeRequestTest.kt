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
package io.rekast.sdk.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BcAuthorizeRequestTest {

    private fun fullBcAuthorizeRequest() = BcAuthorizeRequest(loginHint = "hint", scope = "profile", accessType = "offline")

    @Test
    fun `BcAuthorizeRequest equal instances are equal with same hashCode`() {
        val a = fullBcAuthorizeRequest()
        val b = fullBcAuthorizeRequest()
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `BcAuthorizeRequest instance equals itself and differs from null and other type`() {
        val a = fullBcAuthorizeRequest()
        assertEquals(a, a)
        assertNotEquals(a, null)
        assertNotEquals(a, "x")
    }

    @Test
    fun `BcAuthorizeRequest changing any field breaks equality`() {
        val b = fullBcAuthorizeRequest()
        assertNotEquals(b, b.copy(loginHint = "o"))
        assertNotEquals(b, b.copy(scope = "o"))
        assertNotEquals(b, b.copy(accessType = "o"))
    }

    @Test
    fun `BcAuthorizeRequest copy without args equals original`() {
        val a = fullBcAuthorizeRequest()
        assertEquals(a, a.copy())
    }

    @Test
    fun `BcAuthorizeRequest destructuring returns components`() {
        val (h, s, t) = fullBcAuthorizeRequest()
        assertEquals("hint", h)
        assertEquals("profile", s)
        assertEquals("offline", t)
    }

    @Test
    fun `BcAuthorizeRequest toString contains class name`() {
        assertTrue(fullBcAuthorizeRequest().toString().contains("BcAuthorizeRequest"))
    }
}
