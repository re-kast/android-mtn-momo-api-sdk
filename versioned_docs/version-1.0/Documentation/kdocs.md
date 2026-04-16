---
sidebar_position: 2
sidebar_label: Code Documentation (`Kdocs`)
---

# Code Documentation (`Kdocs`)

We utilize [Dokka](https://kotlinlang.org/docs/dokka/overview.html) to generate comprehensive Kotlin code documentation for the MTN MOMO API SDK. This tool ensures that all code added to the repository is accompanied by corresponding documentation, significantly enhancing usability and maintainability for developers.

The generated documentation is automatically deployed to [https://mtn-momo-sdk.rekast.io/](https://mtn-momo-sdk.rekast.io/) via [GitHub Actions](https://github.com/re-kast/android-mtn-momo-api-sdk/blob/de966147f9a240b2bdf0611663a6fd5b05cf21ae/.github/workflows/docs.yml#L48-L94). This CI/CD pipeline guarantees that any updates to the documentation are promptly reflected on the website.

You can access the Kdocs directly via this link: [Kdocs Documentation](https://mtn-momo-sdk.rekast.io/dokka/).

---

## GitHub Actions Workflow for Dokka

Dokka documentation is generated as part of the same `docs.yml` workflow that builds and deploys the Docusaurus site. The relevant steps are shown below:

```yaml
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Create local.properties
        working-directory: android
        run: |
          cat <<'EOF' > local.properties
          MOMO_BASE_URL="https://sandbox.momodeveloper.mtn.com/"
          MOMO_PROVIDER_CALBACK_HOST="localhost"
          MOMO_COLLECTION_PRIMARY_KEY="placeholder"
          MOMO_COLLECTION_SECONDARY_KEY="placeholder"
          MOMO_REMITTANCE_PRIMARY_KEY="placeholder"
          MOMO_REMITTANCE_SECONDARY_KEY="placeholder"
          MOMO_DISBURSEMENTS_PRIMARY_KEY="placeholder"
          MOMO_DISBURSEMENTS_SECONDARY_KEY="placeholder"
          MOMO_API_USER_ID="placeholder"
          MOMO_ENVIRONMENT="sandbox"
          MOMO_API_VERSION_V1="v1_0"
          MOMO_API_VERSION_V2="v2_0"
          EOF

      - name: Create keystore.properties
        run: touch keystore.properties
        working-directory: android

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
        working-directory: android

      - name: Generate Dokka docs
        run: ./gradlew dokkaGenerate
        working-directory: android

      - name: Copy Dokka output into Docusaurus build
        run: |
          mkdir -p ./build/dokka
          cp -r android/build/dokka/html/. ./build/dokka/
          cp -r CNAME ./build
```

### Explanation of Workflow Steps

1. **Set up JDK 17**: Sets up OpenJDK 17, required for the Gradle/Dokka build.
2. **Create `local.properties`**: Creates the required properties file with placeholder values so the Android build compiles without real API keys.
3. **Create `keystore.properties`**: Creates an empty keystore file required by the Gradle build.
4. **Grant Execute Permission**: Ensures that the `gradlew` script has execute permissions.
5. **Generate Dokka docs**: Runs `./gradlew dokkaGenerate` to produce HTML documentation for all modules. The aggregated output lands in `android/build/dokka/html/`.
6. **Copy Dokka output**: Copies the generated KDoc HTML into the Docusaurus build at `build/dokka/`, making it accessible at [https://mtn-momo-sdk.rekast.io/dokka/](https://mtn-momo-sdk.rekast.io/dokka/).

## Generating Documentation Locally

You can also generate documentation locally to preview what will be deployed once the GitHub Actions run. This is made possible by the configuration specified in the `build.gradle.kts` file:

**Root `android/build.gradle.kts`** — sets the module name, custom styling, and aggregates both submodules:

```kotlin
dokka {
    moduleName.set("| MTN MOMO ANDROID SDK")
    pluginsConfiguration.html {
        customAssets.from(layout.projectDirectory.file("assets/logo-icon.svg"))
        customStyleSheets.from(layout.projectDirectory.file("assets/rekast.css"))
        footerMessage.set("&copy; Re.Kast Limited")
        separateInheritedMembers.set(false)
    }
}

dependencies {
    dokka(project(":momo-api-sdk"))
    dokka(project(":sample"))
}
```

**`momo-api-sdk/build.gradle.kts`** — registers the three KMP source sets explicitly so Dokka V2 picks them up:

```kotlin
dokka {
    dokkaSourceSets {
        named("commonMain") {
            displayName.set("Common")
            sourceRoots.from(file("src/commonMain/kotlin"))
        }
        named("androidMain") {
            displayName.set("Android")
            sourceRoots.from(file("src/androidMain/kotlin"))
        }
        named("jvmMain") {
            displayName.set("JVM")
            sourceRoots.from(file("src/jvmMain/kotlin"))
        }
    }
    pluginsConfiguration.html {
        customAssets.from(rootProject.layout.projectDirectory.file("assets/logo-icon.svg"))
        customStyleSheets.from(rootProject.layout.projectDirectory.file("assets/rekast.css"))
        footerMessage.set("&copy; Re.Kast Limited")
    }
}
```

**`sample/build.gradle.kts`** — `sample` is a plain Android library; Dokka auto-detects its sources, so only the HTML customisation is needed:

```kotlin
dokka {
    pluginsConfiguration.html {
        customAssets.from(rootProject.layout.projectDirectory.file("assets/logo-icon.svg"))
        customStyleSheets.from(rootProject.layout.projectDirectory.file("assets/rekast.css"))
        footerMessage.set("&copy; Re.Kast Limited")
    }
}
```

### Steps to Generate Documentation Locally

1. Navigate to the `android` folder in your project directory:
   ```bash
   cd android
   ```
2. Run the following command to generate the documentation:
   ```bash
   ./gradlew dokkaGenerate
   ```
3. Check `build/dokka/html/` in the root build folder.
4. Open `index.html` to preview the documentation.

## Additional Resources

- **Dokka Documentation**: For more information on how to use Dokka, refer to the [official documentation](https://kotlinlang.org/docs/dokka/overview.html).
- **Kotlin Documentation**: Learn more about Kotlin and its features by visiting the [Kotlin documentation](https://kotlinlang.org/docs/home.html).
- **GitHub Actions Documentation**: For details on automating workflows with GitHub Actions, check out the [GitHub Actions documentation](https://docs.github.com/en/actions).
- **Continuous Integration (CI) Best Practices**: For insights on best practices in CI, refer to [CI Best Practices](https://www.atlassian.com/continuous-delivery/continuous-integration-best-practices).

By following this documentation, developers can effectively generate and maintain code documentation for the MTN MOMO API SDK, ensuring that it remains accessible and useful for all users.
