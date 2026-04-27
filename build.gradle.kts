plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint)
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        additionalEditorconfig.set(
            mapOf(
                "max_line_length" to "off",
                "ktlint_standard_filename" to "disabled",
            ),
        )
        filter {
            exclude {
                it.file.path.contains(
                    layout.buildDirectory
                        .dir("generated")
                        .get()
                        .toString(),
                )
            }
            include("**/kotlin/**")
        }
    }
}
