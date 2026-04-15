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

The following YAML configuration outlines the steps involved in building and deploying the Dokka documentation:

```yaml
build-and-deploy-dokka:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout 🛎️
        uses: actions/checkout@v2.3.1

      - name: Set up JDK 17
        uses: actions/setup-java@v1
        with:
          java-version: 17

      - name: Add empty local.properties
        run: |
          touch local.properties
          echo "MOMO_BASE_URL=${MOMO_BASE_URL}" >> local.properties
          echo "MOMO_COLLECTION_PRIMARY_KEY=${MOMO_COLLECTION_PRIMARY_KEY}" >> local.properties
          echo "MOMO_COLLECTION_SECONDARY_KEY=${MOMO_COLLECTION_SECONDARY_KEY}" >> local.properties
          echo "MOMO_REMITTANCE_PRIMARY_KEY=${MOMO_REMITTANCE_PRIMARY_KEY}" >> local.properties
          echo "MOMO_REMITTANCE_SECONDARY_KEY=${MOMO_REMITTANCE_SECONDARY_KEY}" >> local.properties
          echo "MOMO_DISBURSEMENTS_PRIMARY_KEY=${MOMO_DISBURSEMENTS_PRIMARY_KEY}" >> local.properties
          echo "MOMO_DISBURSEMENTS_SECONDARY_KEY=${MOMO_DISBURSEMENTS_SECONDARY_KEY}" >> local.properties
          echo "MOMO_API_USER_ID=${MOMO_API_USER_ID}" >> local.properties
          echo "MOMO_ENVIRONMENT=${MOMO_ENVIRONMENT}" >> local.properties
          echo "MOMO_API_VERSION_V1=${MOMO_API_VERSION_V1}" >> local.properties
          echo "MOMO_API_VERSION_V2=${MOMO_API_VERSION_V2}" >> local.properties
        working-directory: android

      - name: Add empty keystore.properties
        run: touch keystore.properties
        working-directory: android

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
        working-directory: android

      - name: Document modules with Dokka
        run: ./gradlew dokkaHtmlMultiModule
        working-directory: android

      - name: Deploy 🚀
        if: ${{ github.event_name == 'push' }}
        uses: JamesIves/github-pages-deploy-action@v4.4.1
        with:
          branch: gh-pages # The branch the action should deploy to.
          folder: android/build/dokka # The folder the action should deploy.
          target-folder: dokka
          ssh-key: ${{ secrets.DEPLOY_KEY }}
```

### Explanation of Workflow Steps

1. **Checkout the Repository**: The workflow starts by checking out the repository to access the documentation files.
2. **Set up JDK 17**: It sets up the Java Development Kit (JDK) version 17, which is required for building the documentation.
3. **Add Local Properties**: Creates a `local.properties` file with necessary environment variables for the build process.
4. **Add Keystore Properties**: Creates an empty `keystore.properties` file, which may be required for signing the application.
5. **Grant Execute Permission**: Ensures that the `gradlew` script has execute permissions.
6. **Document Modules with Dokka**: Runs the Dokka task to generate HTML documentation for all modules.
7. **Deploy to GitHub Pages**: Finally, the generated documentation is deployed to the `gh-pages` branch, making it accessible via the specified URL.

## Generating Documentation Locally

You can also generate documentation locally to preview what will be deployed once the GitHub Actions run. This is made possible by the configuration specified in the `build.gradle.kts` file:

```kotlin
tasks.named<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>("dokkaHtmlMultiModule") {
    moduleName.set("| MTN MOMO ANDROID SDK")
    outputDirectory.set(layout.buildDirectory.dir("dokka"))

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(layout.projectDirectory.file("assets/logo-icon.svg").asFile)
        customStyleSheets = listOf(layout.projectDirectory.file("assets/rekast.css").asFile)
        footerMessage = "&copy; Re.Kast Limited"
        separateInheritedMembers = false
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
   ./gradlew dokkaHtmlMultiModule
   ```
3. Check `build/dokka/` in the root build folder.
4. Open `index.html` to preview the documentation.

## Additional Resources

- **Dokka Documentation**: For more information on how to use Dokka, refer to the [official documentation](https://kotlinlang.org/docs/dokka/overview.html).
- **Kotlin Documentation**: Learn more about Kotlin and its features by visiting the [Kotlin documentation](https://kotlinlang.org/docs/home.html).
- **GitHub Actions Documentation**: For details on automating workflows with GitHub Actions, check out the [GitHub Actions documentation](https://docs.github.com/en/actions).
- **Continuous Integration (CI) Best Practices**: For insights on best practices in CI, refer to [CI Best Practices](https://www.atlassian.com/continuous-delivery/continuous-integration-best-practices).

By following this documentation, developers can effectively generate and maintain code documentation for the MTN MOMO API SDK, ensuring that it remains accessible and useful for all users.
