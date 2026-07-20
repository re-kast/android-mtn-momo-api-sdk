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

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.rekast.sdk.app.BuildConfig
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.utils.ApiConfig
import javax.inject.Singleton

/**
 * Application-level Hilt module that provides the [ApiConfig] consumed by the SDK.
 *
 * Lives in the :app module (not :sample) because com.android.kotlin.multiplatform.library's
 * compile JAR does not include KSP-generated Java factory classes; moving @Module providers
 * here ensures hiltJavaCompileDebug can find them on the classpath.
 *
 * Credentials are read from [BuildConfig] which is populated by the Gradle Secrets plugin
 * from `local.properties` or CI environment variables.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides the [ApiConfig] that the SDK uses to configure the base URL, API user ID, and target environment.
     */
    @Provides
    @Singleton
    fun provideApiConfig(): ApiConfig =
        ApiConfig(
            baseUrl = BuildConfig.MOMO_BASE_URL,
            apiUserId = BuildConfig.MOMO_API_USER_ID,
            environment = BuildConfig.MOMO_ENVIRONMENT,
            apiVersion = BuildConfig.MOMO_API_VERSION_V1
        )

    /**
     * Provides the [SampleConfig] used by the sample app's ViewModels to supply
     * API versions, product subscription keys, and other runtime configuration values.
     */
    @Provides
    @Singleton
    fun provideSampleConfig(): SampleConfig =
        SampleConfig(
            apiVersionV1 = BuildConfig.MOMO_API_VERSION_V1,
            apiVersionV2 = BuildConfig.MOMO_API_VERSION_V2,
            environment = BuildConfig.MOMO_ENVIRONMENT,
            providerCallbackHost = BuildConfig.MOMO_PROVIDER_CALLBACK_HOST,
            apiUserId = BuildConfig.MOMO_API_USER_ID,
            collectionPrimaryKey = BuildConfig.MOMO_COLLECTION_PRIMARY_KEY,
            collectionSecondaryKey = BuildConfig.MOMO_COLLECTION_SECONDARY_KEY,
            remittancePrimaryKey = BuildConfig.MOMO_REMITTANCE_PRIMARY_KEY,
            remittanceSecondaryKey = BuildConfig.MOMO_REMITTANCE_SECONDARY_KEY,
            disbursementsPrimaryKey = BuildConfig.MOMO_DISBURSEMENTS_PRIMARY_KEY,
            disbursementsSecondaryKey = BuildConfig.MOMO_DISBURSEMENTS_SECONDARY_KEY
        )
}
