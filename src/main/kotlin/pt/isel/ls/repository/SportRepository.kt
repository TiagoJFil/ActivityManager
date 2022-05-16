package pt.isel.ls.repository

import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo

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
    fun getSports(paginationInfo: PaginationInfo): List<Sport>

    /**
     * Gets a sport by its id.
     * @param sportID The id of the sport to be retrieved.
     * @return [Sport] The sport with the given id or null if it does not exist.
     */
    fun getSport(sportID: SportID): Sport?

    /**
     * Checks if a sport with the given id exists.
     * @param sportID The id of the sport to be checked.
     * @return [Boolean] True if the sport exists, false otherwise.
     */
    fun hasSport(sportID: SportID): Boolean

    /**
     * Updates a sport in the repository.
     * @param sid The id of the sport to be updated.
     * @param newName The sport's name. or null if it should not be updated.
     * @param newDescription The sport's description or null if it should not be updated.
     */
    fun updateSport(sid: SportID, newName: String?, newDescription: String?): Boolean
}
