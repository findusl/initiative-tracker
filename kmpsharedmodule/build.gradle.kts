plugins {
	kotlin("multiplatform")
	id("com.android.library")
	id("org.jetbrains.compose") version Version.JetbrainsCompose.foundation
	id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
	android {
		compilations.all {
			kotlinOptions {
				jvmTarget = "17"
			}
		}
	}

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
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}