@file:Suppress("OPT_IN_USAGE", "UnstableApiUsage")

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.kotlin.plugin.serialization")
}

group = "de.lehrbaum"
version = "2.0.0"

kotlin {
	jvm() {
		mainRun {
			mainClass = "de.lehrbaum.MainKt"
		}
	}
	linuxX64 {
		binaries {
			executable { entryPoint = "de.lehrbaum.main" }
		}
	}

	sourceSets {
		named("commonMain") {
			dependencies {
				implementation(project(path = ":commands"))

				implementation("io.ktor:ktor-server-core:${Version.ktor}")
				implementation("io.ktor:ktor-server-cio:${Version.ktor}")
				implementation("io.ktor:ktor-server-content-negotiation:${Version.ktor}")
				implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.ktor}")
				implementation("io.ktor:ktor-server-websockets:${Version.ktor}")
			}
		}
		named("commonTest") {
			dependencies {
				implementation("io.ktor:ktor-server-tests:${Version.ktor}")
				implementation(kotlin("test"))
			}
		}
		named("jvmMain") {
			dependencies {
				implementation("ch.qos.logback:logback-classic:${Version.logback}")
				implementation("io.ktor:ktor-server-call-logging:${Version.ktor}")
			}
		}
		named("linuxX64Main")
	}
}

val fullProjectName: String
	get() = project.parent?.let { "${it.name}." } + name

tasks.register<Exec>("publishNativeImageToLocalRegistry") {
	group = "docker"
	description = "Build the native binaries and publish them as an image to the local registry"
	dependsOn("linuxX64Binaries")

	commandLine(
		"docker",
		"build",
		"-t",
		"$fullProjectName:latest",
		".",// TODO this image is not multiplatform so I cannot use it
	)
}

tasks.register<Exec>("buildNativeImageToTarFile") {
	group = "docker"
	description = "Save the Docker image as a tar file."
	dependsOn("publishNativeImageToLocalRegistry")

	commandLine(buildList {// different approach of building the command
		add("docker")
		add("save")
		add("-o")
		add("$fullProjectName.tar")
		add("$fullProjectName:latest")
	})
}
