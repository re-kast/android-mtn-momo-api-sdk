# MTN MOMO API SDK for Android

[![Create staging repository](https://github.com/dubdabasoduba/android-mtn-momo-api/actions/workflows/main.yml/badge.svg?branch=develop)](https://github.com/dubdabasoduba/android-mtn-momo-api/actions/workflows/main.yml)

## Overview

The MTN MOMO API SDK is an Android library designed to simplify the integration of MTN's Mobile Money (MOMO) services into native Android applications. This SDK provides developers with a seamless way to interact with MTN's mobile payment infrastructure, enabling features such as user authentication, balance inquiries, and transaction processing.

## Key Features

- Easy integration with MTN MOMO API
- Support for various MOMO operations:
  - User information retrieval
  - Account balance checks
  - Payment requests and processing
  - Disbursements and refunds
- Built with modern Android development practices:
  - Kotlin language
  - Coroutines for asynchronous operations
  - Hilt for dependency injection
  - Jetpack Compose for UI (in sample app)
- Comprehensive error handling and logging
- Secure API communication with proper authentication

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Kotlin 1.9.10 or later
- Minimum SDK version 24
- Compile SDK version 34

### Installation

Add the following to your project's `build.gradle.kts` file:

```
dependencies {
    implementation("io.rekast:momo-api-sdk:0.0.2-SNAPSHOT")
}
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.

## Contact

Benjamin Mwalimu Mulyungi - [GitHub](https://github.com/dubdabasoduba)

Project Link: [https://github.com/dubdabasoduba/android-mtn-momo-api-sdk](https://github.com/dubdabasoduba/android-mtn-momo-api-sdk)

## Acknowledgments

- MTN for providing the MOMO API infrastructure
- The Android development community for various open-source libraries used in this project
- Contributors who have helped improve and maintain this SDK
