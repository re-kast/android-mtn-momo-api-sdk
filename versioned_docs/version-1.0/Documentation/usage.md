---
sidebar_position: 1
sidebar_label: Library Usage
---

# Library Usage

The MTN MOMO API SDK documentation is built using [Docusaurus](https://docusaurus.io/), a modern static website generator that simplifies the creation of documentation websites. The documentation files are organized in a structured manner within the [versioned_docs folder](https://github.com/re-kast/android-mtn-momo-api-sdk/tree/feature/issue_27/versioned_docs), with the current version being `1.0`. You can find all relevant documentation in the `version-1.0` folder.

---

## Deployment

The documentation is automatically deployed to [https://mtn-momo-sdk.rekast.io/](https://mtn-momo-sdk.rekast.io/) using [GitHub Actions](https://github.com/re-kast/android-mtn-momo-api-sdk/blob/de966147f9a240b2bdf0611663a6fd5b05cf21ae/.github/workflows/docs.yml#L20-L46). This CI/CD pipeline ensures that any updates to the documentation are promptly reflected on the website, providing users with the latest information.

### GitHub Actions Workflow

The following YAML configuration outlines the steps involved in building and deploying the Docusaurus documentation:

```yaml
build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: 18
          cache: yarn

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

      - name: Install Docusaurus dependencies
        run: yarn install

      - name: Build Docusaurus site
        run: yarn build

      - name: Copy Dokka output into Docusaurus build
        run: |
          mkdir -p ./build/dokka
          cp -r android/build/dokka/html/. ./build/dokka/
          cp -r CNAME ./build

      - name: Deploy to gh-pages
        if: ${{ github.event_name == 'push' }}
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_branch: gh-pages
          publish_dir: ./build
          user_name: github-actions[bot]
          user_email: 41898282+github-actions[bot]@users.noreply.github.com
```

### Explanation of Workflow Steps

1. **Checkout the Repository**: The workflow starts by checking out the repository to access the documentation files.
2. **Setup Node.js**: Sets up Node.js 18, required for building the Docusaurus site.
3. **Setup JDK 17**: Sets up OpenJDK 17, required for the Gradle/Dokka build.
4. **Create `local.properties`**: Creates the required properties file with placeholder values so the Android build compiles correctly without real API keys.
5. **Create `keystore.properties`**: Creates an empty keystore file required by the Gradle build.
6. **Grant execute permission**: Ensures `gradlew` is executable.
7. **Generate Dokka docs**: Runs `./gradlew dokkaGenerate` to produce KDoc HTML for all modules.
8. **Install Dependencies**: Installs Yarn dependencies for the Docusaurus build.
9. **Build the Website**: Builds the Docusaurus site, generating static files for deployment.
10. **Copy Dokka output**: Copies the generated KDoc HTML into the Docusaurus build output at `build/dokka/`.
11. **Deploy to GitHub Pages**: Deploys the combined Docusaurus + KDoc build to the `gh-pages` branch.

## Running Documentation Locally

To preview the documentation locally before deployment, follow these steps:

1. Ensure you have Node.js and Yarn installed on your machine.
2. Navigate to the root project folder:
   ```bash
   cd path/to/your/project
   ```
3. Run the following command to build the documentation site:
   ```bash
   yarn build
   ```
4. Start a local server to serve the site:
   ```bash
   yarn serve
   ```
5. Open your web browser and navigate to `http://localhost:3000` to view the documentation.

## Additional Resources

- **Docusaurus Documentation**: For more information on how to use Docusaurus, refer to the [official documentation](https://docusaurus.io/docs).
- **GitHub Actions Documentation**: Learn more about GitHub Actions and how to automate your workflows by visiting the [GitHub Actions documentation](https://docs.github.com/en/actions).
- **Node.js Documentation**: For details on Node.js and its features, check out the [Node.js documentation](https://nodejs.org/en/docs/).
- **Yarn Documentation**: For more information on using Yarn as a package manager, visit the [Yarn documentation](https://classic.yarnpkg.com/en/docs/).

By following this documentation, developers can effectively utilize the MTN MOMO API SDK and contribute to its ongoing development and documentation efforts. This ensures that the SDK remains accessible, well-documented, and easy to integrate into applications.