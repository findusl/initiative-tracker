import Constants.kotlinVersion
import Constants.ktorVersion
import Constants.logbackVersion
import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension
import io.ktor.plugin.features.DockerImageRegistry
import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol
import io.ktor.plugin.features.JreVersion

@Suppress("RemoveRedundantQualifierName") // Imports do not seem to apply in the plugins block
plugins {
	application
	kotlin("jvm")
	id("io.ktor.plugin") version "2.1.3"
	id("org.jetbrains.kotlin.plugin.serialization")
	id("com.google.cloud.tools.appengine") version "2.4.2"
	id("com.github.johnrengelman.shadow") version "7.1.2"
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

	implementation("io.ktor:ktor-server-core:$ktorVersion")
	implementation("io.ktor:ktor-server-netty:$ktorVersion")
	implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
	implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
	implementation("io.ktor:ktor-server-websockets:$ktorVersion")
	implementation("ch.qos.logback:logback-classic:$logbackVersion")

	testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${kotlinVersion}")
	testImplementation(kotlin("test"))
	testImplementation("org.mockito:mockito-core:4.11.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
}

tasks.test {
	useJUnitPlatform()
}
