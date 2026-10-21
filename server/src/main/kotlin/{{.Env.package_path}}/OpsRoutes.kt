package {{.Inputs.package.Value}}

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val status: String,
)

@Serializable
data class VersionInfo(
    val version: String,
)

@Serializable
data class ErrorResponse(
    val error: String,
)

/**
 * What an orchestrator needs: liveness, readiness and the running version.
 */
fun Route.opsRoutes(version: String) {
    get("/healthz") { call.respond(Status("ok")) }
    get("/readyz") { call.respond(Status("ready")) }
    get("/version") { call.respond(VersionInfo(version)) }
}
