@file:Suppress("UnstableApiUsage") // the warning doesn't help

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.util.*

buildscript {
	dependencies {
		classpath("com.android.tools.build:gradle:${Version.Android.gradleBuildTools}")
	}
}

plugins {
	id("com.android.application")
	kotlin("android")
	id("org.jetbrains.kotlin.kapt")
	id("org.jetbrains.kotlin.plugin.serialization")
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
		versionName = "1.0.0"

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
		kotlinCompilerExtensionVersion = Version.Android.composeCompiler
	}
	namespace = "de.lehrbaum.initiativetracker"
}

dependencies {
	implementation(project(":frontendshared"))
	implementation(project(":dtos"))

	// Compose versions
	val composeBom = platform("androidx.compose:compose-bom:${Version.Android.composeBom}")
	implementation(composeBom)

	testImplementation("junit:junit:4.13.2")

	// For previews
	androidTestImplementation("androidx.compose.material:material")
	debugImplementation("androidx.compose.ui:ui-tooling")
	androidTestImplementation("androidx.compose.ui:ui-tooling-preview")

	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Allow references to generated code
kapt {
	correctErrorTypes = true
}

fun KotlinJvmOptions.enableContextReceivers() {
	freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
}
