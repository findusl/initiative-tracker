plugins {
	kotlin("multiplatform")
	kotlin("plugin.serialization")
}

group = "de.lehrbaum"
version = libs.versions.backendVersion.get()

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

				implementation(libs.ktor.server.core)
				implementation(libs.ktor.server.cio)
				implementation(libs.ktor.server.content.negotiation)
				implementation(libs.ktor.serialization.kotlinx.json)
				implementation(libs.ktor.server.websockets)
			}
		}
		named("commonTest") {
			dependencies {
				implementation(libs.ktor.server.tests)
				implementation(kotlin("test"))
			}
		}
		named("jvmMain") {
			dependencies {
				implementation(libs.logback.classic)
				implementation(libs.ktor.server.call.logging)
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

	commandLine(
		buildList {
			add("docker")
			add("save")
			add("-o")
			add("$fullProjectName.tar")
			add("$fullProjectName:latest")
			// TASK this image cannot build on other architectures like mac arm
		},
	)
}

val fullProjectName: String
	get() = project.parent?.let { "${it.name}." } + name
