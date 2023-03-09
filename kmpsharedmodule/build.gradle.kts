plugins {
	kotlin("multiplatform")
	id("com.android.library")
}

kotlin {
	android {
		compilations.all {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(project(path = ":commands"))
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		val androidMain by getting
		val androidUnitTest by getting
	}
}

android {
	namespace = "de.lehrbaum.initiativetracker.kmpsharedmodule"
	compileSdk = 33
	defaultConfig {
		minSdk = 28
	}
}