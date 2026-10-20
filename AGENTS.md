# AGENTS.md

Guidelines for agentic coding agents working in this repository.

## Project Overview

`{{.Inputs.project_name.Value}}` is a Kotlin web service on Ktor{{if eq .Inputs.database.Value "sqlite"}}, with Exposed and SQLite{{end}}.

- **Language**: Kotlin, JDK {{.Inputs.java_version.Value}}
- **JDK**: pinned in `.java-version` (CI) and `.jabbarc` ([jabba](https://github.com/Jabba-Team/jabba): `jabba use`); to change it, update both, `jvmToolchain` in `build.gradle.kts` and the Dockerfile
- **Build**: Gradle (Kotlin DSL) with a version catalog in `gradle/libs.versions.toml`
- **Package**: `{{.Inputs.package.Value}}`

## Commands

```bash
./gradlew build            # ktlint, tests and the distribution
./gradlew test             # tests only
./gradlew test --tests "*ConfigTest"   # one test class
./gradlew ktlintFormat     # fix formatting before committing
./gradlew run              # start the server
```

## Layout

```
src/main/kotlin/{{.Env.package_path}}/
  Application.kt   # main and the Ktor module: plugins and routes, wiring only
  Config.kt        # settings read once from the environment
  Routes.kt        # health, readiness and version routes
{{if eq .Inputs.database.Value "sqlite"}}  db/              # Exposed tables, queries and the routes that use them
{{end}}src/test/kotlin/...  # tests mirror the main packages
```

## Conventions

- Add dependencies to `gradle/libs.versions.toml` and reference them as `libs.*`; do not put versions in `build.gradle.kts`.
- Configuration comes from environment variables, read in `Config.fromEnv` and passed down; do not call `System.getenv` elsewhere.
- Warnings are errors, and formatting is ktlint (`.editorconfig`); run `./gradlew ktlintFormat` before committing.
- Return errors as `ErrorResponse` JSON with a suitable status; unexpected exceptions are handled by the `StatusPages` plugin.
- Test routes with `testApplication` and a `testConfig()`; no real network is needed.
{{if eq .Inputs.database.Value "sqlite"}}- Database access goes through functions in `db/`, each in its own `transaction`; routes do not use Exposed directly.
{{end}}