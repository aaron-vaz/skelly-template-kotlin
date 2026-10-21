package {{.Inputs.package.Value}}.greetings

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * The routes of the greetings domain, which only translate between HTTP and [GreetingService];
 * the server module decides to install them.
 */
fun Route.greetingRoutes(service: GreetingService = GreetingService()) {
    get("/greetings/{name}") {
        try {
            call.respond(service.greet(call.parameters["name"].orEmpty()))
        } catch (e: InvalidNameException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message.orEmpty()))
        }
    }
}
