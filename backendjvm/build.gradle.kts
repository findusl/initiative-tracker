import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol

plugins {
	application
	kotlin("jvm")
	kotlin("plugin.serialization")
	alias(libs.plugins.ktor)
}

application {
	mainClass.set("de.lehrbaum.initiativetracker.backend.MainKt")
	version = libs.versions.backendVersion.get()
}

ktor {
	docker {
		localImageName.set("initiative-tracker")
		portMappings.set(
			listOf(
				DockerPortMapping(
					5009,
					8080,
					DockerPortMappingProtocol.TCP,
				),
			),
		)
	}
}

dependencies {
	implementation(project(path = ":backendshared"))
}
