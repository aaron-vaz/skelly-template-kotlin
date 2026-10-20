// A module that serves part of the HTTP API: Ktor, JSON and the Log4j API, which is the logging facade.
plugins {
    id("kotlin-conventions")
    kotlin("plugin.serialization")
}

dependencies {
    api(lib("ktor-server-core"))
    implementation(lib("ktor-server-content-negotiation"))
    implementation(lib("ktor-serialization-kotlinx-json"))
    implementation(lib("log4j-api"))

    testImplementation(lib("ktor-server-test-host"))
    // Log4j itself, and the bridge that sends the SLF4J logging of Ktor and Netty to it
    testRuntimeOnly(lib("log4j-core"))
    testRuntimeOnly(lib("log4j-slf4j2-impl"))
}
