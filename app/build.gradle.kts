@file:Suppress("UnstableApiUsage") // the warning doesn't help

import Constants.hiltVersion
import Constants.kotlinxSerializationVersion
import Constants.ktorVersion
import Constants.navigationVersion
import java.util.*

buildscript {
	@Suppress("RemoveRedundantQualifierName") // Imports do not seem to apply in the buildscript block
	dependencies {
		classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Constants.navigationVersion}")
		classpath("com.android.tools.build:gradle:7.4.2")
	}
}

@Suppress("RemoveRedundantQualifierName") // Imports do not seem to apply in the plugins block
plugins {
	id("com.android.application") version "7.4.2"
	id("org.jetbrains.kotlin.android") version Constants.kotlinVersion
	id("org.jetbrains.kotlin.kapt") version Constants.kotlinVersion
	id("org.jetbrains.kotlin.plugin.serialization") version Constants.kotlinVersion
	id("androidx.navigation.safeargs.kotlin") version Constants.navigationVersion
	id("com.google.dagger.hilt.android") version Constants.hiltVersion
}

val properties = Properties()
val localProperties = project.rootProject.file("local.properties")
if (localProperties.exists()) {
	localProperties.inputStream().use {
		properties.load(it)
	}
}

android {
	compileSdk = 33

	defaultConfig {
		applicationId = "de.lehrbaum.initiativetracker"
		minSdk = 28
		targetSdk = 33
		versionCode = 1
		versionName = "0.1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		getByName("release") {
			isMinifyEnabled = true
			// proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
			isDebuggable = false
		}
	}

	flavorDimensions.add("backend")
	productFlavors {
		create("lan") {
			// Assigns this product flavor to the "version" flavor dimension.
			// If you are using only one dimension, this property is optional,
			// and the plugin automatically assigns all the module"s flavors to
			// that dimension.
			dimension = "backend"
			applicationIdSuffix = ".lan"
			versionNameSuffix = "-lan"

			val host = properties.getProperty("backend.lan.host", "\"10.0.2.2\"")
			val port = properties.getProperty("backend.lan.port", "8080")
			buildConfigField("String", "BACKEND_HOST", host)
			buildConfigField("int", "BACKEND_PORT", port)
		}
		create("remote") {
			dimension = "backend"
			applicationIdSuffix = ".remote"
			versionNameSuffix = "-remote"

			val host = properties.getProperty("backend.lan.host", "\"undefined\"")
			val port = properties.getProperty("backend.lan.port", "443")
			buildConfigField("String", "BACKEND_HOST", host)
			buildConfigField("int", "BACKEND_PORT", port)
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	buildFeatures {
		dataBinding = true
		viewBinding = true
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.4.3"
	}
	kotlinOptions {
		freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
		jvmTarget = "1.8"
	}
	namespace = "de.lehrbaum.initiativetracker"
}

dependencies {
	implementation(project(path = ":commands"))

	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("androidx.constraintlayout:constraintlayout:2.1.4")
	implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
	implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
	implementation("androidx.legacy:legacy-support-v4:1.0.0")
	implementation("androidx.recyclerview:recyclerview:1.2.1")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

	implementation("com.google.android.material:material:1.8.0")

	// Kotlinx Serialization
	api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
	api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
	api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

	// Ktor dependencies
	api("io.ktor:ktor-client-core:$ktorVersion")
	api("io.ktor:ktor-client-serialization:$ktorVersion")
	api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
	implementation("io.ktor:ktor-client-websockets:$ktorVersion")
	implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
	implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

	implementation("com.google.dagger:hilt-android:$hiltVersion")
	implementation("androidx.core:core-ktx:1.9.0")
	kapt("com.google.dagger:hilt-compiler:$hiltVersion")

	// Napier allows us to easily log on Kotlin Multiplatform in the future
	implementation("io.github.aakira:napier:2.6.1")

	// Compose dependencies
	implementation("androidx.compose.ui:ui:1.3.3")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-rc01")
	implementation("androidx.compose.material:material:1.3.1")
	debugImplementation("androidx.compose.ui:ui-tooling:1.4.0-beta02")
	implementation("androidx.compose.ui:ui-tooling-preview:1.4.0-beta02")

	testImplementation("junit:junit:4.13.2")

	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Allow references to generated code
kapt {
	correctErrorTypes = true
}