import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.*

plugins {
	// Plugin helps find available updates for dependencies https://github.com/ben-manes/gradle-versions-plugin
	id("com.github.ben-manes.versions") version Version.versionPlugin

	// this is necessary to avoid the plugins to be loaded multiple times
	kotlin("multiplatform") version Version.kotlin apply false
	id("org.jetbrains.compose") version Version.JetbrainsCompose.foundation apply false
}

buildscript {

	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:${Version.Android.gradleBuildTools}")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}")
		classpath("org.jetbrains.kotlin:kotlin-serialization:${Version.kotlin}")
		// https://github.com/google/dagger/issues/3068
		classpath("com.squareup:javapoet:1.13.0")
		// https://issuetracker.google.com/issues/240445963
		classpath("org.apache.commons:commons-compress:${Version.ApacheCommons.compress}")
	}
}

allprojects {
	repositories {
		google()
		mavenCentral()
	}
}

tasks.register("clean", Delete::class) {
	delete(rootProject.layout.buildDirectory)
}

// What kind of dependencies updates interest me https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
	rejectVersionIf {
		!isStable(candidate.version) && isStable(currentVersion)
	}
}

/** Helper for dependency update filters */
fun isStable(version: String): Boolean {
	val stableKeyword = listOf("RELEASE", "FINAL", "GA")
		.any { version.uppercase(Locale.getDefault()).contains(it) }
	val regex = "^[0-9,.v-]+(-r)?$".toRegex()
	val isStable = stableKeyword || regex.matches(version)
	return isStable
}
