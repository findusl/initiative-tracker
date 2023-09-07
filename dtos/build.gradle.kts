plugins {
	kotlin("multiplatform")
	id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
	jvm()
	linuxX64()
	iosX64()
	iosArm64()
	iosSimulatorArm64()
	sourceSets {
		named("commonMain") {
			dependencies {
				api("org.jetbrains.kotlinx:kotlinx-serialization-core:${Version.kotlinxSerialization}")
				api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.kotlinxSerialization}")
			}
		}
	}
}
