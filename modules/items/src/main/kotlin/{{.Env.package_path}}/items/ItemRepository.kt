package {{.Inputs.package.Value}}.items

import org.apache.logging.log4j.kotlin.logger
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

private object Items : Table("items") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 255)

    override val primaryKey = PrimaryKey(id)
}

/**
 * The data of the items domain. The module owns its database, so Exposed stays behind this class
 * and nothing outside of the module sees it.
 */
internal class ItemRepository private constructor(
    private val database: Database,
) {
    fun list(): List<Item> =
        transaction(database) {
            Items.selectAll().map { Item(it[Items.id], it[Items.name]) }
        }

    fun add(name: String): Item =
        transaction(database) {
            val id = Items.insert { it[Items.name] = name }[Items.id]
            Item(id, name)
        }

    companion object {
        /** Connects to the SQLite database at [url] and creates the tables that are missing. */
        fun connect(url: String): ItemRepository {
            logger.info { "Items are stored in $url" }
            val database = Database.connect(url, driver = "org.sqlite.JDBC")
            transaction(database) {
                SchemaUtils.create(Items)
            }
            return ItemRepository(database)
        }
    }
}
