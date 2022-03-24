package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Sport
import pt.isel.ls.entities.SportID
import pt.isel.ls.repository.SportRepository



class SportDataMemRepository: SportRepository {

    private val sportsMap = mutableMapOf<SportID, Sport>()

    /**
     * Adds a sport.
     *
     * @param sport the sport to add
     */
    override fun addSport(sport: Sport) {
        TODO("Not yet implemented")
    }


    /**
     * Gets all the sports.
     *
     * @return [List] of [Sport]
     */
    override fun getSports(): List<Sport> = sportsMap.values.toList()

    /**
     * @sportID the unique number that identifies the sport
     * @return A [Sport] object or null if there is no sport identified by the [sportID]
     */
    override fun getSportByID(sportID: SportID): Sport? = sportsMap[sportID]
}