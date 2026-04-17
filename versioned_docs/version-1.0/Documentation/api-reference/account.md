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

| Parameter | Type | Description |
|---|---|---|
| `productType` | `String` | Product type string |
| `apiVersion` | `String` | API version, e.g. `"v1_0"` |
| `currency` | `String?` | ISO-4217 currency code, or `null` for the default currency |
| `productSubscriptionKey` | `String` | Primary subscription key for the product |
| `environment` | `String` | `"sandbox"` or `"production"` |

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

| Parameter | Type | Description |
|---|---|---|
| `productType` | `String` | Product type string |
| `apiVersion` | `String` | API version |
| `accountHolder` | `String` | MSISDN of the account holder |
| `productSubscriptionKey` | `String` | Primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |

---

## Get User Info With Consent

Retrieves full profile information for the authenticated subscriber. Requires a valid OAuth2 access token (the subscriber must have approved via the CIBA flow).

```kotlin
defaultRepository.getUserInfoWithConsent(
    productType = ProductType.COLLECTION.productType,
    apiVersion = "v1_0",
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> {
            val info = result.response
            // info.sub, info.name, info.phoneNumber, info.email, etc.
        }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter | Type | Description |
|---|---|---|
| `productType` | `String` | Product type string |
| `apiVersion` | `String` | API version |
| `productSubscriptionKey` | `String` | Primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |

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

| Parameter | Type | Description |
|---|---|---|
| `productType` | `String` | Product type string |
| `apiVersion` | `String` | API version |
| `accountHolder` | `AccountHolder` | Account identifier — `partyIdType` is typically `"MSISDN"` |
| `productSubscriptionKey` | `String` | Primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |
