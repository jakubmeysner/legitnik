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
                credentials {
                    val githubUsername: String? by settings
                    val githubPassword: String? by settings

                    username = githubUsername ?: System.getenv("GITHUB_USER")
                    password = githubPassword ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

rootProject.name = "Legitnik"

include(":mobile")
include(":wear")
