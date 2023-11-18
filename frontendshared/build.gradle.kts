import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.*
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
				implementation("io.ktor:ktor-client-okhttp:${Version.ktor}")
			}
		}
		val jvmTargetsTest by creating {
			dependsOn(commonTest.get())
			jvmTest.get().dependsOn(this)
			androidUnitTest.dependsOn(this)
			dependencies {
				implementation("io.mockk:mockk:${Version.mockk}")
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
				implementation(Dependency.napier)

				// To have Dispatchers.Main on Android
				runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}")
			}
		}
		appleMain {
			dependencies {
				implementation("io.ktor:ktor-client-darwin:${Version.ktor}")
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
