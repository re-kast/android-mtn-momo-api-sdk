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
package io.rekast.sdk.app.di

import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.utils.MomoApiConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for [AppModule].
 *
 * Because [AppModule] is a Dagger [dagger.Module] object, its `@Provides` methods are
 * plain functions that can be called directly in tests — no Hilt test component is needed.
 *
 * Tests verify:
 * - [AppModule.provideMomoApiConfig] returns a non-null [MomoApiConfig] with a non-empty base URL.
 * - [AppModule.provideSampleConfig] returns a non-null [SampleConfig] with non-empty key fields.
 */
class AppModuleTest {
    /**
     * Verifies that [AppModule.provideMomoApiConfig] returns a non-null [MomoApiConfig]
     * whose [MomoApiConfig.baseUrl] is non-empty (populated from [io.rekast.sdk.app.BuildConfig]).
     */
    @Test
    fun `provideMomoApiConfig returns non-null MomoApiConfig with non-empty baseUrl`() {
        val config: MomoApiConfig = AppModule.provideMomoApiConfig()
        assertNotNull(config)
        assertFalse("baseUrl should not be blank", config.baseUrl.isBlank())
    }

    /**
     * Verifies that [AppModule.provideMomoApiConfig] returns a [MomoApiConfig] with a
     * non-empty [MomoApiConfig.apiUserId].
     */
    @Test
    fun `provideMomoApiConfig returns config with non-empty apiUserId`() {
        val config: MomoApiConfig = AppModule.provideMomoApiConfig()
        assertFalse("apiUserId should not be blank", config.apiUserId.isBlank())
    }

    /**
     * Verifies that [AppModule.provideSampleConfig] returns a non-null [SampleConfig].
     */
    @Test
    fun `provideSampleConfig returns non-null SampleConfig`() {
        val config: SampleConfig = AppModule.provideSampleConfig()
        assertNotNull(config)
    }

    /**
     * Verifies that [AppModule.provideSampleConfig] returns a [SampleConfig] with non-empty
     * API version fields.
     */
    @Test
    fun `provideSampleConfig returns config with non-empty apiVersionV1`() {
        val config: SampleConfig = AppModule.provideSampleConfig()
        assertFalse("apiVersionV1 should not be blank", config.apiVersionV1.isBlank())
    }
}
