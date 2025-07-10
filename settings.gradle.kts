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

        // ✅ ADD THIS LINE FOR MPAndroidChart via JitPack:
        maven(url = "https://jitpack.io")

        maven(url = "https://maven.spotify.com/artifactory/public/") // ✅ This line is crucial!

    }
}

rootProject.name = "MoodyMusic"
include(":app")
 