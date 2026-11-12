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
    }
}

rootProject.name = "HowDoILook"

include(":app")
include(":core:domain")
include(":core:data")
include(":core:ads")
include(":core:ui")
include(":feature:rating")
include(":feature:history")
include(":feature:home")
include(":feature:settings")
