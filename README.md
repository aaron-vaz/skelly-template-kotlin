# {{.Inputs.project_name.Value}}

A modular Kotlin service on [Ktor](https://ktor.io){{if eq .Inputs.database.Value "sqlite"}}, with an items domain on [Exposed](https://www.jetbrains.com/help/exposed/) and SQLite{{end}}.

## Layout

```
server/            # the assembly: main, configuration, plugins, ops routes, and the wiring of the modules
modules/
  greetings/       # an example domain: its routes and logic, tested on its own
{{if eq .Inputs.database.Value "sqlite"}}  items/           # a domain with its own data: Exposed on SQLite, keeping its database to itself
{{end}}buildSrc/          # convention plugins: the build logic shared by the modules
gradle/libs.versions.toml   # the version catalog, for the modules and the convention plugins alike
```

Each domain module owns its routes, its logic and its data, and does not depend on another module, so it can be split into a service of its own when that is needed. `server` is the only module that knows all of them.

## Development

Requires JDK {{.Inputs.java_version.Value}}. Gradle is provided by the wrapper.

With [jabba](https://github.com/Jabba-Team/jabba), install the JDK once with `jabba install temurin@~{{.Inputs.java_version.Value}}`, then run `jabba use` in this directory: it reads `.jabbarc`.

```bash
./gradlew :server:run     # start the server on http://localhost:8080
./gradlew test            # run the tests of every module
./gradlew spotlessApply   # fix formatting (ktlint, using .editorconfig)
./gradlew -p buildSrc spotlessApply   # the same for buildSrc, which is a build of its own
./gradlew build           # formatting check, tests and the distribution
```

## Configuration

Everything is read from environment variables once, at startup.

| Variable | Default | |
|---|---|---|
| `HOST` | `0.0.0.0` | Address to listen on |
| `PORT` | `8080` | Port to listen on |
| `APP_VERSION` | `dev` | Reported by `/version`, set from the git tag in the image |
{{if eq .Inputs.database.Value "sqlite"}}| `DATABASE_URL` | `jdbc:sqlite:{{.Inputs.project_name.Value}}.db` | SQLite database of the items module; the file is created if missing |
{{end}}
Logging is [Log4j 2](https://logging.apache.org/log4j/2.x/): code uses its Kotlin API ([log4j-api-kotlin](https://logging.apache.org/log4j/kotlin/)), and `server/src/main/resources/log4j2.xml` configures it. The SLF4J logging of Ktor and Netty is routed to it.

## Endpoints

| | |
|---|---|
| `GET /healthz` | Liveness |
| `GET /readyz` | Readiness |
| `GET /version` | The running version |
| `GET /greetings/{name}` | A greeting |
{{if eq .Inputs.database.Value "sqlite"}}| `GET /items` | List the items stored in the database |
| `POST /items` | Add an item, `{"name": "milk"}` |
{{end}}
## Container image

The image is built by [Jib](https://github.com/GoogleContainerTools/jib) on a distroless Java base (`gcr.io/distroless/java{{.Inputs.java_version.Value}}-debian13:nonroot`), without a Dockerfile.

```bash
./gradlew :server:jibDockerBuild   # build into the local Docker daemon as ghcr.io/{{.Inputs.owner.Value | lower}}/{{.Inputs.project_name.Value}}
./gradlew :server:jibBuildTar      # build a tarball, no Docker needed: server/build/jib-image.tar
./gradlew :server:jib -Pversion=v1.0.0   # push to the registry (REGISTRY_USERNAME and REGISTRY_PASSWORD)
```

```bash
docker run --rm -p 8080:8080 {{if eq .Inputs.database.Value "sqlite"}}-v {{.Inputs.project_name.Value}}-data:/data {{end}}ghcr.io/{{.Inputs.owner.Value | lower}}/{{.Inputs.project_name.Value}}:dev
```
{{if eq .Inputs.database.Value "sqlite"}}
The database lives in the `/data` volume of the image, which any user can write to, as the image does not run as root.
{{end}}
Pushing a `v*` tag publishes the image to `ghcr.io/{{.Inputs.owner.Value | lower}}/{{.Inputs.project_name.Value}}`.

## Releases

`release-please` opens a release PR from the conventional commits on `main`, and merging it creates the tag. It needs a repository secret named `RELEASE_PLEASE_PAT`: a personal access token that can write contents and pull requests. The default `GITHUB_TOKEN` would not do, because a tag it creates does not trigger the image workflow.
