pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
	}
}

rootProject.name = "initiative-tracker"

include(":frontendandroid")
include(":frontendshared")
include(":frontenddesktop")
include(":dtos")
include(":backendshared")
include(":backendjvm")
