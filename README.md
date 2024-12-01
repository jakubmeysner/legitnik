# Legitnik

## Building

In order to obtain the required dependencies from the GitHub Maven repository used you'll need to
configure credentials for it in Gradle. In order to do that you'll need to:

1. Create a GitHub Personal Access Token
    1. Open https://github.com/settings/tokens?type=beta
    2. Generate a new token with read-only public repositories access
2. Configure your credentials
    1. Select a [way to set the Gradle properties](https://github.com/settings/tokens?type=beta)
       (e.g. modifying `~/.gradle/gradle.properties`)
    2. Set the `githubUsername` property to your GitHub username (`githubUsername=YOUR_USERNAME`)
    3. Set the `githubPassword` property to the generated PAT (`githubPassword=GENERATED_PAT`)
