package {{.Inputs.package.Value}}

/**
 * The service's settings, read once from the environment at startup.
 */
data class Config(
    val host: String,
    val port: Int,
    val version: String,
{{if eq .Inputs.database.Value "sqlite"}}    val databaseUrl: String,
{{end}}) {
    companion object {
        const val DEFAULT_PORT = 8080

        fun fromEnv(env: Map<String, String>): Config {
            val port = env["PORT"]?.let { parsePort(it) } ?: DEFAULT_PORT
            return Config(
                host = env["HOST"] ?: "0.0.0.0",
                port = port,
                version = env["APP_VERSION"] ?: "dev",
{{if eq .Inputs.database.Value "sqlite"}}                databaseUrl = env["DATABASE_URL"] ?: "jdbc:sqlite:{{.Inputs.project_name.Value}}.db",
{{end}}            )
        }

        private fun parsePort(value: String): Int {
            val port = value.toIntOrNull()
            require(port != null && port in 1..65535) { "PORT must be a number between 1 and 65535, got \"$value\"" }
            return port
        }
    }
}
