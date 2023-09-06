import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.compose")
}

dependencies {
	implementation(project(":frontendshared"))
	implementation(compose.desktop.currentOs)
	// For the Dispatchers.Main. Sadly adding in shared module in Desktop.Main was not enough?
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${Version.coroutines}")
}

application {
	mainClass.set("de.lehrbaum.initiativetracker.MainKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
