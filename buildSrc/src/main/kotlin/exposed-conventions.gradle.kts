import support.libs

// A module that stores its data with Exposed on SQLite.
plugins {
    id("kotlin-conventions")
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite.jdbc)
}
