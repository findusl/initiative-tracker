pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
		eachPlugin {
			if (requested.id.id.startsWith("com.google.cloud.tools.appengine")) {
				useModule("com.google.cloud.tools:appengine-gradle-plugin:${requested.version}")
			}
		}
	}
}

rootProject.name = "initiative-tracker"

include(":frontendandroid")
include(":frontendshared")
include(":frontenddesktop")
include(":dtos")
include(":backendshared")
include(":backendjvm")
