package pt.isel.ls.services

import pt.isel.ls.entities.Route
import pt.isel.ls.entities.Sport
import pt.isel.ls.http.sportsRoutes
import pt.isel.ls.repository.SportRepository

class SportsServices(
    val sportsRepository: SportRepository
) {
    fun getSports(): List<Sport> =
        sportsRepository.getSports()



}
