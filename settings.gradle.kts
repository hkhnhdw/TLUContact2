pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.8.0" apply false
        id("org.jetbrains.kotlin.android") version "2.1.0" apply false
        id("com.google.gms.google-services") version "4.4.2" apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TLUContact"
include(":app")
