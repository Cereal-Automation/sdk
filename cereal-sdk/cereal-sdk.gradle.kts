apply(from = "../gradle/publishing.gradle")

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.binaryCompatibilityValidator)
}

kotlin {
    jvmToolchain(17)
}

apiValidation {
    // Validate only the public API of the com.cereal.sdk package
    validationDisabled = false
    nonPublicMarkers.add("com.cereal.sdk.internal.InternalApi")
}

dependencies {
    testImplementation(libs.kotlin.test)
    testImplementation(project(":cereal-test-utils"))
    testImplementation(libs.coroutines.core)
    testImplementation(libs.mockk)
    // Compiles script snippets in tests to prove the opt-in API is enforced at compile time.
    testImplementation(libs.kotlin.compiler.embeddable)
}

tasks {
    kotlin {
        jvmToolchain(17)

        compilerOptions {
            freeCompilerArgs.set(listOf("-jvm-default=enable"))
            allWarningsAsErrors.set(true)
        }
    }
}
