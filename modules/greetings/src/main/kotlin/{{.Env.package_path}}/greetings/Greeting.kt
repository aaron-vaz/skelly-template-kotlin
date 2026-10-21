package {{.Inputs.package.Value}}.greetings

import kotlinx.serialization.Serializable

@Serializable
data class Greeting(
    val message: String,
)
