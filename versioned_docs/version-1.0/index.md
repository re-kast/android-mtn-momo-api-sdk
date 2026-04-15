---
sidebar_position: 1
sidebar_label: Overview
---

<head>
  <meta name="algolia-site-verification"  content="B7251C3239FB8992" />
</head>

# MTN MOMO API SDK for Android
---

[![Publish to Maven Central](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/publish.yml/badge.svg)](https://github.com/re-kast/android-mtn-momo-api-sdk/actions/workflows/publish.yml) [![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/B0B315PHS1)

## Overview

The MTN MOMO API SDK is a powerful and versatile Android library designed to simplify the integration of MTN’s Mobile Money (MOMO) services into native Android applications. This SDK enables developers to seamlessly interact with MTN’s extensive mobile payment infrastructure, facilitating core functionalities such as secure user authentication, balance inquiries, and efficient transaction processing. By abstracting the complexities of the MOMO API, this library provides a reliable and secure bridge between Android applications and MTN’s financial services, allowing developers to focus on building exceptional user experiences.

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

- **Kotlin**: Developed using Kotlin, the preferred language for Android development, ensuring compatibility with contemporary Android codebases.
  
- **Coroutines for Asynchronous Operations**: Utilizes Kotlin Coroutines to handle asynchronous operations efficiently, ensuring non-blocking API interactions that enhance user experience.
  
- **Hilt for Dependency Injection**: Integrates with Hilt, a widely-used dependency injection framework, promoting clean and maintainable code architecture.
  
- **Jetpack Compose for UI (Sample App)**: Includes a sample application built with Jetpack Compose, the modern UI toolkit, to demonstrate effective integration and best practices for implementing MOMO services.

For further exploration, check out the documentation on [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html), [Hilt](https://developer.android.com/training/dependency-injection/hilt-android), and [Jetpack Compose](https://developer.android.com/jetpack/compose).

### Additional Benefits

- **Comprehensive Error Handling and Logging**: The SDK comes equipped with built-in error handling and logging tools, enabling developers to track and resolve issues effectively, ensuring smooth functionality in production environments.
  
- **Secure API Communication**: Implements secure communication channels with proper authentication mechanisms, safeguarding all transactions and user data in compliance with industry standards.

This SDK empowers Android developers to integrate MTN MOMO services confidently, providing secure and efficient mobile payment solutions.

For detailed instructions on integrating and configuring the MTN MOMO API SDK, please consult the official [MTN MOMO API documentation](https://momodeveloper.mtn.com/).

## Getting Started

### Installation

To include the MTN MOMO API SDK in your project, add the following dependency to your project's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.rekast:momo-api-sdk:0.0.2-SNAPSHOT")
}
```

### Sample `local.properties`

To configure your local environment for the MTN MOMO API SDK, create a `local.properties` file in the root of your project with the following content:

```properties
# Local properties for the MTN MOMO API SDK

MOMO_BASE_URL="" ## Use https://sandbox.momodeveloper.mtn.com for sandbox and https://momodeveloper.mtn.com for production
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

:::danger
**Important Note**: Ensure that all entries in the `local.properties` file are filled out correctly. The application will fail to compile if any required entries are missing. Double-check your configuration to avoid compilation errors. Read more about how to find the diffrent keys [**here**](./engineering/getting-started/developer-setup)
:::

## License

This project is licensed under the Apache License, Version 2.0. For more details, please refer to the [LICENSE](LICENSE) file.

## Contact

For inquiries or support, please reach out to:

**Benjamin Mwalimu** [GitHub Profile](https://github.com/dubdabasoduba)

Project Repository: [https://github.com/re-kast/android-mtn-momo-api-sdk](https://github.com/re-kast/android-mtn-momo-api-sdk)

## Acknowledgments

- **MTN** for providing the MOMO API infrastructure.
- The **Android development community** for various open-source libraries utilized in this project.
- All **contributors** who have helped improve and maintain this SDK.
