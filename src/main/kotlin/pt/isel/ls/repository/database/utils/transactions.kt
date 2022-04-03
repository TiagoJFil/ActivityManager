package pt.isel.ls.repository.database.utils

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


inline fun  <R> Connection.transaction(block: () -> R): R{
    this.autoCommit = false
    try {
        val rv = block()
        this.commit()
        return rv
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