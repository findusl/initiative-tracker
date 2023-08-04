@file:Suppress("OPT_IN_USAGE", "UnstableApiUsage")

import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol
import io.ktor.plugin.features.JreVersion

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.kotlin.plugin.serialization")
	id("io.ktor.plugin") version Version.ktor
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
			project.sourceSets.main.get().java.srcDirs.addAll(kotlin.srcDirs)
			println("Applied srcDirs ${project.sourceSets.main.get().java.srcDirs}")
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
			project.sourceSets.main.get().java.srcDirs.addAll(kotlin.srcDirs)
		}
		named("linuxX64Main")
	}
}

sourceSets {
	main {
		println("classpath ${this.compileClasspath}")
		println("Java sources ${java.sourceDirectories}")
		println("Java srcdirs ${java.srcDirs.map { it.absolutePath }.reduce(String::plus)}")
	}
}

ktor {
	docker {
		jreVersion.set(JreVersion.JRE_17)
		localImageName.set("initiative-tracker")
		portMappings.set(
			listOf(
				DockerPortMapping(
					5009,
					8080,
					DockerPortMappingProtocol.TCP
				)
			)
		)
	}
}

tasks.register<Copy>("buildAndCopyImage") {
	group = "docker"
	description = "Custom task for my particular backend deployment"
	dependsOn("buildImage")

	from("$buildDir/jib-image.tar")
	into("/Volumes/hspeed/docker_images")
}
