// The module that assembles the others into a service, and packages it as a distroless image with Jib.
plugins {
    id("ktor-conventions")
    application
    id("com.google.cloud.tools.jib")
}

dependencies {
    implementation(lib("ktor-server-netty"))
    implementation(lib("ktor-server-call-logging"))
    implementation(lib("ktor-server-status-pages"))
    // Log4j is the implementation, and also receives the SLF4J logging of Ktor and Netty
    runtimeOnly(lib("log4j-core"))
    runtimeOnly(lib("log4j-slf4j2-impl"))
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
