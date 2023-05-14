import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.compose") version Version.JetbrainsCompose.foundation
}

dependencies {
    implementation(project(path = ":kmpsharedmodule"))
    implementation(compose.desktop.currentOs)
}

application {
	mainClass.set("de.lehrbaum.initiativetracker.Mainkt")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "17"
    }
}