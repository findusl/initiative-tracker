import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol

plugins {
	application
	kotlin("jvm")
	id("org.jetbrains.kotlin.plugin.serialization")
	id("io.ktor.plugin") version Version.ktor
}

application {
	mainClass.set("de.lehrbaum.initiativetracker.backend.MainKt")
}

ktor {
	docker {
		jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
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
	implementation(project(path = ":backendshared"))
}
