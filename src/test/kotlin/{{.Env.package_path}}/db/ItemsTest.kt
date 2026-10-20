package {{.Inputs.package.Value}}.db

import {{.Inputs.package.Value}}.module
import {{.Inputs.package.Value}}.testConfig
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemsTest {
    @Test
    fun `items start empty`() =
        testApplication {
            application { module(testConfig()) }

            assertEquals("[]", client.get("/items").bodyAsText())
        }

    @Test
    fun `an item can be added and listed`() =
        testApplication {
            application { module(testConfig()) }

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
    fun `malformed json is a bad request and not a server error`() =
        testApplication {
            application { module(testConfig()) }

            val response =
                client.post("/items") {
                    contentType(ContentType.Application.Json)
                    setBody("{nope")
                }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("""{"error":"invalid request"}""", response.bodyAsText())
        }

    @Test
    fun `a blank name is rejected and nothing is stored`() =
        testApplication {
            application { module(testConfig()) }

            val response =
                client.post("/items") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"name":"  "}""")
                }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("[]", client.get("/items").bodyAsText())
        }
}
