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

include(":app")
include(":backend")
include(":commands")
include(":kmpsharedmodule")
include(":desktop")
