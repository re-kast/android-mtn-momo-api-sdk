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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests for the [LiveData] string extensions.
 *
 * Each extension guards a nullable [LiveData.value]. An unset [MutableLiveData] genuinely reports
 * a `null` value, so these tests exercise both the null and non-null branches directly.
 */
class LiveDataExtensionsTest {

    @Test
    fun `valueOrEmpty returns the value when set`() {
        val liveData: LiveData<String> = MutableLiveData("hello")
        assertEquals("hello", liveData.valueOrEmpty())
    }

    @Test
    fun `valueOrEmpty returns empty string when value is unset`() {
        val liveData: LiveData<String> = MutableLiveData()
        assertEquals("", liveData.valueOrEmpty())
    }

    @Test
    fun `valueOrNullIfBlank returns the value when it is non-blank`() {
        val liveData: LiveData<String> = MutableLiveData("FIN-123")
        assertEquals("FIN-123", liveData.valueOrNullIfBlank())
    }

    @Test
    fun `valueOrNullIfBlank returns null when the value is blank`() {
        val liveData: LiveData<String> = MutableLiveData("")
        assertNull(liveData.valueOrNullIfBlank())
    }

    @Test
    fun `valueOrNullIfBlank returns null when the value is unset`() {
        val liveData: LiveData<String> = MutableLiveData()
        assertNull(liveData.valueOrNullIfBlank())
    }
}
