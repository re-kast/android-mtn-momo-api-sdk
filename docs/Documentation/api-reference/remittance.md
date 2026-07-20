---
sidebar_position: 4
sidebar_label: Remittance
---

# Library Usage — Remittance

Remittance APIs let you send international transfers to Mobile Money accounts and check their status. Two flows are available: the legacy V1 `transfer` and the V2 `cashTransfer`, which extends the payload with optional KYC fields about the sending party for cross-border compliance.

Authentication is handled automatically by the SDK's interceptors — you never pass an access token. Every `DefaultRepository` method returns a `Flow<NetworkResult<T>>`; collect it inside a coroutine scope.

---

## Transfer

Sends a cross-border remittance to a recipient's Mobile Money account.

```kotlin
defaultRepository.transfer(
    productType = ProductType.REMITTANCE.productType,
    apiVersion = "v1_0",
    momoTransaction = MomoTransaction(
        amount = "500",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payee = AccountHolder(partyIdType = "MSISDN", partyId = "256770000000"),
        payerMessage = "Remittance from abroad",
        payeeNote = "Family support"
    ),
    uuid = UUID.randomUUID().toString(),
    productSubscriptionKey = remittancePrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* transfer initiated */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type              | Description                                          |
|--------------------------|-------------------|------------------------------------------------------|
| `productType`            | `String`          | `ProductType.REMITTANCE.productType`                 |
| `apiVersion`             | `String`          | API version, e.g. `"v1_0"`                           |
| `momoTransaction`        | `MomoTransaction` | Transfer details (amount, currency, payee, messages) |
| `uuid`                   | `String`          | Unique reference ID — save this to poll for status   |
| `productSubscriptionKey` | `String`          | Remittance primary subscription key                  |
| `environment`            | `String`          | `"sandbox"` or `"production"`                        |

---

## Get Transfer Status

Retrieves the status of a previously initiated remittance transfer.

```kotlin
defaultRepository.getTransferStatus(
    productType = ProductType.REMITTANCE.productType,
    apiVersion = "v1_0",
    referenceId = transferUuid,
    productSubscriptionKey = remittancePrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* parse result.response */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                          |
|--------------------------|----------|--------------------------------------|
| `productType`            | `String` | `ProductType.REMITTANCE.productType` |
| `apiVersion`             | `String` | API version                          |
| `referenceId`            | `String` | UUID used when calling `transfer`    |
| `productSubscriptionKey` | `String` | Remittance primary subscription key  |
| `environment`            | `String` | `"sandbox"` or `"production"`        |

---

## Cash Transfer (V2)

Initiates a cross-border remittance via the V2 `cashtransfer` endpoint. Use this when the sending
party is not a registered MTN mobile money subscriber; the optional `payer*` KYC fields carry the
sender's identity for compliance. Poll `getCashTransferStatus` with the same `uuid` to check the outcome.

```kotlin
val cashTransferUuid = UUID.randomUUID().toString()

defaultRepository.cashTransfer(
    apiVersion = "v2_0",
    cashTransfer = CashTransfer(
        amount = "300",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payee = AccountHolder(partyIdType = "MSISDN", partyId = "256770000000"),
        payerMessage = "Cash transfer",
        payeeNote = "Family support",
        // Optional KYC fields describing the sending party:
        payerIdentity = "256770000001",
        payerFirstName = "Jane",
        payerSurName = "Doe",
        originatingCountry = "UG"
    ),
    uuid = cashTransferUuid,
    productSubscriptionKey = remittancePrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* accepted (HTTP 202) — poll for status with cashTransferUuid */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type           | Description                                                 |
|--------------------------|----------------|-------------------------------------------------------------|
| `apiVersion`             | `String`       | API version; use `"v2_0"` for this endpoint                 |
| `cashTransfer`           | `CashTransfer` | Recipient (`payee`), amounts, and optional payer KYC fields |
| `uuid`                   | `String`       | Unique reference ID — save this to poll for status          |
| `productSubscriptionKey` | `String`       | Remittance primary subscription key                         |
| `environment`            | `String`       | `"sandbox"` or `"production"`                               |

---

## Get Cash Transfer Status

Retrieves the status of a previously initiated cash transfer.

```kotlin
defaultRepository.getCashTransferStatus(
    apiVersion = "v2_0",
    referenceId = cashTransferUuid,
    productSubscriptionKey = remittancePrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* parse the status from result.response */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type     | Description                                 |
|--------------------------|----------|---------------------------------------------|
| `apiVersion`             | `String` | API version; use `"v2_0"` for this endpoint |
| `referenceId`            | `String` | UUID used when calling `cashTransfer`       |
| `productSubscriptionKey` | `String` | Remittance primary subscription key         |
| `environment`            | `String` | `"sandbox"` or `"production"`               |
