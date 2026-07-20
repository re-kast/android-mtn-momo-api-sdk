# Security Policy

The MTN MoMo API SDK (`io.rekast:momo-api-sdk`) handles authentication material —
API-user IDs, subscription keys, and OAuth2 / access tokens — on behalf of the apps that
embed it. We take the security of the SDK and of the credentials it manages seriously, and
we appreciate the community's help in keeping it safe.

This policy explains which versions receive security fixes, how to report a vulnerability,
what to expect after you report, and how to configure the SDK securely.

## Supported Versions

The SDK is pre-1.0 and still evolving, so security fixes land on the **latest published
minor line only**. Older lines do not receive back-ported patches — upgrade to the newest
release to stay covered. When we reach 1.0 this table will move to the standard "current +
previous minor" model.

| Version          | Supported          | Notes                                                  |
|------------------|--------------------|--------------------------------------------------------|
| `0.3.x`          | :white_check_mark: | Current release line — receives all security fixes     |
| `0.3.x-SNAPSHOT` | :warning:          | Pre-release; fixes land here first, not for production |
| `< 0.3`          | :x:                | Unsupported — please upgrade                           |

Releases are published to Maven Central. Always pin an explicit, non-`SNAPSHOT` version in
production builds and update promptly when a security release is announced.

## Reporting a Vulnerability

**Please do not open a public GitHub issue, pull request, or discussion for security
problems, and do not disclose the details publicly until a fix has been released.**

Report privately through GitHub's built-in **private vulnerability reporting**:

1. Go to the repository's **[Security tab](https://github.com/re-kast/android-mtn-momo-api-sdk/security)**.
2. Click **"Report a vulnerability"** to open a private security advisory visible only to
   you and the maintainers.
3. If you are unable to use that channel, open a minimal public issue that says only
   *"I would like to report a security vulnerability privately"* — with **no technical
   details** — and a maintainer will open a private advisory to continue the conversation.

### What to include

The more of the following you can provide, the faster we can triage:

- The affected version(s) and platform (Android API level, target/host, etc.).
- A description of the vulnerability and its security impact.
- Step-by-step reproduction instructions, and a proof-of-concept if you have one.
- Any relevant logs, stack traces, or configuration (with secrets redacted).
- Your assessment of severity and any suggested remediation.

Please test only against your own sandbox credentials and environments. Do not access,
modify, or exfiltrate data that isn't yours, and do not run denial-of-service or
high-volume automated testing against MTN's live services.

## What to Expect

We follow a coordinated-disclosure process:

| Stage                | Target timeline                                                            |
|----------------------|----------------------------------------------------------------------------|
| Acknowledgement      | Within **3 business days** of your report                                  |
| Initial assessment   | Within **10 business days** (triage, severity, whether it's accepted)      |
| Progress updates     | At least once every **2 weeks** while the report is open                   |
| Fix & release target | **Critical/High**: within 30 days · **Medium/Low**: next scheduled release |
| Public disclosure    | Coordinated with you, typically within **90 days** of triage               |

- **If the report is accepted**, we will work on a fix, keep you updated, credit you in the
  advisory and release notes (unless you prefer to remain anonymous), and publish a GitHub
  Security Advisory once the fix is released.
- **If the report is declined** (e.g., out of scope, working as intended, or not
  reproducible), we will explain why. You are welcome to share additional context if you
  disagree with the assessment.

Severity is assessed using [CVSS 3.1](https://www.first.org/cvss/calculator/3.1). Timelines
are targets, not guarantees — a maintainer will always tell you where things stand.

## Scope

**In scope** — this repository's code:

- The `momo-api-sdk` library (networking, authentication/token handling, interceptors,
  credential storage helpers, and models).
- The build and release pipeline for the published artifact.

**Out of scope:**

- The MTN MoMo API itself and MTN's infrastructure — report those to MTN.
- The `sample` and `app` modules, which are demonstrations only and are **not** intended
  for production use.
- Vulnerabilities that require a rooted/compromised device, a malicious OS, or physical
  access to an unlocked device.
- Issues caused by an integrating app misusing the SDK contrary to the guidance below.

## Using the SDK Securely

Because this SDK manages payment-provider credentials, a few practices materially reduce
your risk:

- **Never commit secrets.** Keep `MOMO_*` subscription keys, API-user IDs, and any
  keystore material out of version control (use `local.properties`, environment variables,
  or a secrets manager). Rotate any credential that has ever been committed.
- **Prefer production configuration.** The sandbox helper `UnsafeOkHttpClient` disables TLS
  certificate validation and must **never** be used in a release build or against
  production endpoints — it exists solely for local sandbox testing.
- **Protect tokens at rest.** Access and consent tokens are sensitive; store them using the
  provided encrypted storage and clear them on sign-out. Do not log tokens, subscription
  keys, or full request/response bodies in production.
- **Keep dependencies current.** Update to the latest SDK release regularly; the project
  runs CodeQL analysis on every change, and security fixes ship in the current line only.
- **Enforce TLS.** Do not weaken the default HTTP client, disable certificate checks, or
  allow cleartext traffic for MoMo endpoints in production.

## Recognition

We are grateful to everyone who reports vulnerabilities responsibly. With your permission,
we credit reporters in the relevant GitHub Security Advisory and release notes.

---

*Thank you for helping keep the MTN MoMo API SDK and its users safe.*
