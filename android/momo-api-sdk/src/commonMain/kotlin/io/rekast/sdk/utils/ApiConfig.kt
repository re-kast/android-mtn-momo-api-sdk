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
package io.rekast.sdk.utils

/**
 * Holds runtime configuration for the MTN MoMo SDK.
 *
 * Pass an instance of this class when initializing the SDK instead of relying
 * on Android-only [BuildConfig] fields, making the SDK usable on both Android
 * and plain-JVM targets.
 *
 * @property baseUrl The base URL for the MTN MoMo API (e.g. `https://sandbox.momodeveloper.mtn.com/`).
 * @property apiUserId The API user ID provisioned in the MoMo developer portal.
 * @property environment The deployment environment identifier (e.g. `sandbox` or `mtncongo`).
 */
data class ApiConfig(val baseUrl: String, val apiUserId: String, val environment: String)
