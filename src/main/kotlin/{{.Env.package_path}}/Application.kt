package {{.Inputs.package.Value}}

{{if eq .Inputs.database.Value "sqlite"}}import {{.Inputs.package.Value}}.db.connectDatabase
import {{.Inputs.package.Value}}.db.itemRoutes
{{end}}import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("{{.Inputs.package.Value}}")

fun main() {
    val config = Config.fromEnv(System.getenv())
    embeddedServer(Netty, host = config.host, port = config.port) { module(config) }
        .start(wait = true)
}

fun Application.module(config: Config) {
    install(ContentNegotiation) { json() }
    install(CallLogging)
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            logger.debug("Bad request on {}", call.request.local.uri, cause)
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid request"))
        }
        exception<Throwable> { call, cause ->
            logger.error("Unhandled error on {}", call.request.local.uri, cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("internal server error"))
        }
    }
{{if eq .Inputs.database.Value "sqlite"}}
    val database = connectDatabase(config.databaseUrl)
{{end}}
    routing {
        healthRoutes(config.version)
{{if eq .Inputs.database.Value "sqlite"}}        itemRoutes(database)
{{end}}    }
}
