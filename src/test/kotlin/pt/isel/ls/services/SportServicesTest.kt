package pt.isel.ls.services

import org.junit.Test
import pt.isel.ls.entities.Sport
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.utils.guestUser
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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


    @Test
    fun `create a sport without name throws MissingParam`(){

        assertFailsWith<MissingParameter> {
            sportsServices.createSport(guestUser.id, null, "A game played with feet")
        }

    }

    @Test
    fun `create a sport with empty name throws InvalidParam`(){

        assertFailsWith<InvalidParameter> {
            sportsServices.createSport(guestUser.id, "", "A game played with feet")
        }

    }

    @Test
    fun `create a sport without description is allowed`(){
        val sportID = sportsServices.createSport(guestUser.id, "Football", null)
        val sport = sportsServices.getSport(sportID)
        val expected = Sport(sportID,"Football", null, guestUser.id)
        assertEquals(expected,sport)
    }

    @Test
    fun `create a sport with blank description is allowed`(){
        val sportID = sportsServices.createSport(guestUser.id, "Football", "")
        val sport = sportsServices.getSport(sportID)
        val expected = Sport(sportID,"Football", null, guestUser.id)
        assertEquals(expected,sport)
    }

    @Test
    fun `create multiple sports with same name is allowed`(){
        val sportID1 = sportsServices.createSport(guestUser.id, "Football", "A game played with feet")
        val sportID2 = sportsServices.createSport(guestUser.id, "Football", "A game played with feet")
        val sport1 = sportsServices.getSport(sportID1)
        val sport2 = sportsServices.getSport(sportID2)
        val expected1 = Sport(sportID1,"Football", "A game played with feet", guestUser.id)
        val expected2 = Sport(sportID2,"Football", "A game played with feet", guestUser.id)
        assertEquals(expected1,sport1)
        assertEquals(expected2,sport2)
    }

    @Test
    fun `create 1000 sports checking if they are in getAll`(){
        val sports = (1..1000).map { i ->
            val sportID = sportsServices.createSport(guestUser.id, "Football$i", "A game played with feet")
            sportsServices.getSport(sportID)
        }
        assertEquals(1000,sportsServices.getSports().size)
        assertEquals(sports,sportsServices.getSports())
    }




}