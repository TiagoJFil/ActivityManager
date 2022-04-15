package pt.isel.ls.utils.repository

import pt.isel.ls.utils.ID
import pt.isel.ls.utils.api.PaginationInfo
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class DataBaseAccessException(message: String) : Exception(message) // TODO: Fazer InternalException do services

/**
 * Tries to execute the given [operation] if it fails throws [DataBaseAccessException]
 */
inline fun <T> tryDataBaseOperation(operation: () -> T): T {
    return try {
        operation()
    } catch (e: Exception) {
        throw DataBaseAccessException("Error while accessing the database: ${e.stackTraceToString()}")
    }
}

/**
 * Sets the pagination information on the given [PreparedStatement].
 */
fun PreparedStatement.applyPagination(paginationInfo: PaginationInfo, indexes: Pair<Int, Int>) {
    setInt(indexes.first, paginationInfo.limit)
    setInt(indexes.second, paginationInfo.offset)
}


/**
 * Creates a transaction on the given connection properly allocating and disposing connection resources.
 * Resource management for [Statement]s and [ResultSet]s have to be done by the caller in [block].
 *
 * Commits on success.
 * Rolls back if an exception is thrown propagating it afterwards.
 *
 * @param block the block to execute in the transaction
 * @return the result of the block function invoked in the transaction
 * @throws DataBaseAccessException if an error occurs while accessing the database
 */
inline fun <R> Connection.transaction(block: Connection.() -> R): R = tryDataBaseOperation {
    use {
        this.autoCommit = false
        try {
            this.block()
                .also { commit() }
        } catch (e: Exception) {
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
 */ // TODO : TROCAR PARA Ficheiro statements.kt
fun PreparedStatement.generatedKey(): ID {
    generatedKeys.use {
        if (!it.next()) throw IllegalStateException("No generated key")
        return it.getInt(1)
    }
}

/**
 * Sets the pagination information on the given [PreparedStatement].
 */
fun PreparedStatement.applyPagination(paginationInfo: PaginationInfo, indexes: Pair<Int, Int>) {
    setInt(indexes.first, paginationInfo.limit)
    setInt(indexes.second, paginationInfo.offset)
}
