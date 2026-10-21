import org.gradle.accessors.dm.LibrariesForLibs

// A module that serves part of the HTTP API: Ktor, JSON and the Log4j API, which is the logging facade.
plugins {
    id("kotlin-conventions")
    kotlin("plugin.serialization")
}

val libs = the<LibrariesForLibs>()

dependencies {
    api(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.log4j.api)
    implementation(libs.log4j.api.kotlin)

    testImplementation(libs.ktor.server.test.host)
    // Log4j itself, and the bridge that sends the SLF4J logging of Ktor and Netty to it
    testRuntimeOnly(libs.log4j.core)
    testRuntimeOnly(libs.log4j.slf4j2.impl)
}
