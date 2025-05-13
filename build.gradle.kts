plugins {
	// this is necessary to avoid the plugins loaded multiple times
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.jetbrains.compose) apply false
	alias(libs.plugins.compose.compiler) apply false
}

buildscript {

	repositories {
		mavenCentral()
		google()
		gradlePluginPortal()
	}

	dependencies {
		classpath(libs.android.gradle)
		classpath(libs.kotlin.gradle.plugin)
		classpath(libs.kotlin.serialization)
		// https://github.com/google/dagger/issues/3068
		classpath(libs.javapoet)
		// https://issuetracker.google.com/issues/240445963
		classpath(libs.commons.compress)
	}
}

allprojects {
	repositories {
		mavenCentral()
		google()
	}
}

tasks.register("clean", Delete::class) {
	delete(rootProject.layout.buildDirectory)
}
