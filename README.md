# {{.Inputs.project_name.Value}}

A Kotlin web service on [Ktor](https://ktor.io){{if eq .Inputs.database.Value "sqlite"}}, with [Exposed](https://www.jetbrains.com/help/exposed/) and SQLite{{end}}.

## Development

Requires JDK {{.Inputs.java_version.Value}}. Gradle is provided by the wrapper.

With [jabba](https://github.com/Jabba-Team/jabba), install the JDK once with `jabba install temurin@~{{.Inputs.java_version.Value}}`, then run `jabba use` in this directory: it reads `.jabbarc`.

```bash
./gradlew run          # start the server on http://localhost:8080
./gradlew test         # run the tests
./gradlew ktlintFormat # fix formatting
./gradlew build        # ktlint, tests and the distribution
```

## Configuration

Everything is read from environment variables once, at startup.

| Variable | Default | |
|---|---|---|
| `HOST` | `0.0.0.0` | Address to listen on |
| `PORT` | `8080` | Port to listen on |
| `APP_VERSION` | `dev` | Reported by `/version`, set from the git tag in the image |
{{if eq .Inputs.database.Value "sqlite"}}| `DATABASE_URL` | `jdbc:sqlite:{{.Inputs.project_name.Value}}.db` | SQLite database; the file is created if missing |
{{end}}
## Endpoints

| | |
|---|---|
| `GET /healthz` | Liveness |
| `GET /readyz` | Readiness |
| `GET /version` | The running version |
{{if eq .Inputs.database.Value "sqlite"}}| `GET /items` | List the items stored in the database |
| `POST /items` | Add an item, `{"name": "milk"}` |
{{end}}
## Container

```bash
docker build -t {{.Inputs.project_name.Value}} .
docker run --rm -p 8080:8080 {{if eq .Inputs.database.Value "sqlite"}}-v {{.Inputs.project_name.Value}}-data:/data {{end}}{{.Inputs.project_name.Value}}
```

Pushing a `v*` tag publishes the image to `ghcr.io/{{.Inputs.owner.Value}}/{{.Inputs.project_name.Value}}`.
