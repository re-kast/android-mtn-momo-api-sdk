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

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for [messageOrEmpty]. Exercises both the null and non-null branches of [Throwable.message].
 */
class ThrowableExtensionsTest {

    @Test
    fun `messageOrEmpty returns the message when present`() {
        assertEquals("boom", RuntimeException("boom").messageOrEmpty())
    }

    @Test
    fun `messageOrEmpty returns empty string when the throwable has no message`() {
        assertEquals("", RuntimeException().messageOrEmpty())
    }
}
