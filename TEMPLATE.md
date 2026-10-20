# skelly-template-kotlin

A [skelly](https://github.com/aaron-vaz/skelly) template for Kotlin web services on Ktor, with optional Exposed and SQLite.

| `database` | You get |
|---|---|
| `none` | Ktor with Netty, JSON, call logging, error handling, `/healthz`, `/readyz` and `/version`, env-based config and tests |
| `sqlite` | The same plus [Exposed](https://www.jetbrains.com/help/exposed/) on SQLite with an example `items` table, repository functions, `/items` routes, tests and a data volume in the Dockerfile |

Both come with Gradle (Kotlin DSL, version catalog, wrapper), ktlint, warnings as errors, a Dockerfile, CI, release-please, Dependabot and an `AGENTS.md`.

## Usage

```bash
skelly sources add kotlin github.com/aaron-vaz/skelly-template-kotlin
skelly init kotlin -dst ./my-service
```

Inputs: `database` (`none` or `sqlite`, default `none`), `java_version` (default `21`), `owner` (default `aaron-vaz`), `package` (default derived from `owner` and `project_name`, e.g. `io.github.aaronvaz.myservice`) and `project_name` (required, kebab-case).

From a script or an AI agent:

```bash
skelly inspect kotlin
skelly init kotlin -dst ./my-service -no-input -set project_name=my-service -set database=sqlite
```

## How it works

`.skelly.yml` uses several skelly features to produce both variants from one tree:

- `choices` on `database`, so a typo is rejected instead of producing a half-empty project.
- a templated `package` default, built from `owner` and `project_name` with the `lower` and `replace` template functions, and an `env` value `package_path` (`io.github.me.app` becomes `io/github/me/app`) used in the names of the source directories.
- an `exclude` entry that removes the `db` package, with its tests, unless `database` is `sqlite`. Patterns use the template's own names, so the package directory (`{{.Env.package_path}}`) is a `*` there.
- `rename`, which moves `github/` to `.github` when the project is generated. The workflows and `dependabot.yml` for generated projects live in `github/`, so GitHub does not run them, and Dependabot does not read them, in this repository. The real `.github/` here holds only the template's own CI.

`github`, `gradlew`, `gradlew.bat` and `gradle/wrapper` are `raw`: they use `${{ }}` or `$` themselves. The JDK version reaches the workflows through `.java-version`, which `setup-java` reads, since raw files cannot use inputs. A `.jabbarc` (`temurin@~21` for `java_version` 21) lets [jabba](https://github.com/Jabba-Team/jabba) users run `jabba use` in the project.

When you edit a workflow for generated projects, edit it under `github/workflows/`. It is not run here, so check changes with the test below.

## Testing the template

`.github/workflows/template-test.yml` renders each variant with skelly and runs `./gradlew build` on the result. To do the same locally:

```bash
skelly init . -dst /tmp/demo-service -no-input -set project_name=demo-service -set database=sqlite
cd /tmp/demo-service && ./gradlew build
```
