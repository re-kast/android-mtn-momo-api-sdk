---
sidebar_position: 6
sidebar_label: Callbacks
---

# Callbacks (`X-Callback-Url`)

MTN MoMo can POST the result of an asynchronous transaction to a URL you control, instead of you polling the status endpoint. You opt in by supplying a callback URL, which the SDK attaches as the `X-Callback-Url` header.

The SDK does this centrally through a single interceptor (`CallbackUrlInterceptor`) — you never pass the URL per call. It is applied only to the **transaction-initiation** POST endpoints:

`requesttopay`, `requesttowithdraw`, `deposit`, `refund`, `transfer`, `cashtransfer`, `invoice`, `preapproval`, `payment`

Status (GET), cancel (DELETE), account/balance/user-info queries, delivery-notification POSTs, and the auth/token endpoints never receive the header.

:::note[Opt-in]
No callback header is sent until you return a non-blank URL. With no configuration, everything works exactly as before (poll the status endpoints).
:::

:::warning[Register your callback host]
The callback host must be **registered/allowed for your API user** on the [MoMo Developer Portal](https://momodeveloper.mtn.com/). An unregistered or invalid URL (for example, pointing it at the API base URL) causes the initiation call to fail with **HTTP 400**.
:::

---

## How it works

Callbacks are supplied by your `CredentialProvider` — the same app-side hook that supplies credentials to the SDK's interceptors:

```kotlin
interface CredentialProvider {
    // …credentials…

    /** Return the callback URL for a given initiation [operation], or "" for none. */
    fun getCallbackUrl(operation: String): String = ""
}
```

`operation` is the endpoint's path segment (one of the nine listed above), so you can return a **different URL per operation** or a single shared URL. The default implementation returns `""`, so callbacks are off unless you override it.

---

## Setup (sample app, config-driven)

The sample app wires a base URL from configuration and appends the operation, giving each endpoint its own callback path.

**1. Add the base URL to `local.properties`** (it is read into `BuildConfig` at build time; leave it out to keep callbacks off):

```properties
MOMO_CALLBACK_BASE_URL=https://webhooks.example.com/momo
```

**2. It flows through to the config** — `app/build.gradle.kts` exposes it as `BuildConfig.MOMO_CALLBACK_BASE_URL`, and `AppModule` puts it on `SampleConfig.callbackBaseUrl`:

```kotlin
// AppModule.provideSampleConfig()
callbackBaseUrl = BuildConfig.MOMO_CALLBACK_BASE_URL
```

**3. The app's `CredentialProvider` appends the operation:**

```kotlin
override fun getCallbackUrl(operation: String): String {
    val base = sampleConfig.callbackBaseUrl
    return if (base.isBlank()) "" else "${base.trimEnd('/')}/$operation"
}
```

With `MOMO_CALLBACK_BASE_URL=https://webhooks.example.com/momo`, the SDK then sends:

| Operation           | `X-Callback-Url`                                      |
|---------------------|-------------------------------------------------------|
| `requesttopay`      | `https://webhooks.example.com/momo/requesttopay`      |
| `requesttowithdraw` | `https://webhooks.example.com/momo/requesttowithdraw` |
| `deposit`           | `https://webhooks.example.com/momo/deposit`           |
| `refund`            | `https://webhooks.example.com/momo/refund`            |
| `transfer`          | `https://webhooks.example.com/momo/transfer`          |
| `cashtransfer`      | `https://webhooks.example.com/momo/cashtransfer`      |
| `invoice`           | `https://webhooks.example.com/momo/invoice`           |
| `preapproval`       | `https://webhooks.example.com/momo/preapproval`       |
| `payment`           | `https://webhooks.example.com/momo/payment`           |

---

## Overriding — choosing your own URLs

`getCallbackUrl(operation)` is the single override point. Some common shapes:

**A distinct URL per endpoint** (e.g. different hosts or paths per product):

```kotlin
override fun getCallbackUrl(operation: String): String = when (operation) {
    "requesttopay"      -> "https://webhooks.example.com/collection/request-to-pay"
    "requesttowithdraw" -> "https://webhooks.example.com/collection/request-to-withdraw"
    "invoice"           -> "https://webhooks.example.com/collection/invoice"
    "payment"           -> "https://webhooks.example.com/collection/payment"
    "preapproval"       -> "https://webhooks.example.com/collection/pre-approval"
    "deposit"           -> "https://webhooks.example.com/disbursement/deposit"
    "refund"            -> "https://webhooks.example.com/disbursement/refund"
    "transfer"          -> "https://webhooks.example.com/transfer"
    "cashtransfer"      -> "https://webhooks.example.com/remittance/cash-transfer"
    else                -> "" // no callback for anything else
}
```

**A single shared webhook** (demultiplex by `referenceId`/`externalId` in the callback body):

```kotlin
override fun getCallbackUrl(operation: String): String = "https://webhooks.example.com/momo"
```

**Callbacks for only some operations** — return `""` for the rest:

```kotlin
override fun getCallbackUrl(operation: String): String = when (operation) {
    "requesttopay" -> "https://webhooks.example.com/momo/request-to-pay"
    else           -> ""
}
```

---

## Notes

- Returning `""` (blank) for an operation omits the `X-Callback-Url` header for that request.
- The `operation` values are raw MTN path segments (lowercase, no separators), which is why they read as `requesttopay` rather than `requestToPay`.
- Callbacks apply only to the initiation POSTs; there is nothing to configure for status/cancel calls.
