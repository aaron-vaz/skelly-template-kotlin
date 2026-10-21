package {{.Inputs.package.Value}}.items

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Int,
    val name: String,
)

@Serializable
data class NewItem(
    val name: String,
)
