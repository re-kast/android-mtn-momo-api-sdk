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

import io.rekast.sdk.sample.utils.Constants
import io.rekast.sdk.utils.MomoApiConfig
import io.rekast.sdk.utils.Settings

/**
 * JVM entry point for the MTN MoMo API SDK demo.
 *
 * Demonstrates SDK configuration and utility usage without Android dependencies.
 * For full API usage on JVM, wire up [io.rekast.sdk.repository.DefaultRepository] with
 * a Retrofit instance built from [MomoApiConfig].
 *
 * Supply credentials via environment variables:
 *   MOMO_BASE_URL, MOMO_API_USER_ID, MOMO_ENVIRONMENT
 */
fun main() {
    println("=== MTN MoMo API SDK — JVM Demo ===")
    println()

    val config = MomoApiConfig(
        baseUrl = System.getenv("MOMO_BASE_URL") ?: "https://sandbox.momodeveloper.mtn.com/",
        apiUserId = System.getenv("MOMO_API_USER_ID") ?: "your-api-user-id",
        environment = System.getenv("MOMO_ENVIRONMENT") ?: "sandbox"
    )

    val settings = Settings()

    println("SDK Configuration:")
    println("  Base URL   : ${config.baseUrl}")
    println("  Environment: ${config.environment}")
    println("  API User ID: ${config.apiUserId}")
    println()
    println("Utilities:")
    println("  UUID #1    : ${settings.generateUUID()}")
    println("  UUID #2    : ${settings.generateUUID()}")
    println("  Phone (raw): 0733123456  →  formatted: ${settings.formatPhoneNumber("0733123456", "256")}")
    println()
    println("Constants (${Constants.SANDBOX_CURRENCY} / len=${Constants.STRING_LENGTH}):")
    println("  Sandbox currency   : ${Constants.SANDBOX_CURRENCY}")
    println("  Random string size : ${Constants.STRING_LENGTH}")
    println()
    println(
        "Navigation destinations: ${Constants.NavigationTitle.HOME}, " +
            "${Constants.NavigationTitle.REMITTANCE}, ${Constants.NavigationTitle.DISBURSEMENT_DEPOSIT}"
    )
    println()
    println("To make real API calls from JVM, create a Retrofit instance using MomoApiConfig.baseUrl,")
    println("add kotlinx.serialization converter, and inject dependencies into DefaultRepository manually.")
}
