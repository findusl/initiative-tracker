plugins {
	kotlin("multiplatform")
	id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
	jvm()
	sourceSets {
		named("commonMain") {
			dependencies {
				api("org.jetbrains.kotlinx:kotlinx-serialization-core:${Version.kotlinxSerialization}")
				api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.kotlinxSerialization}")
			}
		}
	}
}
