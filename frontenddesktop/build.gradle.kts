plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.compose")
}

kotlin {
	jvmToolchain(17)
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
