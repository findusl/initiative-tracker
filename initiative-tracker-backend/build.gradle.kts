@file:Suppress("PropertyName")

import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
	application
	kotlin("jvm") version "1.7.10"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
	id("com.google.cloud.tools.appengine") version "2.4.2"
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.lehrbaum"
version = "1"

application {
	mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
	mavenCentral()
}

configure<AppEngineAppYamlExtension> {
	val projectVersion = project.version.toString()
	val projectName = project.name
	stage {
		setArtifact("build/libs/$projectName-${project.version}-all.jar")
	}
	deploy {
		projectId = "GCLOUD_CONFIG"
		version = projectVersion
		stopPreviousVersion = true
		promote = true
	}
}

dependencies {
	implementation("io.ktor:ktor-server-core:$ktor_version")
	implementation("io.ktor:ktor-server-netty:$ktor_version")
	implementation("io.ktor:ktor-server-call-logging:$ktor_version")
	implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
	implementation("io.ktor:ktor-server-websockets:$ktor_version")
	implementation("ch.qos.logback:logback-classic:$logback_version")
	testImplementation("io.ktor:ktor-server-tests:$ktor_version")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
