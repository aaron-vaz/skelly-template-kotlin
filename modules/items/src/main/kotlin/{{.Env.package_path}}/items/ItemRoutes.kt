package {{.Inputs.package.Value}}.items

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger("{{.Inputs.package.Value}}.items")

/**
 * The routes of the items domain, backed by the SQLite database at [databaseUrl].
 * A body that cannot be read is left to the server module's error handling.
 */
fun Route.itemRoutes(databaseUrl: String) {
    val database = connectDatabase(databaseUrl)
    logger.info("Items are stored in {}", databaseUrl)

    route("/items") {
        get { call.respond(listItems(database)) }
        post {
            val newItem = call.receive<NewItem>()
            if (newItem.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "name must not be blank"))
            } else {
                call.respond(HttpStatusCode.Created, addItem(database, newItem.name.trim()))
            }
        }
    }
}
