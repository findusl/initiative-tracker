import Constants.kotlinxSerializationVersion

plugins {
	`java-library`
	kotlin("jvm")
	id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
	api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
	api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}
