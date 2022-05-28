package pt.isel.ls.repository.memory

import pt.isel.ls.repository.SportRepository
import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.service.applyPagination

class SportDataMemRepository(testSport: Sport) : SportRepository {

    private var currentID = 0

    /**
     * Mapping between [SportID] and [Sport]
     */
    private val sportsMap = mutableMapOf<Int, Sport>(testSport.id to testSport)

    /**
     * Adds a new sport to the repository.
     *
     * @param name The sport's name.
     * @param description The sport's description(optional).
     * @param userID The user's id.
     */
    override fun addSport(name: String, description: String?, userID: UserID): SportID {
        val sportID = ++currentID
        val sport = Sport(sportID, name, description, userID)
        sportsMap[sportID] = sport
        return sportID
    }

    /**
     * Gets all the sports.
     *
     * @return [List] of [SportDTO]
     */
    override fun getSports(search: String?, paginationInfo: PaginationInfo): List<Sport> {
        val filteredSports = if (search == null) {
            sportsMap.values
        } else {
            sportsMap.values.filter { sport ->
                sport.name.contains(search, ignoreCase = true) || sport.description?.contains(search, ignoreCase = true) == true
            }
        }

        return filteredSports
            .toList()
            .applyPagination(paginationInfo)
    }

    /**
     * @param sportID the unique number that identifies the sport
     * @return A [SportDTO] object or null if there is no sport identified by the [sportID]
     */
    override fun getSport(sportID: SportID): Sport? = sportsMap[sportID]

    /**
     * Checks if a sport identified by [sportID] exists.
     */
    override fun hasSport(sportID: SportID): Boolean = sportsMap.containsKey(sportID)

    /**
     * Updates a sport in the repository.
     * @param sid The id of the sport to be updated.
     * @param newName The sport's name. or null if it should not be updated.
     * @param newDescription The sport's description or null if it should not be updated.
     */
    override fun updateSport(sid: SportID, newName: String?, newDescription: String?): Boolean {
        val sport = sportsMap[sid] ?: return false
        val name = newName ?: sport.name
        val description = newDescription ?: sport.description
        sportsMap[sid] = sport.copy(name = name, description = description?.ifBlank { null })
        return true
    }
}
