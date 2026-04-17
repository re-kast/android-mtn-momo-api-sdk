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
package io.rekast.sdk.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.rekast.sdk.sample.utils.DefaultDispatcherProvider
import io.rekast.sdk.sample.utils.DispatcherProvider

/**
 * Hilt module that binds the [DefaultDispatcherProvider] implementation to the [DispatcherProvider]
 * interface for the singleton component scope.
 *
 * Lives in the :app module (not :sample) because com.android.kotlin.multiplatform.library's
 * compile JAR does not include KSP-generated Java factory classes needed by hiltJavaCompileDebug.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DispatchersModule {
    /**
     * Binds [DefaultDispatcherProvider] as the application-wide [DispatcherProvider] implementation.
     */
    @Binds
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
