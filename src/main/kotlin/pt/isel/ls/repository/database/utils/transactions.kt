package pt.isel.ls.repository.database.utils

import java.sql.*

class DataBaseAccessException(message: String) : RuntimeException(message)

/**
 * Tries to execute the given [operation] if it fails, it will throw a [DataBaseAccessException]
 */
inline fun <T> tryDataBaseOperation(operation: () -> T): T{
    return try {
        operation()
    } catch (e: SQLException) {
        throw DataBaseAccessException("Error while accessing the database: ${e.message}")
    }
}

/**
 * Creates a transaction on the given connection properly allocating and disposing connection resources.
 * Resource management for [Statement]s and [ResultSet]s have to be done by the caller in the [block].
 *
 * Commits on success.
 * Rolls back if an exception is thrown propagating it afterwards.
 *
 * @param block the block to execute in the transaction
 * @return the result of the block function invoked in the transaction
 * @throws DataBaseAccessException if an error occurs while accessing the database
 */
inline fun <R> Connection.transaction(block: Connection.() -> R): R
    = tryDataBaseOperation{
        use {
            this.autoCommit = false
            try {
                this.block()
                    .also { commit() }
            } catch (e: SQLException) {
                this.rollback()
                throw e
            } finally {
                this.autoCommit = true
            }
        }
    }

/**
 * Gets the last generated key from the given [PreparedStatement].
 * Called after a successful insertion.
 */
fun PreparedStatement.generatedKey(): String {
    generatedKeys.use {
        if(!it.next()) throw SQLException("No generated key")
        return it.getInt(1).toString()
    }
}