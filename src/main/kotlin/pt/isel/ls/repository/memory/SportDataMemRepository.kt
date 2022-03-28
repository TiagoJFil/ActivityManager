package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Sport
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.utils.SportID

class SportDataMemRepository: SportRepository {

    private val sportsMap = mutableMapOf<SportID, Sport>()

    /**
     * Adds a sport.
     *
     * @param sport the sport to add
     */
    override fun addSport(sport: Sport) {
        sportsMap[sport.id] = sport
    }


    /**
     * Gets all the sports.
     *
     * @return [List] of [Sport]
     */
    override fun getSports(): List<Sport> = sportsMap.values.toList()

    /**
     * @param sportID the unique number that identifies the sport
     * @return A [Sport] object or null if there is no sport identified by the [sportID]
     */
    override fun getSportByID(sportID: SportID): Sport? = sportsMap[sportID]

    /**
     * Checks if a sport identified by [sportID] exists.
     */
    override fun hasSport(sportID: SportID): Boolean = sportsMap.containsKey(sportID)

}