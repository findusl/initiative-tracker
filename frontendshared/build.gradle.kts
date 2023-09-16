import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.*

plugins {
	kotlin("multiplatform")
	id("com.android.library")
	id("org.jetbrains.compose")
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
	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "shared"
			isStatic = true
		}
	}

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(project(path = ":dtos"))

				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material)
				implementation(compose.runtime)

				implementation("com.russhwolf:multiplatform-settings:${Version.mppSettings}")
				implementation("com.russhwolf:multiplatform-settings-serialization:${Version.mppSettings}")

				implementation("com.aallam.openai:openai-client:${Version.openAiClient}")

				implementation("media.kamel:kamel-image:${Version.kamel}")

				implementation(Dependency.kotlinxSerialization)
				implementation(Dependency.kotlinxCoroutines)
				implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:${Version.kotlinxCollections}")

				implementation("io.ktor:ktor-client-core:${Version.ktor}")
				implementation("io.ktor:ktor-client-serialization:${Version.ktor}")
				implementation("io.ktor:ktor-client-content-negotiation:${Version.ktor}")
				implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.ktor}")
				implementation("io.ktor:ktor-client-websockets:${Version.ktor}")
				implementation("io.ktor:ktor-client-logging:${Version.ktor}")

				// Multiplatform Logging
				api(Dependency.napier)
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		val jvmMain by creating {
			dependsOn(commonMain)
			dependencies {
				implementation("io.ktor:ktor-client-okhttp:${Version.ktor}")
			}
		}
		val desktopMain by getting {
			dependsOn(jvmMain)
			dependencies {
				implementation(compose.desktop.common)
			}
		}
		val androidMain by getting {
			dependsOn(jvmMain)
			dependencies {
				// Android gradle module wants to have this on class path otherwise it complains
				api("androidx.activity:activity-compose")

				// Multiplatform logging
				implementation(Dependency.napier)

				// To have Dispatchers.Main on Android
				runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}")
			}
		}
		val androidUnitTest by getting
		val iosX64Main by getting
		val iosArm64Main by getting
		val iosSimulatorArm64Main by getting
		val iosMain by creating {
			dependsOn(commonMain)
			iosX64Main.dependsOn(this)
			iosArm64Main.dependsOn(this)
			iosSimulatorArm64Main.dependsOn(this)
			dependencies {
				implementation("io.ktor:ktor-client-darwin:${Version.ktor}")
			}
		}
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

	defaultConfigs {
		val openaiApiKey = localProperties.getProperty("openai.api.key", null) ?: null
		buildConfigField(STRING, "openaiApiKey", openaiApiKey, nullable = true)
	}

	defaultConfigs("lan") {
		buildConfigField(STRING, "environment", "lan")

		val host = localProperties.getProperty("backend.lan.host", "\"localhost\"")
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
	compileSdk = 34
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
