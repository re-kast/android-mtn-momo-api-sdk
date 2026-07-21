---
sidebar_position: 1
sidebar_label: Authentication
---

# Library Usage — Authentication

The authentication flow provisions all credentials needed before any product API can be called. Every step is exposed as a `Flow<NetworkResult<T>>` except where noted; collect the flow inside a coroutine scope and handle `Loading`, `Success`, and `Error` states.

The recommended sequence is:

1. [Check / Create API User](#1-check--create-api-user)
2. [Create API Key](#2-create-api-key)
3. [Get Access Token](#3-get-access-token)
4. [BC Authorize (CIBA)](#4-bc-authorize-ciba)
5. [Get OAuth2 Access Token](#5-get-oauth2-access-token)

---

## 1. Check / Create API User

### checkApiUser

Verifies that the sandbox API user exists.

```kotlin
defaultRepository.checkApiUser(
    apiVersion = "v1_0",
    productSubscriptionKey = collectionPrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* user exists */ }
        is NetworkResult.Error   -> { /* user not found — call createApiUser */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                                       |
|--------------------------|----------|---------------------------------------------------|
| `apiVersion`             | `String` | API version, e.g. `"v1_0"`                        |
| `productSubscriptionKey` | `String` | Collection or remittance primary subscription key |

---

### createApiUser

Creates the sandbox API user if it does not already exist.

```kotlin
val callbackHost = ProviderCallBackHost(providerCallbackHost = "localhost")

defaultRepository.createApiUser(
    providerCallBackHost = callbackHost,
    apiVersion = "v1_0",
    uuid = apiUserId,
    productSubscriptionKey = collectionPrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* user created */ }
        is NetworkResult.Error   -> { /* creation failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type                   | Description                                 |
|--------------------------|------------------------|---------------------------------------------|
| `providerCallBackHost`   | `ProviderCallBackHost` | Callback host for the provider              |
| `apiVersion`             | `String`               | API version, e.g. `"v1_0"`                  |
| `uuid`                   | `String`               | Unique reference ID used as the API user ID |
| `productSubscriptionKey` | `String`               | Collection primary subscription key         |

---

## 2. Create API Key

Generates a new API key for the current API user. Skip if a key is already stored — re-creating invalidates all previously issued tokens.

```kotlin
defaultRepository.createApiKey(
    apiVersion = "v1_0",
    productSubscriptionKey = remittancePrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            val apiKey = result.response?.apiKey.orEmpty()
            credentialStorage.saveApiKey(apiKey)
        }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                         |
|--------------------------|----------|-------------------------------------|
| `apiVersion`             | `String` | API version, e.g. `"v1_0"`          |
| `productSubscriptionKey` | `String` | Remittance primary subscription key |

---

## 3. Get Access Token

Exchanges the API key for a short-lived Bearer access token. Requires Basic Auth (API user ID + API key), which the SDK constructs automatically.

```kotlin
defaultRepository.getAccessToken(
    productSubscriptionKey = remittancePrimaryKey,
    productType = ProductTypes.REMITTANCE.productType
).collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            credentialStorage.saveAccessToken(result.response)
        }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                                                    |
|--------------------------|----------|----------------------------------------------------------------|
| `productSubscriptionKey` | `String` | Remittance primary subscription key                            |
| `productType`            | `String` | Product type string, e.g. `ProductTypes.REMITTANCE.productType` |

---

## 4. BC Authorize (CIBA)

Initiates a backchannel (CIBA) authorization request. The MTN MoMo platform sends an approval prompt to the subscriber's handset. The `auth_req_id` returned must be stored and exchanged for an OAuth2 token in the next step.

Requires a valid Bearer access token (step 3) in `CredentialStorage`.

```kotlin
val bcAuthorizeRequest = BcAuthorizeRequest(
    loginHint = "ID:563667/MSISDN",   // format: ID:{msisdn}/MSISDN
    scope = "profile openid",
    accessType = "offline"
)

defaultRepository.bcAuthorize(
    productType = ProductTypes.REMITTANCE.productType,
    apiVersion = "v1_0",
    bcAuthorizeRequest = bcAuthorizeRequest,
    productSubscriptionKey = remittancePrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            result.response?.let { response ->
                credentialStorage.saveBackChannelAuthorizationRequestId(
                    response.authReqId,
                    response.expiresIn
                )
                credentialStorage.saveLoginHint(bcAuthorizeRequest.loginHint)
            }
        }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type                 | Description                                                    |
|--------------------------|----------------------|----------------------------------------------------------------|
| `productType`            | `String`             | Product type string, e.g. `ProductTypes.REMITTANCE.productType` |
| `apiVersion`             | `String`             | API version, e.g. `"v1_0"`                                     |
| `bcAuthorizeRequest`     | `BcAuthorizeRequest` | Login hint, scope, and access type                             |
| `productSubscriptionKey` | `String`             | Remittance primary subscription key                            |
| `environment`            | `String`             | Target environment: `"sandbox"` or `"production"`              |

**`BcAuthorizeRequest` fields**

| Field        | Type     | Description                                                          |
|--------------|----------|----------------------------------------------------------------------|
| `loginHint`  | `String` | Subscriber identifier in the format `ID:{msisdn}/MSISDN`             |
| `scope`      | `String` | OAuth2 scope, e.g. `"profile openid"` or `"all_info"`                |
| `accessType` | `String` | `"online"` for short-lived or `"offline"` for refresh-capable tokens |

---

## 5. Get OAuth2 Access Token

Exchanges the stored `auth_req_id` for an OAuth2 access token. Requires a valid Bearer token (step 3) and a non-blank `auth_req_id` (step 4).

```kotlin
defaultRepository.getOauthAccessToken(
    productType = ProductTypes.REMITTANCE.productType,
    productSubscriptionKey = remittancePrimaryKey,
    environment = "sandbox",
    backChannelAuthorizationRequestId = storedAuthReqId
).collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            credentialStorage.saveOauthAccessToken(result.response)
        }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                           | Type     | Description                                                 |
|-------------------------------------|----------|-------------------------------------------------------------|
| `productType`                       | `String` | Product type string                                         |
| `productSubscriptionKey`            | `String` | Remittance primary subscription key                         |
| `environment`                       | `String` | Target environment: `"sandbox"` or `"production"`           |
| `backChannelAuthorizationRequestId` | `String` | `auth_req_id` returned by `bcAuthorize` — must not be blank |
