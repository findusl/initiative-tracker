plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
}

kotlin {
	jvmToolchain(17)
	jvm()
	linuxX64()
	iosX64()
	iosArm64()
	iosSimulatorArm64()
	sourceSets {
		named("commonMain") {
			dependencies {
				api(libs.kotlinx.serialization.core)
				api(libs.kotlinx.serialization.json)
			}
		}
	}
}
