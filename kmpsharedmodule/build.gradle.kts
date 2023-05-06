import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.*

plugins {
	kotlin("multiplatform")
	id("com.android.library")
	id("org.jetbrains.compose") version Version.JetbrainsCompose.foundation
	id("org.jetbrains.kotlin.plugin.serialization")
	// Used to mimic BuildConfig on Multiplatform
	id("com.codingfeline.buildkonfig") version Version.buildKonfig
}

kotlin {
	jvm("desktop")
	android {
		compilations.all {
			kotlinOptions {
				jvmTarget = "17"
			}
		}
	}

	sourceSets {
		named("commonMain") {
			dependencies {
				implementation(project(path = ":commands"))

				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material)
				implementation(compose.runtime)
				implementation(compose.preview)

				implementation("com.russhwolf:multiplatform-settings:${Version.mppSettings}")
				implementation("com.russhwolf:multiplatform-settings-serialization:${Version.mppSettings}")

				implementation(Dependency.kotlinxSerialization)
				implementation(Dependency.kotlinxCoroutines)

				implementation("io.ktor:ktor-client-core:${Version.ktor}")
				implementation("io.ktor:ktor-client-serialization:${Version.ktor}")
				implementation("io.ktor:ktor-client-content-negotiation:${Version.ktor}")
				implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.ktor}")
				implementation("io.ktor:ktor-client-websockets:${Version.ktor}")
				implementation("io.ktor:ktor-client-logging-jvm:${Version.ktor}")

				// Multiplatform Logging
				api(Dependency.napier)
			}
		}
		named("commonTest") {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		named("desktopMain") {
			dependencies {
				implementation(compose.desktop.common)
				implementation("io.ktor:ktor-client-okhttp:${Version.ktor}")
			}
		}
		named("androidMain") {
			dependencies {
				implementation("io.ktor:ktor-client-okhttp:${Version.ktor}")
			}
		}
		named("androidUnitTest")
	}
}

// part of BuildKonfig plugin
buildkonfig {
	val localProperties = Properties()
	val localPropertiesFile: File = project.rootProject.file("local.properties")
	if (localPropertiesFile.exists()) {
		localPropertiesFile.inputStream().use {
			localProperties.load(it)
		}
	}

	packageName = "de.lehrbaum.initiativetracker"
	exposeObjectWithName = "BuildKonfig"

	defaultConfigs {}

	defaultConfigs("lan") {
		buildConfigField(STRING, "environment", "lan")

		val host = localProperties.getProperty("backend.lan.host", "\"10.0.2.2\"")
		val port = localProperties.getProperty("backend.lan.port", "8080")
		buildConfigField(STRING, "backendHost", host)
		buildConfigField(INT, "backendPort", port)
	}

	defaultConfigs("remote") {
		buildConfigField(STRING, "environment", "remote")

		val host = localProperties.getProperty("backend.remote.host", "\"undefined\"")
		val port = localProperties.getProperty("backend.remote.port", "443")
		buildConfigField(STRING, "backendHost", host)
		buildConfigField(INT, "backendPort", port)
	}
}

android {
	namespace = "de.lehrbaum.initiativetracker"
	compileSdk = 33
	defaultConfig {
		minSdk = 28
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}

fun org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions.enableContextReceivers() {
	freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
}
