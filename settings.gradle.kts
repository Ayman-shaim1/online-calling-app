import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://raw.githubusercontent.com/alexgreench/google-webrtc/master") }
        maven { url = URI("https://jitpack.io") }
    }
}

rootProject.name = "WebRTC Sample App"
include(":app")
