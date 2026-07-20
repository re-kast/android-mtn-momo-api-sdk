---
sidebar_position: 5
sidebar_label: Account
---

# Library Usage — Account

Account APIs retrieve subscriber information and validate account status. They are shared across all product types (Collection, Disbursements, Remittance) — pass the appropriate `productType` and `productSubscriptionKey` for the product you are using.

---

## Get Account Balance

Returns the current balance of the product wallet.

```kotlin
defaultRepository.getAccountBalance(
    productType = ProductType.COLLECTION.productType,
    apiVersion = "v1_0",
    currency = null,           // null returns balance in the account's default currency
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            val balance = result.response
            // balance.availableBalance, balance.currency
        }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

Pass a specific ISO-4217 currency code to `currency` to retrieve the balance in that currency:

```kotlin
defaultRepository.getAccountBalance(
    productType = ProductType.REMITTANCE.productType,
    apiVersion = "v1_0",
    currency = "EUR",
    productSubscriptionKey = remittancePrimaryKey,
    environment = "sandbox"
).collect { result -> /* ... */ }
```

| Parameter                | Type      | Description                                                |
|--------------------------|-----------|------------------------------------------------------------|
| `productType`            | `String`  | Product type string                                        |
| `apiVersion`             | `String`  | API version, e.g. `"v1_0"`                                 |
| `currency`               | `String?` | ISO-4217 currency code, or `null` for the default currency |
| `productSubscriptionKey` | `String`  | Primary subscription key for the product                   |
| `environment`            | `String`  | `"sandbox"` or `"production"`                              |

---

## Get Basic User Info

Retrieves the name and other non-sensitive profile fields for a given account holder without requiring the subscriber's consent.

```kotlin
defaultRepository.getBasicUserInfo(
    productType = ProductType.COLLECTION.productType,
    apiVersion = "v1_0",
    accountHolder = "256770000000",   // MSISDN
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            val info = result.response
            // info.name, info.givenName, info.familyName, etc.
        }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                   |
|--------------------------|----------|-------------------------------|
| `productType`            | `String` | Product type string           |
| `apiVersion`             | `String` | API version                   |
| `accountHolder`          | `String` | MSISDN of the account holder  |
| `productSubscriptionKey` | `String` | Primary subscription key      |
| `environment`            | `String` | `"sandbox"` or `"production"` |

---

## Get User Info With Consent

Retrieves full profile information for the authenticated subscriber. This is an OAuth2 **consent resource** endpoint (`/{productType}/oauth2/{apiVersion}/userinfo`): it requires a valid OAuth2 consent token (the subscriber must have approved via the CIBA flow). The SDK attaches that consent token automatically — see [Authentication](./authentication) for how the consent token is provisioned.

```kotlin
defaultRepository.getUserInfoWithConsent(
    productType = ProductType.REMITTANCE.productType,
    apiVersion = "v1_0",
    productSubscriptionKey = remittancePrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            val info = result.response
            // info.name, info.email, info.phonenumber, info.address?.country, info.creditScore, ...
        }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                                                                                                                  |
|--------------------------|----------|------------------------------------------------------------------------------------------------------------------------------|
| `productType`            | `String` | Product type string. Use the product whose subscription key you have provisioned (e.g. `ProductType.REMITTANCE.productType`) |
| `apiVersion`             | `String` | API version                                                                                                                  |
| `productSubscriptionKey` | `String` | Primary subscription key                                                                                                     |
| `environment`            | `String` | `"sandbox"` or `"production"`                                                                                                |

**`UserInfoWithConsent` response fields**

Only `sub` and `name` are always present; every other field is optional (nullable) and is simply omitted when the API does not return it.

| Field                                              | Type       | Description                                                                                         |
|----------------------------------------------------|------------|-----------------------------------------------------------------------------------------------------|
| `sub`                                              | `String`   | Subject identifier for the user                                                                     |
| `name`                                             | `String`   | Full name                                                                                           |
| `givenName` / `familyName` / `middleName`          | `String?`  | Name parts                                                                                          |
| `birthDate`                                        | `String?`  | Date of birth                                                                                       |
| `gender`                                           | `String?`  | Gender                                                                                              |
| `locale`                                           | `String?`  | Locale, e.g. `sv_SE`                                                                                |
| `email`                                            | `String?`  | Email address                                                                                       |
| `emailVerified`                                    | `Boolean?` | Whether the email is verified                                                                       |
| `phonenumber`                                      | `String?`  | Phone number (`phone_number`)                                                                       |
| `phoneNumberVerified`                              | `Boolean?` | Whether the phone number is verified                                                                |
| `address`                                          | `Address?` | Nested address object (`formatted`, `streetAddress`, `postalCode`, `locality`, `region`, `country`) |
| `creditScore`                                      | `Int?`     | Credit score                                                                                        |
| `active`                                           | `Boolean?` | Whether the account is active                                                                       |
| `countryOfBirth` / `regionOfBirth` / `cityOfBirth` | `String?`  | Birthplace                                                                                          |
| `occupation` / `employerName`                      | `String?`  | Employment details                                                                                  |
| `identificationType` / `identificationValue`       | `String?`  | Identification document type and number                                                             |

---

## Validate Account Holder Status

Checks whether a given account holder is registered and active on the MTN MoMo platform.

```kotlin
defaultRepository.validateAccountHolderStatus(
    productType = ProductType.COLLECTION.productType,
    apiVersion = "v1_0",
    accountHolder = AccountHolder(partyIdType = "MSISDN", partyId = "256770000000"),
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* account is active */ }
        is NetworkResult.Error   -> { /* account not found or inactive */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type            | Description                                                |
|--------------------------|-----------------|------------------------------------------------------------|
| `productType`            | `String`        | Product type string                                        |
| `apiVersion`             | `String`        | API version                                                |
| `accountHolder`          | `AccountHolder` | Account identifier — `partyIdType` is typically `"MSISDN"` |
| `productSubscriptionKey` | `String`        | Primary subscription key                                   |
| `environment`            | `String`        | `"sandbox"` or `"production"`                              |
