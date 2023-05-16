pluginManagement {
    repositories {
		maven {
			url = uri("https://plugins.gradle.org/m2/")
		}
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

rootProject.name = "Initiative Tracker"

include(":app")
include(":backend")
include(":commands")
include(":kmpsharedmodule")
include(":desktop")
