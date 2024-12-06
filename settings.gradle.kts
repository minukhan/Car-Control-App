pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral() // 카카오 SDK를 위한 Maven Central
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral() // 카카오 SDK를 위한 Maven Central
        maven("https://repository.map.naver.com/archive/maven")
    }
}

rootProject.name = "HyundaiCar"
include(":app")
