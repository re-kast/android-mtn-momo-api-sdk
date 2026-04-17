---
sidebar_position: 4
sidebar_label: Remittance
---

# Library Usage — Remittance

Remittance APIs let you send international transfers to Mobile Money accounts and check their status.

All flows require a valid Bearer access token stored in `CredentialStorage`.

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

| Parameter | Type | Description |
|---|---|---|
| `productType` | `String` | `ProductType.REMITTANCE.productType` |
| `apiVersion` | `String` | API version, e.g. `"v1_0"` |
| `momoTransaction` | `MomoTransaction` | Transfer details (amount, currency, payee, messages) |
| `uuid` | `String` | Unique reference ID — save this to poll for status |
| `productSubscriptionKey` | `String` | Remittance primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |

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

| Parameter | Type | Description |
|---|---|---|
| `productType` | `String` | `ProductType.REMITTANCE.productType` |
| `apiVersion` | `String` | API version |
| `referenceId` | `String` | UUID used when calling `transfer` |
| `productSubscriptionKey` | `String` | Remittance primary subscription key |
| `environment` | `String` | `"sandbox"` or `"production"` |
