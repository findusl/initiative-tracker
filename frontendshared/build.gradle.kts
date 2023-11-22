import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.*
import java.util.*

plugins {
	kotlin("multiplatform")
	id("com.android.library")
	alias(libs.plugins.jetbrains.compose)
	kotlin("plugin.serialization")
	// Used to mimic BuildConfig from Android on Multiplatform
	alias(libs.plugins.buildkonfig)
}

kotlin {
	jvmToolchain(17)
	jvm()
	androidTarget()
	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "shared"
			// Needs to contain the base bundleId
			binaryOption("bundleId", "de.lehrbaum.initiativetracker.shared")
		}
	}

	applyDefaultHierarchyTemplate()

	sourceSets {
		commonMain {
			dependencies {
				implementation(project(path = ":dtos"))

				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material)
				implementation(compose.runtime)

				implementation(libs.multiplatform.settings)
				implementation(libs.multiplatform.settings.serialization)

				implementation(libs.openai.client)

				implementation(libs.kamel.image)

				implementation(libs.kotlinx.serialization.json)
				implementation(libs.kotlinx.coroutines.core)
				implementation(libs.kotlinx.collections.immutable)

				implementation(libs.ktor.client.core)
				implementation(libs.ktor.client.serialization)
				implementation(libs.ktor.client.content.negotiation)
				implementation(libs.ktor.serialization.kotlinx.json)
				implementation(libs.io.ktor.ktor.client.websockets)
				implementation(libs.io.ktor.ktor.client.logging)

				// Multiplatform Logging
				api(libs.napier)
			}
		}
		commonTest {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		val androidUnitTest by getting {  } // not part of the accessors for some reason
		// All jvm targets, including android
		val jvmTargetsMain by creating {
			dependsOn(commonMain.get())
			jvmMain.get().dependsOn(this)
			androidMain.get().dependsOn(this)
			dependencies {
				implementation(libs.ktor.client.okhttp)
			}
		}
		val jvmTargetsTest by creating {
			dependsOn(commonTest.get())
			jvmTest.get().dependsOn(this)
			androidUnitTest.dependsOn(this)
			dependencies {
				implementation(libs.mockk)
				implementation(kotlin("test-junit"))
			}
		}
		// non-android jvm targets
		jvmMain {
			dependencies {
				implementation(compose.desktop.common)
			}
		}
		androidMain {
			dependencies {
				// Android gradle module wants to have this on class path otherwise it complains
				api("androidx.activity:activity-compose")

				// Multiplatform logging
				implementation(libs.napier)

				// To have Dispatchers.Main on Android
				runtimeOnly(libs.kotlinx.coroutines.android)
			}
		}
		appleMain {
			dependencies {
				implementation(libs.ktor.client.darwin)
			}
		}
	}
}

tasks.register("printSourceSets") {
		println("Defined source sets:")
		kotlin.sourceSets.forEach {
			println(it.name)
		}
}

// from BuildKonfig plugin to define some defaults
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

		val host = localProperties.getProperty("backend.host", "localhost")
		val port = localProperties.getProperty("backend.port", "8080")
		val secure = localProperties.getProperty("backend.secure", "false")
		buildConfigField(STRING, "backendHost", host)
		buildConfigField(INT, "backendPort", port)
		buildConfigField(BOOLEAN, "backendSecure", secure)
	}
}

android {
	namespace = "de.lehrbaum.initiativetracker"
	compileSdk = 34
	defaultConfig {
		minSdk = 28 // to avoid warnings, the actual minSdk is set in frontendandroid/build.gradle.kts
	}
}
