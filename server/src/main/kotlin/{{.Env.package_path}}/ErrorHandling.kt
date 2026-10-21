package {{.Inputs.package.Value}}

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import org.apache.logging.log4j.kotlin.logger

/**
 * How the service answers what no domain handled: a body that cannot be read and unexpected failures.
 */
internal object ErrorHandling {
    fun install(config: StatusPagesConfig) =
        with(config) {
            exception<BadRequestException> { call, cause ->
                logger.debug(cause) { "Bad request on ${call.request.local.uri}" }
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("invalid request"))
            }
            exception<Throwable> { call, cause ->
                logger.error(cause) { "Unhandled error on ${call.request.local.uri}" }
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("internal server error"))
            }
        }
}
