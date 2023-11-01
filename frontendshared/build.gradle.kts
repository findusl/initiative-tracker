import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.*

plugins {
	kotlin("multiplatform")
	id("com.android.library")
	id("org.jetbrains.compose")
	id("org.jetbrains.kotlin.plugin.serialization")
	// Used to mimic BuildConfig from Android on Multiplatform
	id("com.codingfeline.buildkonfig") version Version.buildKonfig
}

kotlin {
	jvm()
	androidTarget {
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
			binaryOption("bundleId", "de.lehrbaum.initiativetracker")
		}
	}

	applyDefaultHierarchyTemplate()

	sourceSets {
		commonMain.dependencies {
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
		commonTest.dependencies {
			implementation(kotlin("test"))
		}
		// All jvm targets: Android and Jvm desktop
		val jvmTargetsMain by creating {
			dependsOn(commonMain.get())
			jvmMain.get().dependsOn(this)
			androidMain.get().dependsOn(this)
			dependencies {
				implementation("io.ktor:ktor-client-okhttp:${Version.ktor}")
			}
		}
		// Jvm non android
		jvmMain.dependencies {
			implementation(compose.desktop.common)
		}
		androidMain.dependencies {
			// Android gradle module wants to have this on class path otherwise it complains
			api("androidx.activity:activity-compose")

			// Multiplatform logging
			implementation(Dependency.napier)

			// To have Dispatchers.Main on Android
			runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}")
		}
		appleMain.dependencies {
			implementation("io.ktor:ktor-client-darwin:${Version.ktor}")
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
	// TODO Might not need the rest anymore
	compileSdk = 34
	defaultConfig {
		minSdk = 28
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}
