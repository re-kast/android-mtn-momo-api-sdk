---
sidebar_position: 4
sidebar_label: Remittance
---

# Library Usage — Remittance

Remittance APIs let you send international transfers to Mobile Money accounts and check their status. Two flows are available: the legacy V1 `transfer` and the V2 `cashTransfer`, which extends the payload with optional KYC fields about the sending party for cross-border compliance.

Authentication is handled automatically by the SDK's interceptors — you never pass an access token. Every `DefaultRepository` method returns a `Flow<NetworkResult<T>>`; collect it inside a coroutine scope.

> **Environment and cross-cutting headers are automatic.** You set the target environment once during SDK setup — `ApiConfig.environment`, which is sourced from `MOMO_ENVIRONMENT` in `local.properties`. On every request the SDK's `EnvironmentInterceptor` adds the required `X-Target-Environment` header, so **environment is never a per-call parameter**. Authentication headers (`Authorization`) are attached the same way, and transaction-initiation endpoints additionally receive an optional `X-Callback-Url` header. See [Callbacks](./callbacks) for details.

---

## Transfer

Sends a cross-border remittance to a recipient's Mobile Money account.

```kotlin
defaultRepository.transfer(
    productType = ProductTypes.REMITTANCE.productType,
    apiVersion = "v1_0",
    transfer = Transfer(
        amount = "500",
        currency = "EUR",
        externalId = UUID.randomUUID().toString(),
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerMessage = "Remittance from abroad",
        payeeNote = "Family support"
    ),
    uuid = UUID.randomUUID().toString(),
    productSubscriptionKey = remittancePrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* transfer initiated */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type       | Description                                          |
|--------------------------|------------|------------------------------------------------------|
| `productType`            | `String`   | `ProductTypes.REMITTANCE.productType`                |
| `apiVersion`             | `String`   | API version, e.g. `"v1_0"`                           |
| `transfer`               | `Transfer` | Transfer details (amount, currency, payee, messages) |
| `uuid`                   | `String`   | Unique reference ID — save this to poll for status   |
| `productSubscriptionKey` | `String`   | Remittance primary subscription key                  |

---

## Get Transfer Status

Retrieves the status of a previously initiated remittance transfer.

```kotlin
defaultRepository.getTransferStatus(
    productType = ProductTypes.REMITTANCE.productType,
    apiVersion = "v1_0",
    referenceId = transferUuid,
    productSubscriptionKey = remittancePrimaryKey
).collect { result ->
    when (result) {
        is NetworkResult.Success -> { /* parse result.response */ }
        is NetworkResult.Error   -> { /* failed */ }
        is NetworkResult.Loading -> { /* in progress */ }
    }
}
```

| Parameter                | Type     | Description                           |
|--------------------------|----------|---------------------------------------|
| `productType`            | `String` | `ProductTypes.REMITTANCE.productType` |
| `apiVersion`             | `String` | API version                           |
| `referenceId`            | `String` | UUID used when calling `transfer`     |
| `productSubscriptionKey` | `String` | Remittance primary subscription key   |

Returns `Flow<NetworkResult<TransferStatus>>` — `TransferStatus` carries `amount`, `currency`, `financialTransactionId`, `externalId`, a `payee` (`Party`), `payerMessage`, `payeeNote`, a `status` of type `StatusTypes` (`PENDING`, `SUCCESSFUL`, `FAILED`), and a `reason` (`ErrorResponse` — `code` + `message`).

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
        payee = Party(partyIdType = PartyTypes.MSISDN, partyId = "256770000000"),
        payerMessage = "Cash transfer",
        payeeNote = "Family support",
        // Optional KYC fields describing the sending party:
        payerIdentificationType = PayerIdentificationType.PASS,
        payerIdentificationNumber = "A1234567",
        payerIdentity = "256770000001",
        payerFirstName = "Jane",
        payerSurName = "Doe",
        payerLanguageCode = "en",
        payerEmail = "jane.doe@example.com",
        payerMsisdn = "256770000001",
        payerGender = "female",
        // Optional foreign-exchange fields:
        originatingCountry = "UG",
        originalAmount = "1200000",
        originalCurrency = "UGX"
    ),
    uuid = cashTransferUuid,
    productSubscriptionKey = remittancePrimaryKey
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

### `CashTransfer` object fields

The first six fields are required; every `payer*` KYC field and the foreign-exchange fields are optional (nullable) and are simply omitted from the request when left unset. The KYC fields identify the sending party and are what make the V2 `cashtransfer` suitable for cross-border compliance when the payer is not a registered MTN mobile money subscriber.

| Field                       | Type                       | Required | Description                                                                                                                                          |
|-----------------------------|----------------------------|----------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `amount`                    | `String`                   | Yes      | The transfer amount                                                                                                                                  |
| `currency`                  | `String`                   | Yes      | ISO 4217 currency code for the transaction (e.g. `"EUR"`, `"UGX"`)                                                                                   |
| `externalId`                | `String`                   | Yes      | Merchant-assigned reference used to correlate the transfer on the integrator side                                                                    |
| `payee`                     | `Party`                    | Yes      | The party receiving the funds; `partyIdType` is a `PartyTypes` enum (e.g. `MSISDN`)                                                                  |
| `payerMessage`              | `String`                   | Yes      | Message visible to the payer describing the purpose of the transfer                                                                                  |
| `payeeNote`                 | `String`                   | Yes      | Note visible to the payee describing the purpose of the transfer                                                                                     |
| `payerIdentificationType`   | `PayerIdentificationType?` | No (KYC) | Type of identification document (`PASS`, `NRIP`, `ALIP`, `ARIP`, `PMRP`, `PECP`, etc.)                                                               |
| `payerIdentificationNumber` | `String?`                  | No (KYC) | Identification document number matching `payerIdentificationType`                                                                                    |
| `payerIdentity`             | `String?`                  | No (KYC) | MSISDN of the sending party (payer)                                                                                                                  |
| `payerFirstName`            | `String?`                  | No (KYC) | First name of the sending party                                                                                                                      |
| `payerSurName`              | `String?`                  | No (KYC) | Surname of the sending party                                                                                                                         |
| `payerLanguageCode`         | `String?`                  | No (KYC) | ISO 639-1 two-letter language code for the payer (e.g. `"en"`)                                                                                       |
| `payerEmail`                | `String?`                  | No (KYC) | Email address of the sending party                                                                                                                   |
| `payerMsisdn`               | `String?`                  | No (KYC) | Phone number of the sending party                                                                                                                    |
| `payerGender`               | `String?`                  | No (KYC) | Gender code of the sending party (per ISO 20022)                                                                                                     |
| `originatingCountry`        | `String?`                  | No (FX)  | ISO country code of the country the funds originate from. Serialized on the wire as `orginatingCountry` (the deliberately misspelled MTN field name) |
| `originalAmount`            | `String?`                  | No (FX)  | The amount in the originating currency before conversion                                                                                             |
| `originalCurrency`          | `String?`                  | No (FX)  | ISO 4217 currency code of the originating amount                                                                                                     |

---

## Get Cash Transfer Status

Retrieves the status of a previously initiated cash transfer.

```kotlin
defaultRepository.getCashTransferStatus(
    apiVersion = "v2_0",
    referenceId = cashTransferUuid,
    productSubscriptionKey = remittancePrimaryKey
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

Returns `Flow<NetworkResult<CashTransferStatus>>` — `CashTransferStatus` mirrors the `CashTransfer` payload (amount, currency, `payee`, externalId, the `payer*` KYC fields, and the foreign-exchange fields) and adds the outcome fields `financialTransactionId`, a `status` string (`PENDING`, `SUCCESSFUL`, `FAILED`), and a `reason` string. The `payerIdentificationType` is a `PayerIdentificationType` enum and `orginatingCountry` is the (deliberately misspelled) MTN wire field mapped to the `originatingCountry` property.
