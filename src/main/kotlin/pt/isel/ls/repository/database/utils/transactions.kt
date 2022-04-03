package pt.isel.ls.repository.database.utils

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

/**
 * Creates a transaction on the given connection.
 *
 * @param block the block to execute in the transaction
 *
 */
inline fun <R> Connection.transaction(block: Connection.() -> R): R
    = use{
        this.autoCommit = false
        try {
            return this.block().also { commit() }
        } catch (e: SQLException) {
            this.rollback()
            throw e // TODO: Throw DatabaseException
        } finally {
            this.autoCommit = true
        }
    }


fun PreparedStatement.generatedKey(): String {
    generatedKeys.use {
        if(!it.next()) throw SQLException("No generated key")
        return it.getInt(1).toString()
    }
}