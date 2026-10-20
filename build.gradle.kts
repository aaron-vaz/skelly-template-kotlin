import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.diffplug.spotless")
}

// Spotless downloads ktlint from the project's repositories, and the root project is no module
repositories {
    mavenCentral()
}

// The modules format their own sources; this covers the scripts that belong to no module
configure<SpotlessExtension> {
    kotlinGradle {
        target("*.gradle.kts", "buildSrc/*.gradle.kts", "buildSrc/src/**/*.gradle.kts", "buildSrc/src/**/*.kt")
        ktlint(
            the<VersionCatalogsExtension>()
                .named("libs")
                .findVersion("ktlint")
                .get()
                .requiredVersion,
        ).setEditorConfigPath(rootProject.file(".editorconfig"))
    }
}
