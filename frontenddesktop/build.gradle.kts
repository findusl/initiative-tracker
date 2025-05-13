plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.jetbrains.compose)
	alias(libs.plugins.compose.compiler)
}

kotlin {
	jvmToolchain(21)
	jvm()
	sourceSets {
		val jvmMain by getting {
			dependencies {
				implementation(project(":frontendshared"))
				implementation(compose.desktop.currentOs)
				// For the Dispatchers.Main. Sadly adding in shared module in Desktop.Main was not enough?
				runtimeOnly(libs.kotlinx.coroutines.swing)
			}
		}
	}
}

compose.desktop {
	application {
		mainClass = "de.lehrbaum.initiativetracker.MainKt"
	}
}
