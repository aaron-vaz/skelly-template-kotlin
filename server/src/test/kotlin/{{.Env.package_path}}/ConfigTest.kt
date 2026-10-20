package {{.Inputs.package.Value}}

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ConfigTest {
    @Test
    fun `defaults are used when the environment is empty`() {
        val config = Config.fromEnv(emptyMap())

        assertEquals("0.0.0.0", config.host)
        assertEquals(8080, config.port)
        assertEquals("dev", config.version)
    }

    @Test
    fun `the environment overrides the defaults`() {
        val config = Config.fromEnv(mapOf("HOST" to "127.0.0.1", "PORT" to "9090", "APP_VERSION" to "1.0.0"))

        assertEquals("127.0.0.1", config.host)
        assertEquals(9090, config.port)
        assertEquals("1.0.0", config.version)
    }

    @Test
    fun `an invalid port is rejected with the value in the message`() {
        for (port in listOf("abc", "0", "65536", "-1", "")) {
            val error = assertFailsWith<IllegalArgumentException>(port) { Config.fromEnv(mapOf("PORT" to port)) }

            assertTrue(error.message!!.contains("\"$port\""), error.message)
        }
    }
}
