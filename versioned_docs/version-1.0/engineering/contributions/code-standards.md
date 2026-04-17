---
sidebar_position: 2
sidebar_label: Code Standards
---

# Code Standards

Establishing clear code standards is essential for maintaining a high-quality codebase that is easy to read, understand, and maintain. These standards help ensure consistency across contributions, making it easier for all developers to collaborate effectively. Below are the key areas of focus for our code standards.

---

## Naming Conventions

Naming conventions play a crucial role in code readability and maintainability. By adhering to consistent naming practices, we can make our code more intuitive and easier to navigate.

- When using abbreviations in CamelCase, keep the first letter capitalized and lowercase the remaining letters. For example:
    - A variable name representing "MTN Momo" should be written as `mtnMomo`.
    - A class name representing "Account Balance" should be written as `accountBalance`.

### Branch Naming

When creating a new branch, it is important to follow a clear naming convention to facilitate organization and tracking of changes. The recommended format is:

```vim
issue-number-feature-name
```

For example, if you are addressing issue number 27 and your feature involves adding documentation, you would name your branch as follows:

```vim
27-adding-documentation
```

This format helps in easily identifying the purpose of the branch and its associated issue.

## Commits

### Atomic Commits

We strongly encourage our contributors to create atomic pull requests (PRs) and commits. An atomic commit focuses on a single, specific change, which significantly enhances the clarity and maintainability of the codebase. Here are several reasons why atomic commits are particularly valuable:

* **Clarity and Readability:** Each commit represents a focused change, making it clear what was modified and why. This improves code readability and allows reviewers to quickly grasp the purpose of each change.
* **Easier Debugging:** If issues arise, atomic commits allow for pinpointing exactly when and where a bug was introduced. This is especially useful in debugging scenarios and when using tools like `git bisect` to trace problematic changes.
* **Simplifies Code Reviews:** Smaller, focused changes make it easier for reviewers to assess the impact of each commit without being overwhelmed by unrelated modifications. This leads to more effective and efficient reviews.
* **Better History Tracking:** Atomic commits create a clean, concise commit history, making it easier to follow the project’s progression over time and understand the rationale behind each change.
* **Facilitates Rollbacks:** In cases where a change needs to be reverted, atomic commits allow for rolling back individual changes without affecting other, unrelated parts of the code.

:::danger

Please note that non-atomic commits may be subject to change requests during the review process.

:::

### Commit Messages

Writing clear and informative commit messages is essential for maintaining a comprehensible project history. Here are some guidelines to follow when crafting your commit messages:

1. Separate the subject/title from the body with a blank line.
2. Limit the subject line to 50 characters to ensure it is concise and easily readable.
3. Capitalize the subject line/title to maintain consistency and professionalism.
4. Do not end the subject line with a period to keep it clean and straightforward.
5. Use hyphens at the beginning of the commit messages in the body to enhance readability.
6. Employ the imperative mood in the commit messages, as if you are giving commands (e.g., "Add feature" instead of "Added feature").
7. Wrap the body of the message at 72 characters to ensure it displays well in various interfaces.
8. Use the body to explain what changes were made and why they were necessary, rather than detailing how the changes were implemented.

For a deeper understanding of these guidelines, please refer to [this article by Chris Beams](https://cbea.ms/git-commit/).

**Sample Commit Message:**
```vim
Update Library Documentation    

- Add and enable Docusaurus
- Add and enable Kdocs
```

## Pull Requests

### Describe Your Pull Request

When submitting a pull request, it is important to provide a comprehensive description of your changes. Follow these steps to ensure clarity:

1. Use the format specified in the pull request template for the repository. Populate the template completely for maximum verbosity and clarity.
   * Tag the actual issue number by replacing #[issue_number] (e.g., #42). This will automatically close the issue when your PR is merged.
   * Tag the actual issue author by replacing @[author] (e.g., @issue_author). This brings the reporter of the issue into the conversation, fostering collaboration.
   * Mark the tasks off your checklist by adding an "x" in the [ ] (e.g., [x]). This checks off the boxes in your to-do list. The more boxes you check, the better.
2. Describe your change in detail. Providing too much detail is better than too little, as it helps reviewers understand the context and purpose of your changes.
3. Explain how you tested your change to demonstrate its effectiveness and reliability.
4. Check the Preview tab to ensure that the Markdown is correctly rendered and that all tags and references are linked appropriately. If not, go back and edit the Markdown accordingly.

For more information, please view the [Pull Request Guidelines](https://opensource.creativecommons.org/contributing-code/pr-guidelines/).

### Request Review

Once your pull request is ready for review, follow these steps:

1. Remove the "[WIP]" (Work In Progress) from the title and/or change it from a draft PR to a regular PR to indicate that it is ready for review.
2. If a specific reviewer is not assigned automatically, please [request a review](https://help.github.com/en/articles/requesting-a-pull-request-review) from the project maintainer and any other interested parties manually.

### Incorporating Feedback

Receiving feedback is an essential part of the development process. Here’s how to effectively incorporate feedback into your pull request:

1. If your PR receives a 'Changes requested' review, address the feedback and update your PR by pushing to the same branch. There is no need to close the PR and open a new one.
2. Be sure to re-request a review once you have made changes after a code review. This signals to reviewers that you have addressed their comments and are awaiting their feedback.
3. Asking for a re-review makes it clear that you have addressed the requested changes and that the PR is now waiting on the maintainers instead of the other way around.

:::danger

Please be aware that non-atomic pull requests may also be subject to change requests during the review process.

:::