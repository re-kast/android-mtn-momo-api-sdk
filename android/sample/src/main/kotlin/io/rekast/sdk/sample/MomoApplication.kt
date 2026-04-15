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
package io.rekast.sdk.sample

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * The main application class for the MTN MOMO SDK sample application.
 *
 * This class initializes the application and sets up dependency injection using Dagger Hilt.
 * It also configures Timber for logging in debug builds.
 */
@HiltAndroidApp
class MomoApplication : Application() {

    /**
     * Called when the application is starting, before any activity, service, or receiver
     * objects have been created. This is where you can perform application-wide initialization.
     */
    override fun onCreate() {
        super.onCreate()
        // Initialize Timber for logging in debug mode
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
