package pt.isel.ls.services

import pt.isel.ls.repository.memory.SportDataMemRepository

class SportServicesTest {

    val sportsServices = SportsServices(SportDataMemRepository())
}