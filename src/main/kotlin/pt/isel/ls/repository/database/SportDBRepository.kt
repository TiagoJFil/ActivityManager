package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.database.utils.*
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
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
                setSport(name, description, userID.toInt())
                executeUpdate()
            }
            statement.generatedKey()
        }

    /**
     * Gets all the sports in the repository.
     */
    override fun getSports(): List<Sport> =
        dataSource.connection.transaction {
            createStatement().use { stmt ->
                stmt.executeQuery("""SELECT * FROM $sportTable""").use { rs ->
                    rs.toListOf<Sport>(ResultSet::toSport)
                }
            }
        }

    /**
     * Gets a sport by its id.
     * @param sportID The id of the sport to be retrieved.
     * @return [Sport] The sport with the given id or null if it does not exist.
     */
    override fun getSportByID(sportID: SportID): Sport? =
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
                ps.setInt(1, sportID.toInt())
                val resultSet: ResultSet = ps.executeQuery()
                resultSet.use { block(it) }
            }
        }
}
