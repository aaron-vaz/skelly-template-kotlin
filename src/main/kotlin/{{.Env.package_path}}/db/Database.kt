package {{.Inputs.package.Value}}.db

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Connects to the SQLite database at [url] and creates the tables that are missing.
 */
fun connectDatabase(url: String): Database {
    val database = Database.connect(url, driver = "org.sqlite.JDBC")
    transaction(database) {
        SchemaUtils.create(Items)
    }
    return database
}
