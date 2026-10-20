package {{.Inputs.package.Value}}.db

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object Items : Table("items") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 255)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Item(val id: Int, val name: String)

@Serializable
data class NewItem(val name: String)

fun listItems(database: Database): List<Item> =
    transaction(database) {
        Items.selectAll().map { Item(it[Items.id], it[Items.name]) }
    }

fun addItem(
    database: Database,
    name: String,
): Item =
    transaction(database) {
        val id = Items.insert { it[Items.name] = name }[Items.id]
        Item(id, name)
    }
