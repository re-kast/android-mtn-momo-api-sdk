---
sidebar_position: 1
sidebar_label: Overview
---

<head>
  <meta name="algolia-site-verification"  content="B7251C3239FB8992" />
</head>

# MTN MOMO API SDK for Android
---

[![Publish to Maven Central](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/publish.yml/badge.svg)](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/publish.yml) [![CI](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/ci.yml/badge.svg)](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/ci.yml) [![codecov](https://codecov.io/gh/re-kast/android-mtn-momo-api-sdk/graph/badge.svg)](https://codecov.io/gh/re-kast/android-mtn-momo-api-sdk) [![code style: spotless](https://img.shields.io/badge/code%20style-spotless-ff69b4.svg)](https://github.com/diffplug/spotless) [![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/B0B315PHS1)

## Overview

:::note[Unofficial SDK]
This is a community-maintained library and is **not affiliated with, endorsed by, or supported by MTN**. "MTN" and "MoMo" are trademarks of their respective owners. For issues with the MTN MoMo API itself, contact MTN via the [MoMo Developer Portal](https://momodeveloper.mtn.com/).
:::

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

The SDK uses a **pull-based credential model** — it never stores credentials internally. Instead, it calls your app's `CredentialProvider` implementation on every request to retrieve the current API user ID, API key, Bearer access token, and OAuth2 consent token.

### How It Works

```
┌─────────────────────────────────────────────────────────────┐
│                        Your App                             │
│                                                             │
│  CredentialStorage          CredentialProvider              │
│  (EncryptedSharedPrefs) ◄── (reads from storage)            │
│          ▲                          │                       │
│          │                          ▼                       │
│  MainViewModel           SDK Interceptors                   │
│  (writes credentials)       BasicAuthenticationInterceptor            │
│                             AccessTokenInterceptor          │
│                                     │                       │
│                             TokenAuthenticator              │
│                             (refreshes on 401)              │
└─────────────────────────────────────────────────────────────┘
```

### Credential Storage

Credentials are stored using `EncryptedSharedPreferences` (AES-256-GCM via the Android Keystore) through `CredentialStorage`. Tokens include expiry timestamps so that expired tokens are never returned — an expired token is treated the same as no token.

### Automatic Token Refresh

The `TokenAuthenticator` (an OkHttp `Authenticator`) fires automatically on every HTTP 401 response from a protected endpoint:

1. Verifies the failed request was using Bearer auth (OAuth2 consent endpoints are exempt — see below).
2. Calls the MTN MoMo token endpoint via a dedicated `AuthenticationService` backed by a minimal, Basic-Auth-only `OkHttpClient` — this avoids a circular dependency with the main client.
3. Saves the refreshed Bearer token to `CredentialStorage`.
4. If the OAuth2 access token is also expired, refreshes it in the same pass and saves it to `CredentialStorage`. An OAuth2 refresh failure is non-fatal — the original request is still retried with the refreshed Bearer token.
5. Returns the original request so OkHttp re-runs the interceptors — `AccessTokenInterceptor` reads the new token from storage and attaches the correct `Authorization` header on the retry.

After at most **one retry**, the authenticator gives up and propagates the 401 to the caller.

### OAuth2 Consent Endpoints

Two different Bearer tokens are in play. The regular **API-user access token** authenticates most endpoints. OAuth2 **consent resource** endpoints — those whose path contains an `oauth2` segment but not `token`, e.g. `/{productType}/oauth2/{apiVersion}/userinfo` — are instead authenticated with the **OAuth2 consent token** obtained through the CIBA flow. `AccessTokenInterceptor` routes the correct token per request automatically:

- **Consent resource endpoints** (userinfo) → the OAuth2 consent token from `CredentialProvider.getOauthAccessToken()`.
- **Everything else**, including the OAuth2 **token** endpoint (`/{productType}/oauth2/token/`) that mints the consent token → the regular Bearer token from `CredentialProvider.getAccessToken()`.

On a 401 from a consent endpoint, `TokenAuthenticator` refreshes the consent token (running bc-authorize if needed) and retries — independently of the regular Bearer-token refresh above.

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

    // The OAuth2 consent token authenticates OAuth2 resource endpoints (e.g. userinfo).
    // Defaults to "" in the interface, so override it only if you use consent-based APIs.
    override fun getOauthAccessToken(): String = storage.getOauthAccessToken()
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
    implementation("io.rekast:momo-api-sdk:1.0.0")
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
MOMO_DISBURSEMENTS_PRIMARY_KEY="" ## The disbursement endpoint/product subscription primary key
MOMO_DISBURSEMENTS_SECONDARY_KEY="" ## The disbursement endpoint/product subscription secondary key
MOMO_API_USER_ID="" ## The sandbox API user ID. You can use a [UUID generator](https://www.uuidgenerator.net/version4) to create one
MOMO_ENVIRONMENT="" ## API environment, use 'sandbox' for testing and 'production' for live operations
MOMO_API_VERSION_V1="" ## The API version for v1 endpoints, use 'v1_0' for sandbox and 'v1' for production
MOMO_API_VERSION_V2="" ## The API version for v2 endpoints, use 'v2_0' for sandbox and 'v2' for production
```

:::danger
**Important Note**: Ensure that all entries in the `local.properties` file are filled out correctly. The application will fail to compile if any required entries are missing. Double-check your configuration to avoid compilation errors. Read more about how to find the different keys [**here**](./engineering/getting-started/developer-setup)
:::

## Library Usage

The SDK exposes all operations through `DefaultRepository`. Every method returns a `Flow<NetworkResult<T>>` — collect it inside a coroutine scope and handle the three states:

```kotlin
defaultRepository.someApi(...).collect { result ->
    when (result) {
        is NetworkResult.Loading -> { /* show progress */ }
        is NetworkResult.Success -> { /* use result.response */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
    }
}
```

The available API groups are:

| Group                                                              | Description                                                                                        |
|--------------------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| [**Authentication**](./Documentation/api-reference/authentication) | Provision API user, API key, Bearer token, and OAuth2 token via the CIBA flow                      |
| [**Collection**](./Documentation/api-reference/collection)         | Request to Pay, Request to Withdraw, payments, invoices, pre-approvals, and delivery notifications |
| [**Disbursements**](./Documentation/api-reference/disbursements)   | Transfers, deposits, refunds, cash transfers, and delivery notifications                           |
| [**Remittance**](./Documentation/api-reference/remittance)         | Cross-border transfers and transfer status                                                         |
| [**Account**](./Documentation/api-reference/account)               | Account balance, basic user info, user info with consent, and account holder validation            |

Each page contains a working Kotlin code snippet followed by a parameter table. See the [Library Usage](./Documentation/api-reference) section in the sidebar for the full reference.

### Status responses

Status queries deserialize the response into a typed model (collected as `Flow<NetworkResult<T>>`):

- `requestToPayTransactionStatus`, `requestToWithdrawTransactionStatus`, `getTransferStatus`, `getDepositStatus`, `getRefundStatus`, and `getCashTransferStatus` return `MomoTransaction`.
- `validateAccountHolderStatus` returns `AccountHolderStatus`.
- `getApprovedPreApprovals` returns `ApprovedPreApprovals` (a `preApprovalDetails` list of `PreApprovalDetails`, with `status` a `StatusTypes` enum and `frequency` a `FrequencyType` enum).
- `getPreApprovalStatus` returns `PreApprovalStatus`.
- `getPaymentStatus` returns `PaymentStatus` (with a `StatusTypes` enum status).

## Security

Security is a first-class concern for a library that handles Mobile Money credentials and access tokens. Please review the [Security Policy](https://github.com/re-kast/android-mtn-momo-api-sdk/blob/develop/SECURITY.md) for the full details.

- **Reporting a vulnerability**: Report privately via GitHub's **["Report a vulnerability"](https://github.com/re-kast/android-mtn-momo-api-sdk/security)** button — never in a public issue, PR, or discussion. The [Security Policy](https://github.com/re-kast/android-mtn-momo-api-sdk/blob/develop/SECURITY.md) covers what to include and our response timelines.
- **Supported versions**: Security fixes ship on the latest release line only (currently `1.0.x`). Pin an explicit, non-`SNAPSHOT` version in production and upgrade promptly.
- **Automated scanning**: Every change is analysed with [CodeQL](https://github.com/re-kast/android-mtn-momo-api-sdk/blob/develop/.github/workflows/codeql.yml).

### Secure Usage Checklist

- **Never commit secrets** — keep `MOMO_*` subscription keys, the API user ID, and any keystore material out of version control (use `local.properties` or a secrets manager) and rotate anything that leaks.
- **Never ship `UnsafeOkHttpClient`** — it disables TLS certificate validation and exists solely for local sandbox testing; it must never appear in a release build or run against production endpoints.
- **Protect tokens at rest** — access and consent tokens are held via `EncryptedSharedPreferences`; never log tokens, subscription keys, or full request/response bodies in production.
- **Keep the SDK current** — security fixes land only on the latest release line, so update regularly.

## License

This project is licensed under the Apache License, Version 2.0. For more details, please refer to the [LICENSE](LICENSE) file.

## Contact

For inquiries or support, please reach out to:

**Benjamin Mwalimu** [GitHub Profile](https://github.com/dubdabasoduba)

Project Repository: [https://github.com/re-kast/android-mtn-momo-api-sdk](https://github.com/re-kast/android-mtn-momo-api-sdk)

## Acknowledgments

- **MTN** for providing the MOMO API infrastructure.
- The **Android development community** for various open-source libraries used in this project.
- All **contributors** who have helped improve and maintain this SDK.
