package {{.Inputs.package.Value}}.items

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

/**
 * The routes of the items domain, backed by the SQLite database at [databaseUrl].
 * A body that cannot be read is left to the server module's error handling.
 */
fun Route.itemRoutes(databaseUrl: String) {
    val repository = ItemRepository.connect(databaseUrl)

    route("/items") {
        get { call.respond(repository.list()) }
        post {
            val newItem = call.receive<NewItem>()
            if (newItem.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "name must not be blank"))
            } else {
                call.respond(HttpStatusCode.Created, repository.add(newItem.name.trim()))
            }
        }
    }
}
