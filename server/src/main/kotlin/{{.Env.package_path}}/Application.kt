package {{.Inputs.package.Value}}

import {{.Inputs.package.Value}}.greetings.greetingRoutes
{{if eq .Inputs.database.Value "sqlite"}}import {{.Inputs.package.Value}}.items.itemRoutes
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
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger("{{.Inputs.package.Value}}")

fun main() {
    val config = Config.fromEnv(System.getenv())
    embeddedServer(Netty, host = config.host, port = config.port) { module(config) }
        .start(wait = true)
}

/**
 * The service: the plugins every module relies on, and the routes of each domain module.
 */
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

    routing {
        opsRoutes(config.version)
        greetingRoutes()
{{if eq .Inputs.database.Value "sqlite"}}        itemRoutes(config.databaseUrl)
{{end}}    }
}
