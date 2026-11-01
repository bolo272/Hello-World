pluginManagement {
    repositories {
        google()
        // Lustro Maven Central od Google: bywa szybsze i nie zwraca "429 Too Many Requests".
        maven("https://maven-central.storage-download.googleapis.com/maven2/")
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        // Lustro Maven Central od Google: bywa szybsze i nie zwraca "429 Too Many Requests".
        maven("https://maven-central.storage-download.googleapis.com/maven2/")
        mavenCentral()
    }
}
rootProject.name = "KocieLawki"
include(":app")
