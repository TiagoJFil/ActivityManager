package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Sport
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.utils.SportID

class SportDataMemRepository : SportRepository {

    private val sportsMap = mutableMapOf<SportID, Sport>()

    override fun addSport(sport: Sport) {
        sportsMap[sport.id] = sport
    }

    override fun getSports(): List<Sport> = sportsMap.values.toList()

    override fun getSportByID(sportID: SportID): Sport? {
        TODO("Not yet implemented")
    }
}
