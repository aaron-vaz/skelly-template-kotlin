import org.gradle.accessors.dm.LibrariesForLibs

// A module that stores its data with Exposed on SQLite.
plugins {
    id("kotlin-conventions")
}

val libs = the<LibrariesForLibs>()

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite.jdbc)
}
