pluginManagement {
	repositories {
		gradlePluginPortal()
		google()
		mavenCentral()
	}
}
plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "initiative-tracker"

include(":frontendandroid")
include(":frontendshared")
include(":frontenddesktop")
include(":dtos")
include(":backendshared")
include(":backendjvm")
