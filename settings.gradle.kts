pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }

        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

        repositories {
            maven("https://maven.pkg.github.com/signerry/packages") {
                name = "githubSignerry"
                credentials(PasswordCredentials::class)
            }
        }
    }
}

rootProject.name = "Legitnik"

include(":mobile")
include(":wear")
