package pt.isel.ls.http.utils

import pt.isel.ls.http.getApiRoutes
import pt.isel.ls.http.sportsRoutes
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.services.SportsServices

class SportIntegrationTest {

    val sportServices = SportsServices(SportDataMemRepository())
    val backend = getApiRoutes(sportsRoutes(sportServices))


}