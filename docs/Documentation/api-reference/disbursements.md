---
sidebar_position: 3
sidebar_label: Disbursements
---

# Library Usage — Disbursements

Disbursements APIs let you send money to Mobile Money accounts (transfers), credit accounts directly (deposits), and reverse transactions (refunds), plus query the status of each.

Authentication is handled automatically by the SDK's interceptors — you never pass an access token. Every `DefaultRepository` method returns a `Flow<NetworkResult<T>>`; collect it inside a coroutine scope. Cross-border cash transfers live under [Remittance](./remittance).

> **Environment and cross-cutting headers are automatic.** You set the target environment once during SDK setup — `ApiConfig.environment`, which is sourced from `MOMO_ENVIRONMENT` in `local.properties`. On every request the SDK's `EnvironmentInterceptor` adds the required `X-Target-Environment` header, so **environment is never a per-call parameter**. Authentication headers (`Authorization`) are attached the same way, and transaction-initiation endpoints additionally receive an optional `X-Callback-Url` header. See [Callbacks](./callbacks) for details.

---

## Transfer

Sends money from your account to a recipient's Mobile Money account.

```kotlin
defaultRepository.transfer(
    productType = ProductTypes.DISBURSEMENTS.productType,
    apiVersion = "v1_0",
    transfer = Transfer(
        amount = "250",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerMessage = "Salary payment",
        payeeNote = "March salary"
    ),
    uuid = UUID.randomUUID().toString(),
    productSubscriptionKey = disbursementsPrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* transfer initiated */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type       | Description                                                        |
|--------------------------|------------|--------------------------------------------------------------------|
| `productType`            | `String`   | Product type string, e.g. `ProductTypes.DISBURSEMENTS.productType` |
| `apiVersion`             | `String`   | API version, e.g. `"v1_0"`                                         |
| `transfer`               | `Transfer` | Transfer details (amount, currency, payee, messages)               |
| `uuid`                   | `String`   | Unique reference ID — save this to poll for status                 |
| `productSubscriptionKey` | `String`   | Disbursements primary subscription key                             |

---

## Get Transfer Status

Retrieves the status of a previously initiated transfer.

```kotlin
defaultRepository.getTransferStatus(
    productType = ProductTypes.DISBURSEMENTS.productType,
    apiVersion = "v1_0",
    referenceId = transferUuid,
    productSubscriptionKey = disbursementsPrimaryKey
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

Returns `Flow<NetworkResult<TransferStatus>>` — `TransferStatus` carries `amount`, `currency`, `financialTransactionId`, `externalId`, a `payee` (`Party`), `payerMessage`, `payeeNote`, a `status` of type `StatusTypes` (`PENDING`, `SUCCESSFUL`, `FAILED`), and a `reason` (`ErrorResponse` — `code` + `message`).

---

## Deposit

Credits a Mobile Money account directly (agent-initiated flow).

```kotlin
val transactionUuid = UUID.randomUUID().toString()

defaultRepository.deposit(
    deposit = Deposit(
        amount = "100",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
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
| `deposit`                | `Deposit`         | Deposit details                        |
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

Returns `Flow<NetworkResult<DepositStatus>>` — `DepositStatus` carries `amount`, `currency`, `financialTransactionId`, `externalId`, a `payee` (`Party`), `payerMessage`, `payeeNote`, a `status` of type `StatusTypes` (`PENDING`, `SUCCESSFUL`, `FAILED`), and a `reason` (`ErrorResponse` — `code` + `message`).

---

## Refund

Reverses a previously completed disbursements transaction.

```kotlin
val refundUuid = UUID.randomUUID().toString()

defaultRepository.refund(
    refund = Refund(
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
| `refund`                 | `Refund`          | Refund details; set `referenceIdToRefund` to the original transaction UUID |
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

Returns `Flow<NetworkResult<RefundStatus>>` — `RefundStatus` carries `amount`, `currency`, `financialTransactionId`, `externalId`, a `payee` (`Party`), `payerMessage`, `payeeNote`, a `status` of type `StatusTypes` (`PENDING`, `SUCCESSFUL`, `FAILED`), and a `reason` (`ErrorResponse` — `code` + `message`).

> **Cash transfers** — the V2 cross-border `cashTransfer` / `getCashTransferStatus` operations are documented under [Remittance](./remittance). **Withdrawal delivery notifications** — `requestToWithdrawDeliveryNotification` is documented under [Collection](./collection).
