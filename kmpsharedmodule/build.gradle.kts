@file:Suppress("OPT_IN_USAGE")

plugins {
	kotlin("multiplatform")
	id("com.android.library")
	id("org.jetbrains.compose") version Version.JetbrainsCompose.foundation
	id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
	jvm()
	android {
		compilations.all {
			kotlinOptions {
				jvmTarget = "11"
			}
		}
	}

	targetHierarchy.default {
		common {
			group("frontend") {
				withJvm()
				group("mobile") {
					withAndroid()
					withIos()
				}
			}
			group("backend") {
				withCompilations { it.target.platformType == org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm }
				withNative()
			}
		}
	}

	@Suppress("UNUSED_VARIABLE")
	sourceSets {
		named("commonMain") {
			dependencies {
				implementation(project(path = ":commands"))
				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material)
				implementation(compose.runtime)
				implementation(compose.preview)
			}
		}

		named("frontendMain") {
			dependencies {

			}
		}


		named("commonTest") {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		named("androidMain") {

		}
	}
}

android {
	namespace = "de.lehrbaum.initiativetracker"
	compileSdk = 33
	defaultConfig {
		minSdk = 28
	}
}