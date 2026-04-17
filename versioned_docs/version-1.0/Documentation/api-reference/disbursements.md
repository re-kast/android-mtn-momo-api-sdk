---
sidebar_position: 3
sidebar_label: Disbursements
---

# Library Usage — Disbursements

Disbursements APIs let you send money to Mobile Money accounts, query transfer status, issue cash transfers, and send delivery notifications.

All flows require a valid Bearer access token stored in `CredentialStorage`.

---

## Transfer

Sends money from your account to a recipient's Mobile Money account.

```kotlin
defaultRepository.transfer(
    productType = ProductType.DISBURSEMENT.productType,
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

| Parameter | Type | Description |
|---|---|---|
| `productType` | `String` | Product type string, e.g. `ProductType.DISBURSEMENT.productType` |
| `apiVersion` | `String` | API version, e.g. `"v1_0"` |
| `momoTransaction` | `MomoTransaction` | Transfer details (amount, currency, payee, messages) |
| `uuid` | `String` | Unique reference ID — save this to poll for status |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |

---

## Get Transfer Status

Retrieves the status of a previously initiated transfer.

```kotlin
defaultRepository.getTransferStatus(
    productType = ProductType.DISBURSEMENT.productType,
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

| Parameter | Type | Description |
|---|---|---|
| `productType` | `String` | Product type string |
| `apiVersion` | `String` | API version |
| `referenceId` | `String` | UUID used when calling `transfer` |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |

---

## Deposit

Credits a Mobile Money account directly (agent-initiated flow).

```kotlin
val transactionUuid = UUID.randomUUID().toString()

val result = defaultRepository.deposit(
    accessToken = credentialStorage.getAccessToken(),
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
)
// result is a Retrofit Response<Unit>; check result.isSuccessful
```

| Parameter | Type | Description |
|---|---|---|
| `accessToken` | `String` | Bearer access token |
| `momoTransaction` | `MomoTransaction` | Deposit details |
| `apiVersion` | `String` | API version, e.g. `"v1_0"` |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `uuid` | `String` | Unique reference ID |

---

## Get Deposit Status

Retrieves the status of a deposit.

```kotlin
val response = defaultRepository.getDepositStatus(
    referenceId = transactionUuid,
    apiVersion = "v1_0",
    productSubscriptionKey = disbursementsPrimaryKey,
    accessToken = credentialStorage.getAccessToken()
)
```

| Parameter | Type | Description |
|---|---|---|
| `referenceId` | `String` | UUID used when calling `deposit` |
| `apiVersion` | `String` | API version |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `accessToken` | `String` | Bearer access token |

---

## Refund

Reverses a previously completed disbursements transaction.

```kotlin
val refundUuid = UUID.randomUUID().toString()

val result = defaultRepository.refund(
    accessToken = credentialStorage.getAccessToken(),
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
)
```

| Parameter | Type | Description |
|---|---|---|
| `accessToken` | `String` | Bearer access token |
| `momoTransaction` | `MomoTransaction` | Refund details; set `referenceIdToRefund` to the original transaction UUID |
| `apiVersion` | `String` | API version, e.g. `"v2_0"` |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `uuid` | `String` | Unique reference ID for this refund |

---

## Get Refund Status

Retrieves the status of a refund.

```kotlin
val response = defaultRepository.getRefundStatus(
    referenceId = refundUuid,
    apiVersion = "v2_0",
    productSubscriptionKey = disbursementsPrimaryKey,
    accessToken = credentialStorage.getAccessToken()
)
```

| Parameter | Type | Description |
|---|---|---|
| `referenceId` | `String` | UUID used when calling `refund` |
| `apiVersion` | `String` | API version |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `accessToken` | `String` | Bearer access token |

---

## Cash Transfer

Initiates a cash transfer (over-the-counter disbursement).

```kotlin
defaultRepository.cashTransfer(
    apiVersion = "v1_0",
    cashTransfer = CashTransfer(
        amount = "300",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payerIdentity = "256770000000",
        payeeIdentity = "256770000001",
        payerMessage = "Cash transfer",
        payeeNote = "Transfer"
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

| Parameter | Type | Description |
|---|---|---|
| `apiVersion` | `String` | API version |
| `cashTransfer` | `CashTransfer` | Transfer details including payer and payee identities |
| `uuid` | `String` | Unique reference ID |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |

---

## Get Cash Transfer Status

Retrieves the status of a cash transfer.

```kotlin
defaultRepository.getCashTransferStatus(
    apiVersion = "v1_0",
    referenceId = cashTransferUuid,
    productSubscriptionKey = disbursementsPrimaryKey,
    environment = "sandbox"
).collect { result -> /* ... */ }
```

| Parameter | Type | Description |
|---|---|---|
| `apiVersion` | `String` | API version |
| `referenceId` | `String` | UUID used when calling `cashTransfer` |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |

---

## Request to Withdraw Delivery Notification

Sends a delivery notification after a withdrawal has been processed.

```kotlin
defaultRepository.requestToWithdrawDeliveryNotification(
    apiVersion = "v1_0",
    referenceId = withdrawalUuid,
    momoNotification = MomoNotification(notificationMessage = "Your withdrawal has been processed."),
    productSubscriptionKey = disbursementsPrimaryKey,
    environment = "sandbox"
).collect { result -> /* ... */ }
```

| Parameter | Type | Description |
|---|---|---|
| `apiVersion` | `String` | API version |
| `referenceId` | `String` | UUID of the original withdrawal |
| `momoNotification` | `MomoNotification` | Notification message body |
| `productSubscriptionKey` | `String` | Disbursements primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |
