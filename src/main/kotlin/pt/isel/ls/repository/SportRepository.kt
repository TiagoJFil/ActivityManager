package pt.isel.ls.repository

import pt.isel.ls.entities.Sport
import pt.isel.ls.utils.SportID

interface SportRepository {

    fun addSport(sport: Sport)

    fun getSports(): List<Sport>

    fun getSportByID(sportID: SportID): Sport?
}
