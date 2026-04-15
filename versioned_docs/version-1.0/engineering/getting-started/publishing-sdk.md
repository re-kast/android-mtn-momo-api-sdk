---
sidebar_position: 2
sidebar_label: SDK Publishing
---

# SDK Publishing

Publishing the MTN MOMO API SDK is handled automatically by a GitHub Actions CI pipeline using the standard Gradle [`maven-publish`](https://docs.gradle.org/current/userguide/publishing_maven.html) plugin. Pushing a git tag triggers the workflow, which runs tests, signs artifacts, and publishes to Maven Central — no manual steps required.

---

## Repositories

Every tag publishes to both Maven Central and GitHub Packages. A GitHub Release is also created for stable tags.

| Version type | Repository | URL |
|---|---|---|
| Stable release | Maven Central | [io.rekast:momo-api-sdk](https://central.sonatype.com/artifact/io.rekast/momo-api-sdk) |
| Stable release | GitHub Packages | [re-kast/android-mtn-momo-api-sdk](https://github.com/re-kast/android-mtn-momo-api-sdk/packages) |
| Snapshot | Maven Central Snapshots | `https://central.sonatype.com/repository/maven-snapshots/` |
| Snapshot | GitHub Packages | [re-kast/android-mtn-momo-api-sdk](https://github.com/re-kast/android-mtn-momo-api-sdk/packages) |

---

## How Publishing Works

The publish pipeline is defined in [`.github/workflows/publish.yml`](https://github.com/re-kast/android-mtn-momo-api-sdk/blob/develop/.github/workflows/publish.yml) and is triggered by pushing a tag.

### Tag format

| Tag | Published version | Destination |
|---|---|---|
| `v1.2.3` | `1.2.3` | Maven Central (release) |
| `v1.2.3-SNAPSHOT` | `1.2.3-SNAPSHOT` | Maven Central Snapshots |

### Release path

1. Gradle builds the AAR, sources JAR, and Dokka javadoc JAR.
2. Artifacts are GPG-signed using in-memory keys from GitHub secrets.
3. Everything is written to a local staging directory.
4. The staging directory is zipped and uploaded to the Maven Central portal API with `publishingType=AUTOMATIC`.
5. The same artifacts are published to GitHub Packages.
6. A GitHub Release is created with the AAR, sources JAR, and POM attached as downloadable assets.

### Snapshot path

1. Artifacts are built, signed, and deployed directly to the Maven Central snapshots repository.
2. The same artifacts are also published to GitHub Packages.

---

## Pushing a Release

1. **Update the version** in `android/momo-api-sdk/gradle.properties`:
   ```properties
   VERSION_NAME=1.2.3
   ```

2. **Commit and push**, then **create and push a tag**:
   ```bash
   git tag v1.2.3
   git push origin v1.2.3
   ```

The CI workflow starts automatically. You can monitor it under **Actions → Publish to Maven Central** in the GitHub repository.

### Pushing a snapshot

```bash
git tag v1.2.3-SNAPSHOT
git push origin v1.2.3-SNAPSHOT
```

Snapshot versions are available immediately after the workflow completes. To consume a snapshot, add the Maven Central snapshots repository to your project:

```kotlin
repositories {
    maven("https://central.sonatype.com/repository/maven-snapshots/")
}
```

---

## Required GitHub Secrets

The following secrets must be configured under **Settings → Secrets and variables → Actions** in the repository:

| Secret | Description |
|---|---|
| `MAVEN_CENTRAL_USERNAME` | User token name from [central.sonatype.com](https://central.sonatype.com) → Account → User Token |
| `MAVEN_CENTRAL_PASSWORD` | User token password from [central.sonatype.com](https://central.sonatype.com) → Account → User Token |
| `SIGNING_KEY` | Armored GPG private key: `gpg --export-secret-keys --armor <KEY_ID>` |
| `SIGNING_PASSWORD` | Passphrase for the GPG key |

---

## Publishing Locally (Testing)

To verify the publish configuration without uploading to Maven Central, publish to the local staging directory:

```bash
cd android
./gradlew :momo-api-sdk:publishReleasePublicationToLocalStagingRepository \
  -PVERSION_NAME="1.2.3"
```

The staged artifacts are written to `android/momo-api-sdk/build/staging-deploy/`. Inspect them to verify the POM metadata, signatures, and JARs are correct before pushing a tag.

To publish to your local Maven repository (`~/.m2/`) for use in other local projects:

```bash
cd android
./gradlew :momo-api-sdk:publishToMavenLocal -PVERSION_NAME="1.2.3"
```

---

## Versioning

Follow [semantic versioning](https://semver.org/):

- **Patch** (`1.2.3` → `1.2.4`): Bug fixes, no API changes.
- **Minor** (`1.2.3` → `1.3.0`): New features, backwards compatible.
- **Major** (`1.2.3` → `2.0.0`): Breaking API changes.

Append `-SNAPSHOT` to a version to publish a development preview (e.g. `1.3.0-SNAPSHOT`).

---

## References

- [Gradle Maven Publish Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Maven Central Portal](https://central.sonatype.com/)
- [Maven Central — Publishing Guide](https://central.sonatype.org/publish/publish-portal-upload/)
- [Semantic Versioning](https://semver.org/)
- [Dokka Documentation](https://kotlinlang.org/docs/dokka-introduction.html)
- [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
