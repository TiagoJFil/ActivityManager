package pt.isel.ls.repository.database

import pt.isel.ls.repository.SportRepository
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.repository.applyPagination
import pt.isel.ls.utils.repository.generatedKey
import pt.isel.ls.utils.repository.ifNext
import pt.isel.ls.utils.repository.queryTableByID
import pt.isel.ls.utils.repository.setSport
import pt.isel.ls.utils.repository.sportTable
import pt.isel.ls.utils.repository.toListOf
import pt.isel.ls.utils.repository.toSport
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class SportDBRepository(val connection: Connection) : SportRepository {

    /**
     * Adds a new sport to the repository.
     *
     * @param name The sport's name.
     * @param description The sport's description(optional).
     * @param userID The user's id.
     */
    override fun addSport(name: String, description: String?, userID: UserID): SportID {
        val insertSport = """INSERT INTO $sportTable (name, description, "user") VALUES (?, ?, ?)"""
        val statement = connection.prepareStatement(insertSport, Statement.RETURN_GENERATED_KEYS)
        statement.apply {
            setSport(name, description, userID)
            executeUpdate()
        }
        return statement.generatedKey()
    }

    /**
     * Gets all the sports in the repository.
     */
    override fun getSports(search: String?, paginationInfo: PaginationInfo): List<Sport> {
        val query = buildQuery(search)
        val paginationIndexes = if (search == null) Pair(1, 2) else Pair(2, 3)
        val searchString = if (search == null) "" else "${search.trim()}:*".replace(" ", "&")

        return connection.prepareStatement(query).use { stmt ->
            if (search != null) {
                stmt.setString(1, searchString)
            }
            stmt.applyPagination(paginationInfo, indexes = paginationIndexes)
            stmt.executeQuery().use { rs ->
                rs.toListOf<Sport>(ResultSet::toSport)
            }
        }
    }

    /**
     * Builds the query to get the sports
     *
     * @param search the text that has been introduced for searching purposes
     * @return [String] the query
     */
    private fun buildQuery(search: String?): String {
        return if (search == null)
            """SELECT id, name, description, "user" FROM $sportTable LIMIT ? OFFSET ?"""
        else {
            """SELECT id, name, description, "user" FROM $sportTable """ +
                " WHERE to_tsvector(coalesce(name, '') || ' ' || coalesce(description, '')) @@ to_tsquery(?) " +
                "LIMIT ? OFFSET ?; "
        }
    }

    /**
     * Gets a sport by its id.
     * @param sportID The id of the sport to be retrieved.
     * @return [Sport] The sport with the given id or null if it does not exist.
     */
    override fun getSport(sportID: SportID): Sport? =
        querySportByID(sportID) { rs: ResultSet ->
            rs.ifNext { rs.toSport() }
        }

    /**
     * Checks if a sport with the given id exists.
     * @param sportID The id of the sport to be checked.
     * @return [Boolean] True if the sport exists, false otherwise.
     */
    override fun hasSport(sportID: SportID): Boolean =
        querySportByID(sportID) { rs: ResultSet ->
            rs.next()
        }

    /**
     * Updates a sport in the repository.
     * @param sid The id of the sport to be updated.
     * @param newName The sport's name. or null if it should not be updated.
     * @param newDescription The sport's description or null if it should not be updated.
     */
    override fun updateSport(sid: SportID, newName: String?, newDescription: String?): Boolean {
        val queryBuilder = StringBuilder("UPDATE $sportTable SET ")
        if (newName != null) {
            queryBuilder.append("name = ?")
            if (newDescription != null)
                queryBuilder.append(", ")
        }
        if (newDescription != null)
            queryBuilder.append("description = ?")

        queryBuilder.append("WHERE id = ?")
        val nameIndex = if (newName != null) 1 else 0
        val sidIndex =
            when { // TODO(Refactor so it's possible to add an attribute without having to change index numbers)
                newName != null && newDescription != null -> 3
                newName != null || newDescription != null -> 2
                else -> 1
            }

        return connection.prepareStatement(queryBuilder.toString()).use { stmt ->
            if (newName != null)
                stmt.setString(nameIndex, newName)
            if (newDescription != null)
                stmt.setString(nameIndex + 1, newDescription.ifBlank { null })
            stmt.setInt(sidIndex, sid)
            stmt.executeUpdate() == 1
        }
    }

    /**
     * Makes a query to get a sport by its identifier.
     *
     * @param sportID The id of the sport to be queried.
     * @param block specifies what the caller wants to do with the result set.
     * @return [T] The result of calling the block function.
     */
    private fun <T> querySportByID(sportID: SportID, block: (ResultSet) -> T): T =
        connection.queryTableByID(sportID, sportTable, block)
}
