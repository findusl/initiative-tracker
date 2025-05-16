# Initiative Tracker Development Guidelines

## Build and Test Tasks

### JVM vs Android Tasks

When working on this project, always use JVM build and test tasks instead of Android tasks. This is important for several reasons:

1. **Faster Execution**: JVM tasks are generally faster than Android tasks, which require emulator setup or device connection.
2. **Simpler Dependencies**: JVM tasks have fewer dependencies and are more reliable in CI/CD environments.
3. **Consistent Results**: JVM tasks provide more consistent results across different development environments.

### How to Use JVM Tasks

#### Running Tests

To run tests using JVM tasks:

```bash
# Run all JVM tests
./gradlew jvmTest

# Run specific test class
./gradlew jvmTest --tests "de.lehrbaum.initiativetracker.bl.InputValidatorTest"

# Run specific test method
./gradlew jvmTest --tests "de.lehrbaum.initiativetracker.bl.InputValidatorTest.testIsValidHostGoodCase"
```

Avoid using:
```bash
# DON'T use these Android-specific tasks
./gradlew androidTest
./gradlew connectedAndroidTest
```

#### Building the Project

To build the project using JVM tasks:

```bash
# Build JVM artifacts
./gradlew jvmJar

# Build all JVM-related tasks
./gradlew jvmMainClasses jvmTestClasses
```

Avoid using:
```bash
# DON'T use these Android-specific tasks
./gradlew assembleDebug
./gradlew assembleRelease
```

## Project Structure

This is a Kotlin Multiplatform project with the following targets:
- JVM (Desktop)
- Android
- iOS

The project uses a hierarchical source set structure:
- `commonMain` - Code shared across all platforms
- `jvmTargetsMain` - Code shared between JVM and Android
- `jvmMain` - JVM-specific code
- `androidMain` - Android-specific code
- `appleMain` - iOS-specific code

Test source sets follow a similar structure:
- `commonTest` - Tests shared across all platforms
- `jvmTargetsTest` - Tests shared between JVM and Android
- `jvmTest` - JVM-specific tests
- `androidUnitTest` - Android-specific tests

## Development Workflow

1. Always run tests using JVM tasks before committing changes
2. Use the `./gradlew printSourceSets` task to see all available source sets
3. When adding new dependencies, consider which source set they should belong to
4. For local configuration, copy `local.properties.template` to `local.properties` and adjust as needed
5. **When changing something in the backend**, always use JVM tasks for building and testing:
   ```bash
   # Build backend
   ./gradlew :backendjvm:build
   ./gradlew :backendshared:jvmJar

   # Test backend
   ./gradlew :backendjvm:test
   ./gradlew :backendshared:jvmTest
   ```

## Troubleshooting

If you encounter build issues:
1. Try cleaning the project: `./gradlew clean`
2. Make sure you're using the correct JDK version (JDK 21 as specified in the build file)
3. Check that you're using JVM tasks rather than Android tasks
4. Verify that all dependencies are correctly resolved
