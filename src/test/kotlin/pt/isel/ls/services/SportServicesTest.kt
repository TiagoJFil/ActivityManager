package pt.isel.ls.services

import org.junit.Test
import pt.isel.ls.repository.memory.SportDataMemRepository
import kotlin.test.assertEquals

class SportServicesTest {

    val sportsServices = SportsServices(SportDataMemRepository())

    @Test
    fun `get all the users list`(){
        assertEquals(emptyList(), sportsServices.getSports())
    }
}