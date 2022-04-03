package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.database.utils.generatedKey
import pt.isel.ls.repository.database.utils.toSport
import pt.isel.ls.repository.database.utils.transaction
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import java.sql.ResultSet
import java.sql.Statement


class SportDBRepository(private val dataSource: PGSimpleDataSource) : SportRepository {

    /**
     * Adds a new sport to the repository.
     *
     * @param name The sport's name.
     * @param description The sport's description(optional).
     * @param userID The user's id.
     */
    override fun addSport(name: String, description: String?, userID: UserID): SportID =
        dataSource.connection.use { connection ->
            val insertSport = """INSERT INTO sport (name, description, "user") VALUES (?, ?, ?)"""
            connection.transaction {
                val statement = connection.prepareStatement(insertSport, Statement.RETURN_GENERATED_KEYS)
                statement.apply {
                    setString(1, name)
                    setString(2, description)
                    setInt(3, userID.toInt())
                    executeUpdate()
                }
                statement.generatedKey()
            }
        }

    /**
     * Gets all the sports in the repository.
     */
    override fun getSports(): List<Sport> {
        val sports: MutableList<Sport> = mutableListOf()
        dataSource.connection.transaction {
            createStatement().use { stmt ->
                stmt.executeQuery("""SELECT * FROM sport""")
                stmt.resultSet.use { rs ->
                    while (rs.next()) {
                        sports.add(rs.toSport())
                    }
                }
            }
        }
        return sports
    }

    /**
     * Gets a sport by its id.
     * @param sportID The id of the sport to be retrieved.
     * @return [Sport] The sport with the given id or null if it does not exist.
     */
    override fun getSportByID(sportID: SportID): Sport? =
        querySportByID(sportID) { rs: ResultSet ->
            if (!rs.next())
                null
            else
                rs.toSport()
        }

    /**
     * Checks if a sport with the given id exists.
     * @param sportID The id of the sport to be checked.
     * @return [Boolean] True if the sport exists, false otherwise.
     */
    override fun hasSport(sportID: SportID): Boolean = querySportByID(sportID) { it.next() }


    /**
     * Makes a query to get a sport by its identifier.
     *
     * @param sportID The id of the sport to be queried.
     * @param block the block to be executed with respective result set.
     * @return [T] The result of calling the block function.
     */
    private fun <T> querySportByID(sportID: SportID, block: (ResultSet) -> T): T =
        dataSource.connection.use { connection ->
            connection.transaction {
                val pstmt = connection.prepareStatement(
                    """SELECT * FROM sport WHERE id = ?;"""
                )
                pstmt.use { ps ->
                    ps.setInt(1, sportID.toInt())
                    val resultSet: ResultSet = ps.executeQuery()
                    resultSet.use { block(it) }
                }
            }
        }

}