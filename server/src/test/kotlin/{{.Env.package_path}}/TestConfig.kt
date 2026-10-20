package {{.Inputs.package.Value}}

{{if eq .Inputs.database.Value "sqlite"}}import kotlin.io.path.createTempFile

{{end}}/**
 * A configuration for tests: the port is not used, as tests do not listen on one.
 */
fun testConfig(version: String = "test"): Config =
    Config(
        host = "localhost",
        port = Config.DEFAULT_PORT,
        version = version,
{{if eq .Inputs.database.Value "sqlite"}}        databaseUrl = "jdbc:sqlite:${createTempFile("test", ".db").also { it.toFile().deleteOnExit() }}",
{{end}}    )
