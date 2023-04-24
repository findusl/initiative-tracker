import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension
import io.ktor.plugin.features.DockerImageRegistry
import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol
import io.ktor.plugin.features.JreVersion

plugins {
	application
	kotlin("jvm")
	id("org.jetbrains.kotlin.plugin.serialization")
	id("io.ktor.plugin") version "2.2.4"
	id("com.google.cloud.tools.appengine") version "2.4.5"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.lehrbaum"
version = "2.0.0"

application {
	mainClass.set("io.ktor.server.netty.EngineMain")
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
		externalRegistry.set(
			// this does not yet seem to work
			DockerImageRegistry.dockerHub(
				appName = providers.gradleProperty("dockerhub.app.name"),
				username = providers.gradleProperty("dockerhub.username"),
				password = providers.gradleProperty("dockerhub.password")
			)
		)
	}
}

dependencies {
	implementation(project(path = ":commands"))

	implementation("io.ktor:ktor-server-core:${Version.ktor}")
	implementation("io.ktor:ktor-server-netty:${Version.ktor}")
	implementation("io.ktor:ktor-server-call-logging:${Version.ktor}")
	implementation("io.ktor:ktor-server-content-negotiation:${Version.ktor}")
	implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.ktor}")
	implementation("io.ktor:ktor-server-websockets:${Version.ktor}")
	implementation("ch.qos.logback:logback-classic:${Version.logback}")

	testImplementation("io.ktor:ktor-server-tests:${Version.ktor}")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${Version.kotlin}")
	testImplementation(kotlin("test"))
	testImplementation("org.mockito:mockito-core:5.3.1")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
}

tasks.test {
	useJUnitPlatform()
}
