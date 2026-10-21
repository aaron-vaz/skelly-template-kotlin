# skelly-template-kotlin

A [skelly](https://github.com/aaron-vaz/skelly) template for modular Kotlin services on Ktor, with an optional domain on Exposed and SQLite.

| `database` | You get |
|---|---|
| `none` | `server` (the assembly) and a `greetings` example domain module: Ktor on Netty, JSON, error handling, ops routes (`/healthz`, `/readyz`, `/version`), env-based config and tests |
| `sqlite` | The same plus an `items` domain module that owns its data: [Exposed](https://www.jetbrains.com/help/exposed/) on SQLite, `GET`/`POST /items`, tests, and a `/data` volume in the image |

Both come with:

- **Convention plugins** in `buildSrc` (`kotlin-conventions`, `ktor-conventions`, `application-conventions`, `exposed-conventions`) and one **version catalog** that the modules and the plugins share.
- **Log4j 2** as the logging API (through log4j-api-kotlin) and implementation, with the SLF4J bridge for Ktor and Netty.
- **Spotless** running **ktlint**, with the rules in `.editorconfig`, and warnings as errors.
- A **distroless image built by Jib** (no Dockerfile, no Docker needed to build a tarball), plus CI, release-please, Dependabot, an `AGENTS.md` and a `.jabbarc` for [jabba](https://github.com/Jabba-Team/jabba).

## Usage

```bash
skelly sources add kotlin github.com/aaron-vaz/skelly-template-kotlin
skelly init kotlin -dst ./my-service
```

Inputs: `database` (`none` or `sqlite`, default `none`), `java_version` (`17`, `21` or `25`, default `21`: the versions with a distroless Java image), `owner` (default `aaron-vaz`), `group` (the reversed domain you own, default `uk.co.aaronvaz`), `package` (default derived from `group` and `project_name`, e.g. `uk.co.aaronvaz.myservice`) and `project_name` (required, kebab-case).

From a script or an AI agent:

```bash
skelly inspect kotlin
skelly init kotlin -dst ./my-service -no-input -set project_name=my-service -set database=sqlite
```

## How it works

`.skelly.yml` uses several skelly features to produce both variants from one tree:

- `choices` on `database` and `java_version`, so a typo is rejected.
- a templated `package` default, built from `group` and `project_name` with the `lower` and `replace` template functions, and an `env` value `package_path` (`uk.co.me.app` becomes `uk/co/me/app`) used in the names of the source directories.
- an `exclude` entry that removes the `items` module, the test of it in `server`, and the Jib directory for `/data`, unless `database` is `sqlite`. Patterns use the template's own names, so the package directory (`{{.Env.package_path}}`) is a `*` there. The `include` line, the dependency and the wiring are `{{if}}` blocks.
- `rename`, which moves `github/` to `.github` when the project is generated. The workflows and `dependabot.yml` for generated projects live in `github/`, so GitHub does not run them, and Dependabot does not read them, in this repository. The real `.github/` here holds only the template's own CI.

`github`, `gradlew`, `gradlew.bat` and `gradle/wrapper` are `raw`: they use `${{ }}` or `$` themselves. The JDK reaches the workflows and the build through `.java-version`, since raw files cannot use inputs.

When you edit a workflow for generated projects, edit it under `github/workflows/`. It is not run here, so check changes with the test below.

## Testing the template

`.github/workflows/template-test.yml` renders each variant with skelly, runs `./gradlew build` on the result and builds the image tarball with Jib. To do the same locally:

```bash
skelly init . -dst /tmp/demo-service -no-input -set project_name=demo-service -set database=sqlite
cd /tmp/demo-service && ./gradlew build :server:jibBuildTar
```
