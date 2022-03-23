package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Route
import pt.isel.ls.entities.Sport
import pt.isel.ls.entities.SportID
import pt.isel.ls.repository.SportRepository

class SportDataMemRepository: SportRepository {

    private val sportsMap = mutableMapOf<SportID, Sport>()

    override fun addSport(sport: Sport) {
        TODO("Not yet implemented")
    }

    override fun getSports(): List<Sport> = sportsMap.values.toList()

    override fun getSportByID(sportID: SportID): Sport? {
        TODO("Not yet implemented")
    }
}