import com.diffplug.gradle.spotless.SpotlessExtension
import support.libs
import support.javaVersion

// What every module has: Kotlin on the project's JDK, warnings as errors, tests and formatting.
plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(javaVersion())
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

// ktlint through Spotless, which reads the rules from the root .editorconfig
configure<SpotlessExtension> {
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get()).setEditorConfigPath(rootProject.file(".editorconfig"))
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get()).setEditorConfigPath(rootProject.file(".editorconfig"))
    }
}
