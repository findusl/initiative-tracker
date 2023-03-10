import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
	id("com.github.ben-manes.versions") version "0.46.0"
}

buildscript {

	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:7.4.2")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}")
		classpath("org.jetbrains.kotlin:kotlin-serialization:${Version.kotlin}")
		// https://github.com/google/dagger/issues/3068
		classpath("com.squareup:javapoet:1.13.0")
	}
}

allprojects {
	repositories {
		google()
		mavenCentral()
	}
}

tasks.register("clean", Delete::class) {
	delete(rootProject.buildDir)
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
	rejectVersionIf {
		isNonStable(candidate.version) && !isNonStable(currentVersion)
	}
}

fun isNonStable(version: String): Boolean {
	val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
	val regex = "^[0-9,.v-]+(-r)?$".toRegex()
	val isStable = stableKeyword || regex.matches(version)
	return isStable.not()
}
