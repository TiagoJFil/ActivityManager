package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.services.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

class SportDBRepository(private val dataSource: PGSimpleDataSource) : SportRepository {
    /**
     * Adds a new sport to the repository.
     *
     * @param name The sport's name.
     * @param description The sport's description(optional).
     * @param userID The user's id.
     */
    override fun addSport(name: String, description: String?, userID: UserID) : SportID {
        val sportId : SportID
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                    """INSERT INTO sport(name,description,"user") VALUES (?,?,?)"""
            ).use { it ->
                it.setString(1, name)
                it.setString(2, description)
                it.setInt(3, userID.toInt())
                it.executeUpdate()
                it.generatedKeys.use {
                    it.next()
                    sportId = it.getInt(1).toString()
                }
            }
        }
        return sportId
    }

    /**
     * Gets all the sports in the repository.
     */
    override fun getSports(): List<Sport> {
        TODO("Not yet implemented")
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