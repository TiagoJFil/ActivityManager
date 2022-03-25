package pt.isel.ls.services

import org.junit.Test
import pt.isel.ls.entities.Sport
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.utils.guestUser
import kotlin.test.assertEquals

class SportServicesTest {

    val sportsServices = SportsServices(SportDataMemRepository())

    @Test
    fun `get all the sports list`(){
        assertEquals(emptyList(), sportsServices.getSports())
    }

    @Test
    fun `get a sport`(){
        val sportID = sportsServices.createSport(guestUser.id, "Football", "A game played with feet")
        val sport = sportsServices.getSport(sportID)
        val expected = Sport(sportID,"Football", "A game played with feet", guestUser.id)
        assertEquals(expected,sport)
    }
}