import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol
import io.ktor.plugin.features.JreVersion

plugins {
	application
	kotlin("jvm")
	id("org.jetbrains.kotlin.plugin.serialization")
	id("io.ktor.plugin") version Version.ktor
}

group = "de.lehrbaum"
version = "2.0.0"

application {
	mainClass.set("de.lehrbaum.ApplicationKt")
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

dependencies {
	implementation(project(path = ":commands"))

	implementation("io.ktor:ktor-server-core:${Version.ktor}")
	implementation("io.ktor:ktor-server-cio:${Version.ktor}")
	implementation("io.ktor:ktor-server-call-logging:${Version.ktor}")
	implementation("io.ktor:ktor-server-content-negotiation:${Version.ktor}")
	implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.ktor}")
	implementation("io.ktor:ktor-server-websockets:${Version.ktor}")
	implementation("ch.qos.logback:logback-classic:${Version.logback}")

	testImplementation("io.ktor:ktor-server-tests:${Version.ktor}")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${Version.kotlin}")
	testImplementation(kotlin("test"))
	testImplementation("org.mockito:mockito-core:${Version.mockito}")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
}

tasks.test {
	useJUnitPlatform()
}

tasks.register<Copy>("buildAndCopyImage") {
	group = "distribution"
	description = "Custom task for my particular backend deployment"
	dependsOn("buildImage")

	from("$buildDir/jib-image.tar")
	into("/Volumes/hspeed/docker_images")
}
