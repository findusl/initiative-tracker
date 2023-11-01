plugins {
	kotlin("multiplatform")
	id("org.jetbrains.kotlin.plugin.serialization")
}

group = "de.lehrbaum"
version = "2.0.0"

kotlin {
	jvm()
	linuxX64 {
		binaries {
			executable { entryPoint = "de.lehrbaum.initiativetracker.backend.main" }
		}
	}

	applyDefaultHierarchyTemplate()

	sourceSets {
		named("commonMain") {
			dependencies {
				implementation(project(path = ":dtos"))

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

tasks.register<Exec>("publishNativeImageToLocalRegistry") {
	group = "docker"
	description = "Build the native binaries and publish them as an image to the local registry"
	dependsOn("linuxX64Binaries")

	commandLine(
		"docker",
		"build",
		"-t",
		"$fullProjectName:latest",
		".",
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
		// TODO this image cannot build on other architectures like mac arm
	})
}

val fullProjectName: String
	get() = project.parent?.let { "${it.name}." } + name
