@file:Suppress("UnstableApiUsage") // the warning doesn't help

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.util.*

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
}

val properties = Properties()
val localProperties: File = project.rootProject.file("local.properties")
if (localProperties.exists()) {
	localProperties.inputStream().use {
		properties.load(it)
	}
}

kotlin {
	jvmToolchain(17)
}

android {
	compileSdk = 34

	defaultConfig {
		applicationId = "de.lehrbaum.initiativetracker"
		minSdk = 28
		targetSdk = 34
		versionCode = 2
		versionName = "1.0.1"

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
		kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
	}
	namespace = "de.lehrbaum.initiativetracker"
}

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

fun KotlinJvmOptions.enableContextReceivers() {
	freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
}
