plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.jib.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)

    // The type-safe catalog accessors (libs.ktor.server.core) are generated for the build, not for
    // precompiled script plugins, so this puts their class on the plugins' classpath
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

// buildSrc is a build of its own, so it formats its own scripts: ./gradlew -p buildSrc spotlessApply
spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get()).setEditorConfigPath(file("../.editorconfig"))
    }
    kotlinGradle {
        target("*.gradle.kts", "src/**/*.gradle.kts")
        ktlint(libs.versions.ktlint.get()).setEditorConfigPath(file("../.editorconfig"))
    }
}
