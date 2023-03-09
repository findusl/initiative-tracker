plugins {
	`java-library`
	kotlin("jvm")
	id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
	api("org.jetbrains.kotlinx:kotlinx-serialization-core:${Version.kotlinxSerialization}")
	api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.kotlinxSerialization}")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}
