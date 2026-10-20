// A module that stores its data with Exposed on SQLite.
plugins {
    id("kotlin-conventions")
}

dependencies {
    implementation(lib("exposed-core"))
    implementation(lib("exposed-jdbc"))
    implementation(lib("sqlite-jdbc"))
}
