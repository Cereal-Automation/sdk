apply(from = "../gradle/publishing.gradle")

plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":cereal-sdk"))
    implementation(libs.okhttp)
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.reflect)
}
