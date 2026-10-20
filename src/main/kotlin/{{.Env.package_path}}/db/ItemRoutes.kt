package {{.Inputs.package.Value}}.db

import {{.Inputs.package.Value}}.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jetbrains.exposed.v1.jdbc.Database

fun Route.itemRoutes(database: Database) {
    route("/items") {
        get { call.respond(listItems(database)) }
        post {
            val newItem = call.receive<NewItem>()
            if (newItem.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("name must not be blank"))
            } else {
                call.respond(HttpStatusCode.Created, addItem(database, newItem.name.trim()))
            }
        }
    }
}
