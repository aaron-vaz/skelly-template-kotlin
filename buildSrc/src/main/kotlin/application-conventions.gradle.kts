import support.libs
import support.javaVersion

// The module that assembles the others into a service, and packages it as a distroless image with Jib.
plugins {
    id("ktor-conventions")
    application
    id("com.google.cloud.tools.jib")
}

dependencies {
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.status.pages)
    // Log4j is the implementation, and also receives the SLF4J logging of Ktor and Netty
    runtimeOnly(libs.log4j.core)
    runtimeOnly(libs.log4j.slf4j2.impl)
}

application {
    applicationName = rootProject.name
}

jib {
    from {
        image = "gcr.io/distroless/java${javaVersion()}-debian13:nonroot"
    }
    container {
        ports = listOf("8080")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}
