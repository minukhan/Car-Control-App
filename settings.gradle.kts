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
        maven(url = uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/"))
    }
}

rootProject.name = "HyundaiCar"
include(":app")
