plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.jetbrains.compose)
}

kotlin {
	jvmToolchain(17)
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
