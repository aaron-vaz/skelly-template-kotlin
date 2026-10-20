import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.kotlin.dsl.the

private fun Project.catalog() = the<VersionCatalogsExtension>().named("libs")

/**
 * A library of the version catalog by its alias, e.g. lib("ktor-server-core"), because the
 * type-safe libs accessors are not available in precompiled script plugins.
 */
fun Project.lib(alias: String): Provider<MinimalExternalModuleDependency> = catalog().findLibrary(alias).get()

/**
 * A version of the version catalog by its alias.
 */
fun Project.versionOf(alias: String): String = catalog().findVersion(alias).get().requiredVersion

/**
 * The JDK of the project, read from the .java-version file that CI and jabba users share.
 */
fun Project.javaVersion(): Int =
    rootProject
        .file(".java-version")
        .readText()
        .trim()
        .toInt()
