package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.repository.applyPagination
import pt.isel.ls.utils.repository.generatedKey
import pt.isel.ls.utils.repository.ifNext
import pt.isel.ls.utils.repository.setSport
import pt.isel.ls.utils.repository.toListOf
import pt.isel.ls.utils.repository.toSport
import pt.isel.ls.utils.repository.transaction
import java.sql.ResultSet
import java.sql.Statement

class SportDBRepository(private val dataSource: PGSimpleDataSource, suffix: String) : SportRepository {

    private val sportTable = "sport$suffix"

    /**
     * Adds a new sport to the repository.
     *
     * @param name The sport's name.
     * @param description The sport's description(optional).
     * @param userID The user's id.
     */
    override fun addSport(name: String, description: String?, userID: UserID): SportID =
        dataSource.connection.transaction {
            val insertSport = """INSERT INTO $sportTable (name, description, "user") VALUES (?, ?, ?)"""
            val statement = prepareStatement(insertSport, Statement.RETURN_GENERATED_KEYS)
            statement.apply {
                setSport(name, description, userID)
                executeUpdate()
            }
            statement.generatedKey()
        }

    /**
     * Gets all the sports in the repository.
     */
    override fun getSports(paginationInfo: PaginationInfo): List<Sport> =
        dataSource.connection.transaction {
            val query = """SELECT * FROM $sportTable LIMIT ? OFFSET ?"""
            prepareStatement(query).use { stmt ->
                stmt.applyPagination(paginationInfo, indexes = Pair(1, 2))
                stmt.executeQuery().use { rs ->
                    rs.toListOf<Sport>(ResultSet::toSport)
                }
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
            if(newDescription != null)
                queryBuilder.append(", ")
        }
        if (newDescription != null)
            queryBuilder.append("description = ?")

        queryBuilder.append("WHERE id = ?")
        val nameIndex = if (newName != null) 1 else 0
        val sidIndex = when {
            newName != null && newDescription != null -> 3
            newName != null || newDescription != null -> 2
            else -> 1
        }

        return dataSource.connection.transaction {
            prepareStatement(queryBuilder.toString()).use { stmt ->
                if (newName != null)
                    stmt.setString(nameIndex, newName)
                if (newDescription != null)
                    stmt.setString(nameIndex + 1, newDescription)
                stmt.setInt(sidIndex, sid)
                stmt.executeUpdate() == 1
            }
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
        dataSource.connection.transaction {
            val pstmt = prepareStatement("""SELECT * FROM $sportTable WHERE id = ?;""")
            pstmt.use { ps ->
                ps.setInt(1, sportID)
                val resultSet: ResultSet = ps.executeQuery()
                resultSet.use { block(it) }
            }
        }
}
