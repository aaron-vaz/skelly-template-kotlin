FROM eclipse-temurin:{{.Inputs.java_version.Value}}-jdk AS build
WORKDIR /src
COPY . .
RUN ./gradlew installDist --no-daemon

FROM eclipse-temurin:{{.Inputs.java_version.Value}}-jre
ARG VERSION=dev
ENV APP_VERSION=${VERSION}
RUN useradd --system --uid 10001 app{{if eq .Inputs.database.Value "sqlite"}} \
    && mkdir /data && chown app /data{{end}}
COPY --from=build /src/build/install/{{.Inputs.project_name.Value}}/ /app/
USER app
{{if eq .Inputs.database.Value "sqlite"}}ENV DATABASE_URL=jdbc:sqlite:/data/{{.Inputs.project_name.Value}}.db
VOLUME /data
{{end}}EXPOSE 8080
ENTRYPOINT ["/app/bin/{{.Inputs.project_name.Value}}"]
