---
sidebar_position: 3
sidebar_label: Disbursements
---

# Library Usage — Disbursements

Disbursements APIs let you send money to Mobile Money accounts (transfers), credit accounts directly (deposits), and reverse transactions (refunds), plus query the status of each.

Authentication is handled automatically by the SDK's interceptors — you never pass an access token. Every `DefaultRepository` method returns a `Flow<NetworkResult<T>>`; collect it inside a coroutine scope. Cross-border cash transfers live under [Remittance](./remittance).

---

## Transfer

Sends money from your account to a recipient's Mobile Money account.

```kotlin
defaultRepository.transfer(
    productType = ProductType.DISBURSEMENTS.productType,
    apiVersion = "v1_0",
    momoTransaction = MomoTransaction(
        amount = "250",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payee = AccountHolder(partyIdType = "MSISDN", partyId = "256770000000"),
        payerMessage = "Salary payment",
        payeeNote = "March salary"
    ),
    uuid = UUID.randomUUID().toString(),
    productSubscriptionKey = disbursementsPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* transfer initiated */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type              | Description                                                      |
|--------------------------|-------------------|------------------------------------------------------------------|
| `productType`            | `String`          | Product type string, e.g. `ProductType.DISBURSEMENTS.productType` |
| `apiVersion`             | `String`          | API version, e.g. `"v1_0"`                                       |
| `momoTransaction`        | `MomoTransaction` | Transfer details (amount, currency, payee, messages)             |
| `uuid`                   | `String`          | Unique reference ID — save this to poll for status               |
| `productSubscriptionKey` | `String`          | Disbursements primary subscription key                           |
| `environment`            | `String`          | `"sandbox"` or `"production"`                                    |

---

## Get Transfer Status

Retrieves the status of a previously initiated transfer.

```kotlin
defaultRepository.getTransferStatus(
    productType = ProductType.DISBURSEMENTS.productType,
    apiVersion = "v1_0",
    referenceId = transferUuid,
    productSubscriptionKey = disbursementsPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* parse result.response */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                            |
|--------------------------|----------|----------------------------------------|
| `productType`            | `String` | Product type string                    |
| `apiVersion`             | `String` | API version                            |
| `referenceId`            | `String` | UUID used when calling `transfer`      |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `environment`            | `String` | `"sandbox"` or `"production"`          |

---

## Deposit

Credits a Mobile Money account directly (agent-initiated flow).

```kotlin
val transactionUuid = UUID.randomUUID().toString()

defaultRepository.deposit(
    momoTransaction = MomoTransaction(
        amount = "100",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payee = AccountHolder(partyIdType = "MSISDN", partyId = "256770000000"),
        payerMessage = "Cash deposit",
        payeeNote = "Deposit"
    ),
    apiVersion = "v1_0",
    productSubscriptionKey = disbursementsPrimaryKey,
    uuid = transactionUuid
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* accepted (HTTP 202) — poll for status with transactionUuid */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type              | Description                            |
|--------------------------|-------------------|----------------------------------------|
| `momoTransaction`        | `MomoTransaction` | Deposit details                        |
| `apiVersion`             | `String`          | API version, e.g. `"v1_0"`             |
| `productSubscriptionKey` | `String`          | Disbursements primary subscription key |
| `uuid`                   | `String`          | Unique reference ID                    |

---

## Get Deposit Status

Retrieves the status of a deposit.

```kotlin
defaultRepository.getDepositStatus(
    referenceId = transactionUuid,
    apiVersion = "v1_0",
    productSubscriptionKey = disbursementsPrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* parse the status from result.response */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type     | Description                            |
|--------------------------|----------|----------------------------------------|
| `referenceId`            | `String` | UUID used when calling `deposit`       |
| `apiVersion`             | `String` | API version                            |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |

---

## Refund

Reverses a previously completed disbursements transaction.

```kotlin
val refundUuid = UUID.randomUUID().toString()

defaultRepository.refund(
    momoTransaction = MomoTransaction(
        amount = "100",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payerMessage = "Refund for order #42",
        payeeNote = "Refund",
        referenceIdToRefund = originalTransactionUuid
    ),
    apiVersion = "v2_0",
    productSubscriptionKey = disbursementsPrimaryKey,
    uuid = refundUuid
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* accepted (HTTP 202) — poll for status with refundUuid */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type              | Description                                                                |
|--------------------------|-------------------|----------------------------------------------------------------------------|
| `momoTransaction`        | `MomoTransaction` | Refund details; set `referenceIdToRefund` to the original transaction UUID |
| `apiVersion`             | `String`          | API version, e.g. `"v2_0"`                                                 |
| `productSubscriptionKey` | `String`          | Disbursements primary subscription key                                     |
| `uuid`                   | `String`          | Unique reference ID for this refund                                        |

---

## Get Refund Status

Retrieves the status of a refund.

```kotlin
defaultRepository.getRefundStatus(
    referenceId = refundUuid,
    apiVersion = "v2_0",
    productSubscriptionKey = disbursementsPrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* parse the status from result.response */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type     | Description                            |
|--------------------------|----------|----------------------------------------|
| `referenceId`            | `String` | UUID used when calling `refund`        |
| `apiVersion`             | `String` | API version                            |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |

> **Cash transfers** — the V2 cross-border `cashTransfer` / `getCashTransferStatus` operations are documented under [Remittance](./remittance). **Withdrawal delivery notifications** — `requestToWithdrawDeliveryNotification` is documented under [Collection](./collection).
