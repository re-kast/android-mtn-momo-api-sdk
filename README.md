# MTN MOMO API SDK for Android

[![Publish to Maven Central](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/publish.yml/badge.svg)](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/publish.yml) [![CI](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/ci.yml/badge.svg)](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/ci.yml) [![codecov](https://codecov.io/gh/re-kast/android-mtn-momo-api-sdk/graph/badge.svg)](https://codecov.io/gh/re-kast/android-mtn-momo-api-sdk) [![code style: spotless](https://img.shields.io/badge/code%20style-spotless-ff69b4.svg)](https://github.com/diffplug/spotless) [![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/B0B315PHS1)

## Overview

The MTN MOMO API SDK is a **Kotlin Multiplatform (KMP)** library designed to simplify the integration of MTN’s Mobile Money (MOMO) services into Android and JVM applications. The core SDK targets both Android and JVM platforms, while a full-featured Android sample app demonstrates every API operation. This SDK enables developers to seamlessly interact with MTN’s extensive mobile payment infrastructure, facilitating core functionalities such as secure user authentication, balance inquiries, and efficient transaction processing. By abstracting the complexities of the MOMO API, this library provides a reliable and secure bridge between Android applications and MTN’s financial services, allowing developers to focus on building exceptional user experiences.

For more information about the MTN MOMO API, please visit the official documentation [here](https://momodeveloper.mtn.com/).

## Key Features

The MTN MOMO API SDK offers a comprehensive suite of tools and functionalities that support a wide range of MOMO operations while adhering to modern Android development practices. Key features include:

### Core Functionalities

- **Easy Integration with the MTN MOMO API**: This SDK simplifies the process of connecting to the MOMO API, allowing developers to concentrate on building their applications without getting bogged down by the underlying complexities.
  
- **Support for a Range of MOMO Operations**:
  - **User Information Retrieval**: Effortlessly fetch and manage user data associated with MOMO accounts.
  - **Account Balance Checks**: Securely retrieve account balances through API calls, providing users with a transparent view of their MOMO accounts.
  - **Payment Requests and Processing**: Easily request and process payments via the MOMO API with minimal setup required.
  - **Disbursements and Refunds**: Streamline automated disbursements and manage refunds directly from the application.

For a complete overview of available operations, refer to the full MTN MOMO API documentation [here](https://momodeveloper.mtn.com/docs).

### Built with Modern Android Development Practices

- **Kotlin Multiplatform**: The core SDK (`momo-api-sdk`) is built with Kotlin Multiplatform, targeting Android and JVM. This makes the network, repository, and model layers reusable across platforms.
  
- **Coroutines for Asynchronous Operations**: Utilizes Kotlin Coroutines to handle asynchronous operations efficiently, ensuring non-blocking API interactions that enhance user experience.
  
- **Hilt for Dependency Injection**: Integrates with Hilt, a widely-used dependency injection framework, promoting clean and maintainable code architecture.
  
- **Jetpack Compose for UI (Sample App)**: Includes a sample application built with Jetpack Compose, the modern UI toolkit, to demonstrate effective integration and best practices for implementing MOMO services.

For further exploration, check out the documentation on [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html), [Hilt](https://developer.android.com/training/dependency-injection/hilt-android), and [Jetpack Compose](https://developer.android.com/jetpack/compose).

### Additional Benefits

- **Comprehensive Error Handling and Logging**: The SDK ships a KMP-safe `Logger` abstraction (`d`, `i`, `w`, `e`) backed by [Timber](https://github.com/JakeWharton/timber) on Android and standard output on JVM. All SDK internals use `Logger` so log output flows through whichever backend the host platform provides.
  
- **Secure API Communication**: Implements secure communication channels with proper authentication mechanisms, safeguarding all transactions and user data in compliance with industry standards.

This SDK empowers Android developers to integrate MTN MOMO services confidently, providing secure and efficient mobile payment solutions.

For detailed instructions on integrating and configuring the MTN MOMO API SDK, please consult the official [MTN MOMO API documentation](https://momodeveloper.mtn.com/).

## Authentication & Credential Management

The SDK uses a **pull-based credential model** — it never stores credentials internally. Instead, it calls your app's `CredentialProvider` implementation on every request to retrieve the current API user ID, API key, and access token.

### How It Works

```
┌─────────────────────────────────────────────────────────────┐
│                        Your App                             │
│                                                             │
│  CredentialStorage          CredentialProvider              │
│  (EncryptedSharedPrefs) ◄── (reads from storage)            │
│          ▲                          │                       │
│          │                          ▼                       │
│  MainViewModel           SDK Interceptors                │
│  (writes credentials)       BasicAuthInterceptor            │
│                             AccessTokenInterceptor          │
│                                     │                       │
│                             TokenAuthenticator              │
│                             (refreshes on 401)              │
└─────────────────────────────────────────────────────────────┘
```

### Credential Storage

Credentials are stored using `EncryptedSharedPreferences` (AES-256-GCM via the Android Keystore) through `CredentialStorage`. Tokens include expiry timestamps so that expired tokens are never returned — an expired token is treated the same as no token.

### Automatic Token Refresh

The `TokenAuthenticator` (an OkHttp `Authenticator`) fires automatically on every HTTP 401 response from a Bearer-protected endpoint:

1. Verifies the failed request was using Bearer auth.
2. Calls the MTN MoMo token endpoint via a dedicated `AuthenticationService` backed by a minimal, Basic-Auth-only `OkHttpClient` — this avoids a circular dependency with the main client.
3. Saves the refreshed Bearer token to `CredentialStorage`.
4. If the OAuth2 access token is also expired, refreshes it in the same pass and saves it to `CredentialStorage`. An OAuth2 refresh failure is non-fatal — the original request is still retried with the refreshed Bearer token.
5. Returns the original request so OkHttp re-runs the interceptors — `AccessTokenInterceptor` reads the new token from storage and attaches the correct `Authorization` header on the retry.

After at most **one retry**, the authenticator gives up and propagates the 401 to the caller.

### Implementing `CredentialProvider`

```kotlin
class MyCredentialProvider(
    private val storage: CredentialStorage,
    private val config: SampleConfig
) : CredentialProvider {

    override fun getApiUserId(): String = config.apiUserId

    // Return the API key only when no valid access token exists.
    // This prevents Basic Auth from being sent on Bearer-protected requests.
    override fun getApiKey(): String =
        if (storage.getAccessToken().isBlank()) storage.getApiKey() else ""

    override fun getAccessToken(): String = storage.getAccessToken()
}
```

Register it in your Hilt module:

```kotlin
@Provides
@Singleton
fun provideCredentialProvider(
    storage: CredentialStorage,
    config: SampleConfig
): CredentialProvider = MyCredentialProvider(storage, config)
```

### Credential Bootstrap

On first launch, `MainViewModel` runs a one-time sequence to provision credentials:

1. **Check API user** — if the user does not exist, create it.
2. **Create API key** — stored to `CredentialStorage`; skipped if a key already exists.
3. **Fetch access token** — stored with its expiry; skipped if a valid token is already present.
4. **Fetch OAuth2 token** — stored with its expiry; skipped if a valid token is already present.

Subsequent app launches skip any step where a valid, non-expired credential is already stored. Token expiry is checked automatically by `CredentialStorage` — no manual refresh calls are needed.

## Getting Started

### Installation

To include the MTN MOMO API SDK in your project, add the following dependency to your project's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.rekast:momo-api-sdk:0.1.0-SNAPSHOT")
}
```

### Sample `local.properties`

To configure your local environment for the MTN MOMO API SDK, create a `local.properties` file in the root of your project with the following content:

```properties
# Local properties for the MTN MOMO API SDK

MOMO_BASE_URL="" ## Use https://sandbox.momodeveloper.mtn.com for sandbox and https://momodeveloper.mtn.com for production
MOMO_PROVIDER_CALLBACK_HOST="" ## The provider callback host, use 'localhost' for sandbox
MOMO_COLLECTION_PRIMARY_KEY="" ## The collection endpoint/product subscription primary key
MOMO_COLLECTION_SECONDARY_KEY="" ## The collection endpoint/product subscription secondary key
MOMO_REMITTANCE_PRIMARY_KEY="" ## The remittance endpoint/product subscription primary key
MOMO_REMITTANCE_SECONDARY_KEY="" ## The remittance endpoint/product subscription secondary key
MOMO_DISBURSEMENTS_PRIMARY_KEY="" ## The disbursements endpoint/product subscription primary key
MOMO_DISBURSEMENTS_SECONDARY_KEY="" ## The disbursements endpoint/product subscription secondary key
MOMO_API_USER_ID="" ## The sandbox API user ID. You can use a [UUID generator](https://www.uuidgenerator.net/version4) to create one
MOMO_ENVIRONMENT="" ## API environment, use 'sandbox' for testing and 'production' for live operations
MOMO_API_VERSION_V1="" ## The API version for v1 endpoints, use 'v1_0' for sandbox and 'v1' for production
MOMO_API_VERSION_V2="" ## The API version for v2 endpoints, use 'v2_0' for sandbox and 'v2' for production
```

## License

This project is licensed under the Apache License, Version 2.0. For more details, please refer to the [LICENSE](LICENSE) file.

## Contact

For inquiries or support, please reach out to:

**Benjamin Mwalimu** [GitHub Profile](https://github.com/dubdabasoduba)

## Acknowledgments

- **MTN** for providing the MOMO API infrastructure.
- The **Android development community** for various open-source libraries utilized in this project.
- All **contributors** who have helped improve and maintain this SDK.
