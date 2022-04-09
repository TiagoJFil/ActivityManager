package pt.isel.ls.repository.memory

import pt.isel.ls.repository.SportRepository
import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.service.entities.Sport
import pt.isel.ls.service.generateRandomId
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.UserID

class SportDataMemRepository(testSport: Sport) : SportRepository {

    /**
     * Mapping between [SportID] and [Sport]
     */
    private val sportsMap = mutableMapOf<SportID, Sport>(testSport.id to testSport)

    /**
     * Adds a new sport to the repository.
     *
     * @param name The sport's name.
     * @param description The sport's description(optional).
     * @param userID The user's id.
     */
    override fun addSport(name: String, description: String?, userID: UserID): SportID {
        val sportID = generateRandomId()
        val sport = Sport(sportID, name, description, userID)
        sportsMap[sportID] = sport
        return sportID
    }

    /**
     * Gets all the sports.
     *
     * @return [List] of [SportDTO]
     */
    override fun getSports(): List<Sport> = sportsMap.values.toList()

    /**
     * @param sportID the unique number that identifies the sport
     * @return A [SportDTO] object or null if there is no sport identified by the [sportID]
     */
    override fun getSportByID(sportID: SportID): Sport? = sportsMap[sportID]

    /**
     * Checks if a sport identified by [sportID] exists.
     */
    override fun hasSport(sportID: SportID): Boolean = sportsMap.containsKey(sportID)
}
