val kotlinxSerializationVersion: String by parent!!.ext

plugins {
	`java-library`
	kotlin("jvm") version "1.7.20"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
}

dependencies {
	api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
	api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_7
	targetCompatibility = JavaVersion.VERSION_1_7
}
