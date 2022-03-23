package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Sport
import pt.isel.ls.entities.SportID
import pt.isel.ls.repository.SportRepository

class SportDataMemRepository: SportRepository {
    override fun addSport(sport: Sport) {
        TODO("Not yet implemented")
    }

    override fun getSports(): List<Sport> {
        TODO("Not yet implemented")
    }

    override fun getSportByID(sportID: SportID): Sport? {
        TODO("Not yet implemented")
    }
}