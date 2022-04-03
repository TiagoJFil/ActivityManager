package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.repository.database.utils.generatedKey
import pt.isel.ls.repository.database.utils.transaction
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import java.sql.Statement

class SportDBRepository(private val dataSource: PGSimpleDataSource) : SportRepository {
    /**
     * Adds a new sport to the repository.
     *
     * @param name The sport's name.
     * @param description The sport's description(optional).
     * @param userID The user's id.
     */
    override fun addSport(name: String, description: String?, userID: UserID) : SportID {
        dataSource.connection.use { connection ->
            return connection.transaction{
                connection.prepareStatement("""INSERT INTO sport(name,description,"user") VALUES (?,?,?)""", Statement.RETURN_GENERATED_KEYS).use { ps ->
                    ps.setString(1, name)
                    ps.setString(2, description)
                    ps.setInt(3, userID.toInt())
                    ps.executeUpdate()
                    ps.generatedKey()
                }
            }
        }
    }

    /**
     * Gets all the sports in the repository.
     */
    override fun getSports(): List<Sport> {
        val sports : MutableList<Sport> = mutableListOf()
        dataSource.connection.use { connection ->
            connection.transaction {
                connection.createStatement().use { statement ->
                    statement.executeQuery("""SELECT * FROM sport""")

                    statement.resultSet.use { rs ->
                        while (rs.next()) {
                            sports.add(
                                Sport(
                                    rs.getInt("id").toString(),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    rs.getInt("user").toString()
                                )
                            )
                        }
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
    override fun getSportByID(sportID: SportID): Sport? {
        TODO("Not yet implemented")
    }

    /**
     * Checks if a sport with the given id exists.
     * @param sportID The id of the sport to be checked.
     * @return [Boolean] True if the sport exists, false otherwise.
     */
    override fun hasSport(sportID: SportID): Boolean {
        TODO("Not yet implemented")
    }

}