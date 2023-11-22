import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.*

plugins {
	// Plugin helps find available updates for dependencies https://github.com/ben-manes/gradle-versions-plugin
	alias(libs.plugins.ben.manes.versions)

	// this is necessary to avoid the plugins loaded multiple times
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.jetbrains.compose) apply false
}

buildscript {

	repositories {
		google()
		mavenCentral()
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
