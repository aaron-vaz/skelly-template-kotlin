package {{.Inputs.package.Value}}.greetings

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GreetingsTest {
    @Test
    fun `a greeting trims the name`() {
        assertEquals(Greeting("Hello, Ada!"), GreetingService().greet("  Ada "))
    }

    @Test
    fun `a blank name is not greeted`() {
        assertFailsWith<InvalidNameException> { GreetingService().greet("  ") }
    }

    // The module is tested on its own, with only its own routes, as it could be split from the service
    @Test
    fun `the route greets by name`() =
        testApplication {
            application {
                install(ContentNegotiation) { json() }
                routing { greetingRoutes() }
            }

            val response = client.get("/greetings/Ada")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("""{"message":"Hello, Ada!"}""", response.bodyAsText())
        }

    @Test
    fun `a name that is too long is a bad request`() =
        testApplication {
            application {
                install(ContentNegotiation) { json() }
                routing { greetingRoutes() }
            }

            val response = client.get("/greetings/${"a".repeat(101)}")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("""{"error":"name must be 1 to 100 characters"}""", response.bodyAsText())
        }
}
