package pt.isel.ls.repository.db

import pt.isel.ls.entities.Sport
import pt.isel.ls.repository.SportRepository
import pt.isel.ls.utils.SportID



class SportDbRepository: SportRepository {


    override fun addSport(sport: Sport) {
        TODO()
    }

    override fun getSports(): List<Sport> {
        TODO("Not yet implemented")
    }

    override fun getSportByID(sportID: SportID): Sport? {
        TODO("Not yet implemented")
    }

}