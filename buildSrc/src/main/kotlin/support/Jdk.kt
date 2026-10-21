package support

import org.gradle.api.Project

/**
 * The JDK of the project, read from the .java-version file that CI and jabba users share.
 */
fun Project.javaVersion(): Int =
    rootProject
        .file(".java-version")
        .readText()
        .trim()
        .toInt()
