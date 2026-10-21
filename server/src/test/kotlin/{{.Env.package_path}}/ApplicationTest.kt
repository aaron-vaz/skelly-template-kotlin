package {{.Inputs.package.Value}}

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun `healthz reports ok`() =
        testApplication {
            application { module(testConfig()) }

            val response = client.get("/healthz")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("""{"status":"ok"}""", response.bodyAsText())
        }

    @Test
    fun `readyz reports ready`() =
        testApplication {
            application { module(testConfig()) }

            val response = client.get("/readyz")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("""{"status":"ready"}""", response.bodyAsText())
        }

    @Test
    fun `version reports the configured version`() =
        testApplication {
            application { module(testConfig(version = "1.2.3")) }

            assertEquals("""{"version":"1.2.3"}""", client.get("/version").bodyAsText())
        }

    @Test
    fun `the greetings module is part of the service`() =
        testApplication {
            application { module(testConfig()) }

            assertEquals("""{"message":"Hello, Ada!"}""", client.get("/greetings/Ada").bodyAsText())
        }

    @Test
    fun `unknown paths are not found`() =
        testApplication {
            application { module(testConfig()) }

            assertEquals(HttpStatusCode.NotFound, client.get("/nope").status)
        }
}
