package pt.isel.ls.repository

import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

interface SportRepository {

    /**
     * Adds a new sport to the repository.
     *
     * @param name The sport's name.
     * @param description The sport's description(optional).
     * @param userID The user's id.
     */
    fun addSport(name: String, description: String?, userID: UserID): SportID

    /**
     * Gets all the sports in the repository.
     */
    fun getSports(): List<Sport>

    /**
     * Gets a sport by its id.
     * @param sportID The id of the sport to be retrieved.
     * @return [Sport] The sport with the given id or null if it does not exist.
     */
    fun getSportByID(sportID: SportID): Sport?

    /**
     * Checks if a sport with the given id exists.
     * @param sportID The id of the sport to be checked.
     * @return [Boolean] True if the sport exists, false otherwise.
     */
    fun hasSport(sportID: SportID): Boolean
}
