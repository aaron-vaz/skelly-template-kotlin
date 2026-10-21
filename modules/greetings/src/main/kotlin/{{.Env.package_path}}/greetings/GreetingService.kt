package {{.Inputs.package.Value}}.greetings

import org.apache.logging.log4j.kotlin.logger

/**
 * Raised for a name the domain does not greet; the routes turn it into a bad request.
 */
class InvalidNameException(
    message: String,
) : IllegalArgumentException(message)

/**
 * The logic of the greetings domain, which knows nothing about HTTP.
 */
class GreetingService {
    fun greet(name: String): Greeting {
        if (name.isBlank() || name.length > MAX_NAME_LENGTH) {
            logger.debug { "Rejected a name of ${name.length} characters" }
            throw InvalidNameException("name must be 1 to $MAX_NAME_LENGTH characters")
        }
        return Greeting("Hello, ${name.trim()}!")
    }

    private companion object {
        const val MAX_NAME_LENGTH = 100
    }
}
