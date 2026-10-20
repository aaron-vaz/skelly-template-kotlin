package {{.Inputs.package.Value}}.greetings

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger("{{.Inputs.package.Value}}.greetings")

private const val MAX_NAME_LENGTH = 100

@Serializable
data class Greeting(
    val message: String,
)

/**
 * The greeting for [name]; a small piece of domain logic, kept apart from the HTTP routes.
 */
fun greet(name: String): Greeting = Greeting("Hello, ${name.trim()}!")

/**
 * The routes of the greetings domain; the server module decides to install them.
 */
fun Route.greetingRoutes() {
    get("/greetings/{name}") {
        val name = call.parameters["name"].orEmpty()
        if (name.isBlank() || name.length > MAX_NAME_LENGTH) {
            logger.debug("Rejected a name of {} characters", name.length)
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "name must be 1 to $MAX_NAME_LENGTH characters"))
        } else {
            call.respond(greet(name))
        }
    }
}
