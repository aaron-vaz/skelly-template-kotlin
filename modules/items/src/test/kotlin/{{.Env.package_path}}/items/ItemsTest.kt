package {{.Inputs.package.Value}}.items

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.io.path.createTempFile
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemsTest {
    // The module is tested on its own, with a database of its own, as it could be split from the service
    private fun Application.itemsOnly() {
        install(ContentNegotiation) { json() }
        routing { itemRoutes("jdbc:sqlite:${createTempFile("items", ".db").also { it.toFile().deleteOnExit() }}") }
    }

    @Test
    fun `items start empty`() =
        testApplication {
            application { itemsOnly() }

            assertEquals("[]", client.get("/items").bodyAsText())
        }

    @Test
    fun `an item can be added and listed`() =
        testApplication {
            application { itemsOnly() }

            val created =
                client.post("/items") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"name":" milk "}""")
                }

            assertEquals(HttpStatusCode.Created, created.status)
            assertEquals("""{"id":1,"name":"milk"}""", created.bodyAsText())
            assertEquals("""[{"id":1,"name":"milk"}]""", client.get("/items").bodyAsText())
        }

    @Test
    fun `a blank name is rejected and nothing is stored`() =
        testApplication {
            application { itemsOnly() }

            val response =
                client.post("/items") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"name":"  "}""")
                }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("""{"error":"name must not be blank"}""", response.bodyAsText())
            assertEquals("[]", client.get("/items").bodyAsText())
        }
}
