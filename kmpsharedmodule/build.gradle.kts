
plugins {
	kotlin("multiplatform")
	id("org.jetbrains.compose") version Version.JetbrainsCompose.foundation
}

kotlin {
	jvm("desktop")

	sourceSets {
		named("commonMain") {
			dependencies {

				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material)
				implementation(compose.runtime)
				implementation(compose.preview)

				implementation(Dependency.kotlinxCoroutines)

				// Multiplatform Logging
				api(Dependency.napier)
			}
		}
		named("commonTest") {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		named("desktopMain") {
			dependencies {
				implementation(compose.desktop.common)
			}
		}
	}
}
