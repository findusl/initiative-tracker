plugins {
	kotlin("multiplatform")
	id("com.android.library")
	id("org.jetbrains.compose") version 1.3.1
	id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
	android {
		compilations.all {
			kotlinOptions {
				jvmTarget = "11"
			}
		}
	}

	@Suppress("UNUSED_VARIABLE")
	sourceSets {
		named("commonMain") {
			dependencies {
				implementation(project(path = ":commands"))
				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material)
				implementation(compose.runtime)
				implementation(compose.preview)
			}
		}
		named("commonTest") {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		named("androidMain")
		named("androidUnitTest")
	}
}

android {
	namespace = "de.lehrbaum.initiativetracker"
	compileSdk = 33
	defaultConfig {
		minSdk = 28
	}
}