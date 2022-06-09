package pt.isel.ls.utils.repository

import pt.isel.ls.utils.ID
import pt.isel.ls.utils.api.PaginationInfo
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

val routeTable = "Route"
val sportTable = "Sport"
val activityTable = "Activity"
val userTable = """"User""""
val emailTable = "Email"
val tokenTable = "Token"

/**
 * Gets the last generated key from the given [PreparedStatement].
 * Called after a successful insertion.
 */
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

/**
 * Gets a row that is identified by the given id and calls the given [block] with the resultSet of the row.
 *
 * @param id an [ID] that identifies the row
 * @param table the name of the table to query
 * @param block the block to execute with the resultSet of the row
 */
fun <T> Connection.queryTableByID(id: ID, table: String, block: (ResultSet) -> T): T =
    prepareStatement("""SELECT * FROM $table WHERE id = ?""").use { ps ->
        ps.setInt(1, id)
        val resultSet: ResultSet = ps.executeQuery()
        resultSet.use { block(it) }
    }
