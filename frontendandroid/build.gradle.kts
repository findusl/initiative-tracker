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

android {
	compileSdk = 34

	defaultConfig {
		applicationId = "de.lehrbaum.initiativetracker"
		minSdk = 28
		targetSdk = 34
		versionCode = 1
		versionName = "0.1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		getByName("release") {
			isMinifyEnabled = true
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
			isDebuggable = false
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
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
	kotlinOptions {
		enableContextReceivers()
		jvmTarget = "17"
	}
	namespace = "de.lehrbaum.initiativetracker"
}

dependencies {
	implementation(project(":frontendshared"))
	implementation(project(":dtos"))

	// For theme apparently needed. Need to look deeper into theming
	implementation("com.google.android.material:material:1.9.0")

	testImplementation("junit:junit:4.13.2")

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
