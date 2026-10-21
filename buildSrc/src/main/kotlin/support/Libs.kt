package support

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

/**
 * The version catalog with type-safe accessors (libs.ktor.server.core), which Gradle does not
 * generate for precompiled script plugins.
 */
val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()
