object Version {
	/* Limited by compose multiplatform.
	https://github.com/JetBrains/compose-multiplatform/blob/master/VERSIONING.md#kotlin-compatibility*/
	const val kotlin = "1.9.0"
	const val kotlinxSerialization = "1.5.1"
	const val kotlinxCollections = "0.3.5"
	const val coroutines = "1.7.3"
	const val ktor = "2.3.4"
	const val logback = "1.4.11"
	const val buildKonfig = "0.13.3"
	const val mppSettings = "1.0.0"
	const val mockito = "5.4.0"
	const val napier = "2.6.1"
	const val openAiClient = "3.3.2"

	object JetbrainsCompose {
		const val foundation = "1.4.3"
	}
	object Android {
		const val composeBom = "2023.08.00"
		const val composeCompiler = "1.5.1"
		const val gradleBuildTools = "8.1.1"
	}
	object ApacheCommons {
		const val compress = "1.23.0"
	}
}
