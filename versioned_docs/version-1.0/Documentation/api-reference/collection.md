---
sidebar_position: 2
sidebar_label: Collection
---

# Library Usage — Collection

Collection APIs let you request payments from customers, check transaction status, send delivery notifications, manage invoices, and handle pre-approvals.

All flows require a valid Bearer access token and OAuth2 token stored in `CredentialStorage`. The SDK's `TokenAuthenticator` refreshes expired tokens automatically on 401 responses.

---

## Request to Pay

Initiates a debit request against a customer's Mobile Money account.

```kotlin
val transactionUuid = UUID.randomUUID().toString()

defaultRepository.requestToPay(
    requestToPay = RequestToPay(
        amount = "500",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payee = null,
        payerMessage = "Payment for order #42",
        payeeNote = "Order #42"
    ),
    apiVersion = "v1_0",
    productSubscriptionKey = collectionPrimaryKey,
    uuid = transactionUuid
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* accepted (HTTP 202) — poll for status with transactionUuid */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

Authentication is handled automatically by the SDK's interceptors — you never pass an access token. Every `DefaultRepository` method returns a `Flow<NetworkResult<T>>`; collect it inside a coroutine scope.

| Parameter                | Type              | Description                                         |
|--------------------------|-------------------|-----------------------------------------------------|
| `requestToPay`           | `RequestToPay`    | Payment details (amount, currency, payer, messages) |
| `apiVersion`             | `String`          | API version, e.g. `"v1_0"`                          |
| `productSubscriptionKey` | `String`          | Collection primary subscription key                 |
| `uuid`                   | `String`          | Unique reference ID — save this to poll for status  |

---

## Request to Pay — Transaction Status

Polls the status of a previously initiated `requestToPay` call.

```kotlin
defaultRepository.requestToPayTransactionStatus(
    referenceId = transactionUuid,
    apiVersion = "v1_0",
    productSubscriptionKey = collectionPrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* parse the RequestToPayStatus from result.response */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type     | Description                           |
|--------------------------|----------|---------------------------------------|
| `referenceId`            | `String` | UUID used when calling `requestToPay` |
| `apiVersion`             | `String` | API version, e.g. `"v1_0"`            |
| `productSubscriptionKey` | `String` | Collection primary subscription key   |

Returns `Flow<NetworkResult<RequestToPayStatus>>` — `RequestToPayStatus` carries `amount`, `currency`, `financialTransactionId`, `externalId`, a `payer` (`Party`), `payerMessage`, `payeeNote`, a `status` of type `StatusTypes` (`PENDING`, `SUCCESSFUL`, `FAILED`), and a `reason` (`ErrorResponse` — `code` + `message`).

---

## Request to Withdraw

Initiates a credit request to a customer's Mobile Money account.

```kotlin
val transactionUuid = UUID.randomUUID().toString()

defaultRepository.requestToWithdraw(
    requestToWithdraw = RequestToWithdraw(
        amount = "200",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payee = null,
        payerMessage = "Withdrawal request",
        payeeNote = "Withdrawal"
    ),
    apiVersion = "v2_0",
    productSubscriptionKey = collectionPrimaryKey,
    uuid = transactionUuid
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* accepted (HTTP 202) — poll for status with transactionUuid */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type                | Description                         |
|--------------------------|---------------------|-------------------------------------|
| `requestToWithdraw`      | `RequestToWithdraw` | Transaction details                 |
| `apiVersion`             | `String`            | API version, e.g. `"v2_0"`          |
| `productSubscriptionKey` | `String`            | Collection primary subscription key |
| `uuid`                   | `String`            | Unique reference ID                 |

---

## Request to Withdraw — Transaction Status

```kotlin
defaultRepository.requestToWithdrawTransactionStatus(
    referenceId = transactionUuid,
    apiVersion = "v2_0",
    productSubscriptionKey = collectionPrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* parse the RequestToWithdrawStatus from result.response */ }
        is NetworkResult.Error   -> { /* handle result.message */ }
        is NetworkResult.Loading -> { /* show progress */ }
    }
}
```

| Parameter                | Type     | Description                                |
|--------------------------|----------|--------------------------------------------|
| `referenceId`            | `String` | UUID used when calling `requestToWithdraw` |
| `apiVersion`             | `String` | API version, e.g. `"v2_0"`                 |
| `productSubscriptionKey` | `String` | Collection primary subscription key        |

Returns `Flow<NetworkResult<RequestToWithdrawStatus>>` — `RequestToWithdrawStatus` carries `amount`, `currency`, `financialTransactionId`, `externalId`, a `payer` (`Party`), `payerMessage`, `payeeNote`, a `status` of type `StatusTypes` (`PENDING`, `SUCCESSFUL`, `FAILED`), and a `reason` (`ErrorResponse` — `code` + `message`).

---

## Request to Pay Delivery Notification

Sends a delivery notification to the payer after a successful `requestToPay`.

```kotlin
defaultRepository.requestToPayDeliveryNotification(
    productType = ProductTypes.COLLECTION.productType,
    apiVersion = "v1_0",
    referenceId = transactionUuid,
    notifications = Notifications(notificationMessage = "Your payment was received."),
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* notification sent */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type            | Description                                                                     |
|--------------------------|-----------------|---------------------------------------------------------------------------------|
| `productType`            | `String`        | Product initiating the notification, e.g. `ProductTypes.COLLECTION.productType` |
| `apiVersion`             | `String`        | API version, e.g. `"v1_0"`                                                      |
| `referenceId`            | `String`        | UUID of the original `requestToPay`                                             |
| `notifications`          | `Notifications` | Notification message body (`notificationMessage`)                               |
| `productSubscriptionKey` | `String`        | Collection primary subscription key                                             |
| `environment`            | `String`        | `"sandbox"` or `"production"`                                                   |

---

## Request to Withdraw Delivery Notification

Sends a delivery notification to the payer after a successful `requestToWithdraw`.

```kotlin
defaultRepository.requestToWithdrawDeliveryNotification(
    apiVersion = "v1_0",
    referenceId = transactionUuid,
    notifications = Notifications(notificationMessage = "Your withdrawal was processed."),
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* notification sent */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type            | Description                              |
|--------------------------|-----------------|------------------------------------------|
| `apiVersion`             | `String`        | API version, e.g. `"v1_0"`               |
| `referenceId`            | `String`        | UUID of the original `requestToWithdraw` |
| `notifications`          | `Notifications` | Notification message body (`notificationMessage`) |
| `productSubscriptionKey` | `String`        | Collection primary subscription key      |
| `environment`            | `String`        | `"sandbox"` or `"production"`            |

---

## Create Invoice

Creates a payment invoice that a customer can pay via the USSD menu or MoMo app.

```kotlin
defaultRepository.createInvoice(
    apiVersion = "v1_0",
    invoice = Invoice(
        externalId = UUID.randomUUID().toString(),
        amount = "1000",
        currency = "EUR",
        validityDuration = "3600",
        intendedPayer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000001"),
        description = "Invoice for service #99"
    ),
    uuid = UUID.randomUUID().toString(),
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* invoice created */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type      | Description                                                  |
|--------------------------|-----------|--------------------------------------------------------------|
| `apiVersion`             | `String`  | API version, e.g. `"v1_0"`                                   |
| `invoice`                | `Invoice` | Invoice details including amount, currency, payer, and payee |
| `uuid`                   | `String`  | Unique reference ID for the invoice                          |
| `productSubscriptionKey` | `String`  | Collection primary subscription key                          |
| `environment`            | `String`  | `"sandbox"` or `"production"`                                |

---

## Get Invoice Status

Retrieves the current status of an invoice.

```kotlin
defaultRepository.getInvoiceStatus(
    apiVersion = "v1_0",
    referenceId = invoiceUuid,
    productSubscriptionKey = collectionPrimaryKey,
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
| `apiVersion`             | `String` | API version                            |
| `referenceId`            | `String` | UUID used when calling `createInvoice` |
| `productSubscriptionKey` | `String` | Collection primary subscription key    |
| `environment`            | `String` | `"sandbox"` or `"production"`          |

Returns `Flow<NetworkResult<InvoiceStatus>>` — `InvoiceStatus` carries `referenceId`, `externalId`, `amount`, `currency`, a `status` of type `StatusTypes` (`CREATED`, `PENDING`, `SUCCESSFUL`, `FAILED`), `paymentReference`, `invoiceId`, `expiryDateTime`, `payeeFirstName`, `payeeLastName`, an `errorReason` (`ErrorResponse` — `code` + `message`), an `intendedPayer` (`Party`), and `description`.

---

## Cancel Invoice

Cancels an active invoice.

```kotlin
defaultRepository.cancelInvoice(
    apiVersion = "v1_0",
    referenceId = invoiceUuid,
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* invoice cancelled */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                         |
|--------------------------|----------|-------------------------------------|
| `apiVersion`             | `String` | API version                         |
| `referenceId`            | `String` | UUID of the invoice to cancel       |
| `productSubscriptionKey` | `String` | Collection primary subscription key |
| `environment`            | `String` | `"sandbox"` or `"production"`       |

---

## Create Pre-Approval

Creates a pre-approval that allows future debits without further subscriber interaction.

```kotlin
defaultRepository.createPreApproval(
    apiVersion = "v1_0",
    preApproval = PreApproval(
        payer = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerCurrency = "EUR",
        payerMessage = "Authorize monthly subscription",
        validityTime = 3600
    ),
    uuid = UUID.randomUUID().toString(),
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* pre-approval created */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type          | Description                                                        |
|--------------------------|---------------|--------------------------------------------------------------------|
| `apiVersion`             | `String`      | API version                                                        |
| `preApproval`            | `PreApproval` | Payer details, currency, message, and validity duration in seconds |
| `uuid`                   | `String`      | Unique reference ID                                                |
| `productSubscriptionKey` | `String`      | Collection primary subscription key                                |
| `environment`            | `String`      | `"sandbox"` or `"production"`                                      |

---

## Get Pre-Approval Status

Checks the status of a pre-approval.

```kotlin
defaultRepository.getPreApprovalStatus(
    apiVersion = "v1_0",
    referenceId = preApprovalUuid,
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result -> /* ... */ }
```

| Parameter                | Type     | Description                                |
|--------------------------|----------|--------------------------------------------|
| `apiVersion`             | `String` | API version                                |
| `referenceId`            | `String` | UUID used when calling `createPreApproval` |
| `productSubscriptionKey` | `String` | Collection primary subscription key        |
| `environment`            | `String` | `"sandbox"` or `"production"`              |

`getPreApprovalStatus(...)` returns `Flow<NetworkResult<PreApprovalStatus>>` (`payer`, `payerCurrency`, `payerMessage`, a `status` of type `StatusTypes` (`PENDING`, `SUCCESSFUL`, `FAILED`), `expirationDateTime`, and a `reason` of `code` + `message`).

---

## Cancel Pre-Approval

Cancels an active pre-approval.

```kotlin
defaultRepository.cancelPreApproval(
    apiVersion = "v1_0",
    referenceId = preApprovalUuid,
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result -> /* ... */ }
```

| Parameter                | Type     | Description                         |
|--------------------------|----------|-------------------------------------|
| `apiVersion`             | `String` | API version                         |
| `referenceId`            | `String` | UUID of the pre-approval to cancel  |
| `productSubscriptionKey` | `String` | Collection primary subscription key |
| `environment`            | `String` | `"sandbox"` or `"production"`       |

---

## Get Approved Pre-Approvals

Lists the approved pre-approvals for a given account holder.

```kotlin
defaultRepository.getApprovedPreApprovals(
    apiVersion = "v2_0",
    accountHolderIdType = "MSISDN",
    accountHolderId = "256774290781",
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result -> /* ... */ }
```

| Parameter                | Type     | Description                                     |
|--------------------------|----------|-------------------------------------------------|
| `apiVersion`             | `String` | API version                                     |
| `accountHolderIdType`    | `String` | Account holder identifier type (e.g. `MSISDN`)  |
| `accountHolderId`        | `String` | Account holder identifier whose approvals to list |
| `productSubscriptionKey` | `String` | Collection primary subscription key             |
| `environment`            | `String` | `"sandbox"` or `"production"`                   |

`getApprovedPreApprovals(...)` returns `Flow<NetworkResult<ApprovedPreApprovals>>`, whose `preApprovalDetails` is a list of `PreApprovalDetails` (`preApprovalId`, `toFri`, `fromFri`, `fromCurrency`, `createdTime`, a `status` `StatusTypes` enum, `message`, and optional `approvedTime`, `expiryTime`, a `frequency` `FrequencyTypes` enum, `startDate`, `lastUsedDate`, `offer`, `externalId`, `maxDebitAmount`).

---

## Create Payment

Creates a payment (V2). The `money` amount reuses the `Money` model.

```kotlin
defaultRepository.createPayment(
    apiVersion = "v2_0",
    payment = Payment(
        externalTransactionId = UUID.randomUUID().toString(),
        money = Money(amount = "100", currency = "EUR"),
        customerReference = "+46070911111",
        serviceProviderUserName = "rekast",
        couponId = null,
        productId = "product-42",
        productOfferingId = "offering-42",
        receiverMessage = "Payment for order #42",
        senderNote = "Order #42",
        maxNumberOfRetries = 3,
        includeSenderCharges = false
    ),
    uuid = paymentUuid,
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result -> /* ... */ }
```

| Parameter                | Type      | Description                                        |
|--------------------------|-----------|----------------------------------------------------|
| `apiVersion`             | `String`  | API version, e.g. `"v2_0"`                         |
| `payment`                | `Payment` | Amount/currency (`Money`), references, and notes   |
| `uuid`                   | `String`  | Unique reference ID — save this to poll for status |
| `productSubscriptionKey` | `String`  | Collection primary subscription key                |
| `environment`            | `String`  | `"sandbox"` or `"production"`                      |

---

## Get Payment Status

Checks the status of a previously created payment.

```kotlin
defaultRepository.getPaymentStatus(
    apiVersion = "v2_0",
    referenceId = paymentUuid,
    productSubscriptionKey = collectionPrimaryKey,
    environment = "sandbox"
).collect { result -> /* ... */ }
```

| Parameter                | Type     | Description                            |
|--------------------------|----------|----------------------------------------|
| `apiVersion`             | `String` | API version, e.g. `"v2_0"`             |
| `referenceId`            | `String` | UUID used when calling `createPayment` |
| `productSubscriptionKey` | `String` | Collection primary subscription key    |
| `environment`            | `String` | `"sandbox"` or `"production"`          |

`getPaymentStatus(...)` returns `Flow<NetworkResult<PaymentStatus>>` (`referenceId`, a `status` of type `StatusTypes` (`CREATED`, `PENDING`, `SUCCESSFUL`, `FAILED`), `financialTransactionId`, and a `reason` of `code` + `message`).
