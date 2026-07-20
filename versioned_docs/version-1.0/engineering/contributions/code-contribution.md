---
sidebar_position: 1
sidebar_label: Code Contributions
---

# Code Contributions 

Contributions to the MTN MoMo Android SDK are not only welcome but encouraged! We believe that collaboration is key to improving our project and making it more robust. If you have ideas, improvements, or bug fixes, please feel free to submit a Pull Request (PR). However, for significant changes, we recommend opening an issue first to discuss your proposed modifications. This helps ensure that your contributions align with the project's goals and standards.

---

## Steps to Contribute

To contribute to the project, please follow these steps:

1. **Fork the Project**: Start by creating your own copy of the repository. This allows you to make changes without affecting the original codebase.
   
2. **Create Your Branch**: Use the command `git checkout -b AmazingFeature` to create a new branch for your feature or fix. It’s important to use descriptive branch names that reflect the changes you are making. You can read more about branch naming conventions [here](./code-standards/#branch-naming).

3. **Commit Your Changes**: After making your changes, commit them using `git commit -m 'Add some AmazingFeature'`. Writing clear and concise commit messages is crucial for understanding the history of changes. You can find guidelines on writing effective commit messages [here](code-standards/#commit-messages).

4. **Push to Your Branch**: Once your changes are committed, push them to your forked repository using `git push origin AmazingFeature`. This makes your changes available for review.

5. **Open a Pull Request**: Finally, navigate to the original repository and open a Pull Request. This is where you can describe the changes you made and why they are beneficial to the project.

:::info

**Important Reminder**: If you make any changes to the SDK files, please remember to update the SDK version. You can do this [**here**](https://github.com/re-kast/android-mtn-momo-api-sdk/blob/a4f42bee8245c884586401ac210f46bac51f1953/android/momo-api-sdk/gradle.properties#L4). The current version is specified as follows:
```properties
VERSION_NAME=0.3.0-SNAPSHOT
```
:::

## Code Reviews

Once you submit a Pull Request, it will undergo a review process. For a PR to be merged, it must meet the following criteria:

* **Approval from at least one reviewer**: Each PR **MUST** receive approval from at least one designated reviewer. This ensures that your changes have been evaluated for quality and compatibility with the existing codebase.

* **Completion of all items in the PR template**: All checklist items or requirements outlined in the PR template must be marked as completed. This helps ensure that your contribution is ready for integration.

* **Passing status checks**: All required status checks, such as Continuous Integration/Continuous Deployment (CI/CD) pipelines or other automated checks, must pass successfully. This is crucial for maintaining the stability of the project.

* **Up-to-date with the develop branch**: The PR branch **MUST** be updated to the latest version of the [**develop branch**](https://github.com/re-kast/android-mtn-momo-api-sdk/tree/develop). This helps avoid merge conflicts and ensures compatibility with the latest changes in the codebase.

:::tip[Happy coding!]

We hope you enjoy updating and using this library! Your contributions are invaluable to the success of the MTN MoMo Android SDK, and we look forward to seeing your improvements!

:::