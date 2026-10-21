plugins {
    id("application-conventions")
}

dependencies {
    implementation(project(":modules:greetings"))
{{if eq .Inputs.database.Value "sqlite"}}    implementation(project(":modules:items"))
{{end}}}

application {
    mainClass.set("{{.Inputs.package.Value}}.ApplicationKt")
}

jib {
    to {
        image = "ghcr.io/{{.Inputs.owner.Value | lower}}/{{.Inputs.project_name.Value}}"
        tags = setOf(version.toString())
        auth {
            username = System.getenv("REGISTRY_USERNAME")
            password = System.getenv("REGISTRY_PASSWORD")
        }
    }
    container {
        environment =
            mapOf(
                "APP_VERSION" to version.toString(),
{{if eq .Inputs.database.Value "sqlite"}}                "DATABASE_URL" to "jdbc:sqlite:/data/{{.Inputs.project_name.Value}}.db",
{{end}}            )
{{if eq .Inputs.database.Value "sqlite"}}        volumes = listOf("/data")
{{end}}    }
{{if eq .Inputs.database.Value "sqlite"}}    // The database directory has to be writable by the non-root user of the image
    extraDirectories {
        paths {
            path {
                setFrom("src/jib")
                into = "/"
            }
        }
        permissions.set(mapOf("/data" to "777"))
    }
{{end}}}
