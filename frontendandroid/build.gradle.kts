@file:Suppress("UnstableApiUsage") // the warning doesn't help

import java.util.Properties

buildscript {
	dependencies {
		classpath(libs.android.gradle)
	}
}

plugins {
	id("com.android.application")
	kotlin("android")
	id("org.jetbrains.kotlin.kapt")
	kotlin("plugin.serialization")
	alias(libs.plugins.compose.compiler)
}

val properties = Properties()
val localProperties: File = project.rootProject.file("local.properties")
if (localProperties.exists()) {
	localProperties.inputStream().use {
		properties.load(it)
	}
}

kotlin {
	jvmToolchain(21)
}

android {
	compileSdk = 35

	defaultConfig {
		applicationId = "de.lehrbaum.initiativetracker.remote"
		minSdk = 28
		targetSdk = 35
		versionCode = 4
		version = libs.versions.frontendVersion.get()

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		getByName("release") {
			isMinifyEnabled = true
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
			isDebuggable = false
		}
	}


	buildFeatures {
		dataBinding = true
		viewBinding = true
		compose = true
		buildConfig = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.14"
	}
	namespace = "de.lehrbaum.initiativetracker"
	packaging {
		resources.excludes.add("META-INF/INDEX.LIST")
	}
}

//noinspection UseTomlInstead Cannot use version catalog without version, due to compose bom
dependencies {
	implementation(project(":frontendshared"))
	implementation(project(":dtos"))

	// Compose versions
	val composeBom = platform(libs.androidx.compose.bom)
	implementation(composeBom)
	androidTestImplementation(composeBom)

	testImplementation(libs.junit)

	// For previews
	androidTestImplementation("androidx.compose.material:material")
	debugImplementation("androidx.compose.ui:ui-tooling")
	androidTestImplementation("androidx.compose.ui:ui-tooling-preview")

	androidTestImplementation(libs.ext.junit)
	androidTestImplementation(libs.espresso.core)
}

// Allow references to generated code
kapt {
	correctErrorTypes = true
}
