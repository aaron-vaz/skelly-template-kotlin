# AGENTS.md

Guidelines for agentic coding agents working in this repository.

## Project Overview

`{{.Inputs.project_name.Value}}` is a modular Kotlin service on Ktor{{if eq .Inputs.database.Value "sqlite"}}, with an items domain on Exposed and SQLite{{end}}.

- **Language**: Kotlin, JDK {{.Inputs.java_version.Value}}
- **JDK**: pinned in `.java-version` (read by the build and by CI) and `.jabbarc` ([jabba](https://github.com/Jabba-Team/jabba): `jabba use`); to change it, update both, and the image base in `buildSrc/src/main/kotlin/application-conventions.gradle.kts` follows
- **Build**: Gradle (Kotlin DSL), convention plugins in `buildSrc`, one version catalog in `gradle/libs.versions.toml`
- **Logging**: Log4j 2, API in the code and implementation in `server`
- **Package**: `{{.Inputs.package.Value}}`

## Commands

```bash
./gradlew build                    # formatting check, tests and the distribution
./gradlew test                     # the tests of every module
./gradlew :modules:greetings:test  # one module
./gradlew spotlessApply            # fix formatting before committing
./gradlew -p buildSrc spotlessApply  # the same for buildSrc, which is a build of its own
./gradlew :server:run              # start the server
./gradlew :server:jibBuildTar      # build the distroless image as a tarball, no Docker needed
```

## Layout

```
server/           # the assembly: main, Config, plugins, error handling, ops routes, and the wiring of the modules
modules/<domain>/ # one module per domain: its routes, logic and data
buildSrc/         # convention plugins (kotlin-conventions, ktor-conventions, application-conventions, exposed-conventions); helpers are in support/
gradle/libs.versions.toml
```

Sources are in `src/main/kotlin/{{.Env.package_path}}/` of each module, in a sub-package named after the module, and tests mirror them.

## Conventions

- **Modules are domains.** A module owns its routes, logic and data, exposes a `Route.xxxRoutes(...)` function, and does not depend on another domain module. Only `server` depends on all of them and installs their routes in `Application.kt`. Test a module on its own, with only its own routes.
- **Inside a module, one file per responsibility:** the model (`@Serializable` types), the logic (a class that knows nothing about HTTP), the data (a repository, if it stores any) and the routes, which only translate between HTTP and the logic and answer a request they cannot serve with a status and `{"error": "..."}`.
- **To add a domain:** create `modules/<name>/build.gradle.kts` applying `ktor-conventions` (and `exposed-conventions` if it stores data), add `include(":modules:<name>")` to `settings.gradle.kts`, depend on it from `server/build.gradle.kts` and install its routes.
- **Build logic lives in `buildSrc`** as precompiled script plugins, applied by id (`id("ktor-conventions")`). Do not repeat dependencies or compiler settings in a module; put them in a convention plugin.
- **Versions live in the catalog.** Add dependencies to `gradle/libs.versions.toml`; convention plugins use the type-safe accessors (`import support.libs`, then `libs.ktor.server.core`), and nothing else has a version. The root `build.gradle.kts` only sets the group of every project, which a module can append to.
- **Logging goes through log4j-api-kotlin:** in a class or object use the `logger` extension property (`import org.apache.logging.log4j.kotlin.logger`), which names the logger after the class, with lazy messages: `logger.debug { "..." }`, and the exception first: `logger.error(cause) { "..." }`. Do not log from a top-level function; put the code in a class. `server` has the Log4j implementation at runtime and its `log4j2.xml`; the modules get it for their tests only.
- **Configuration** comes from environment variables, read in `Config.fromEnv` and passed down; do not call `System.getenv` elsewhere.
- **Errors:** a domain answers a request it cannot serve with a status and `{"error": "..."}`; unexpected exceptions and unreadable bodies are handled by the `StatusPages` plugin in `server`.
- **Formatting is ktlint through Spotless**, with the rules in `.editorconfig`; warnings are errors. Run `./gradlew spotlessApply` and `./gradlew -p buildSrc spotlessApply` before committing: `buildSrc` formats itself, and `./gradlew build` does not check it.
- **The image is built by Jib** on a distroless base, configured in `application-conventions` and `server/build.gradle.kts`; there is no Dockerfile.
{{if eq .Inputs.database.Value "sqlite"}}- **The items module hides its database:** callers pass a URL to `itemRoutes`, and Exposed does not appear in its public API, so `server` does not depend on it.
{{end}}