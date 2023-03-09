buildscript {

	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:7.4.2")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Constants.kotlinVersion}")
		classpath("org.jetbrains.kotlin:kotlin-serialization:${Constants.kotlinVersion}")
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
