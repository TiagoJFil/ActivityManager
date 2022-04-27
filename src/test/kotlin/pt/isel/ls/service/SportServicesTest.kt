package pt.isel.ls.service

import org.junit.After
import org.junit.Test
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testSport
import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.service.toDTO
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SportServicesTest {

    private var sportsServices = TEST_ENV.sportsServices

    @After
    fun tearDown() {
        sportsServices = TEST_ENV.sportsServices
    }

    @Test
    fun `get all the sports list`() {
        assertEquals(listOf(testSport.toDTO()), sportsServices.getSports(PaginationInfo(10, 0)))
    }

    @Test
    fun `get a sport`() {
        val sportID = sportsServices.createSport(GUEST_TOKEN, "Football", "A game played with feet")
        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", "A game played with feet", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `create a sport without name throws MissingParam`() {

        assertFailsWith<MissingParameter> {
            sportsServices.createSport(GUEST_TOKEN, null, "A game played with feet")
        }
    }

    @Test
    fun `create a sport with empty name throws InvalidParam`() {

        assertFailsWith<InvalidParameter> {
            sportsServices.createSport(GUEST_TOKEN, "", "A game played with feet")
        }
    }

    @Test
    fun `create a sport without description is allowed`() {
        val sportID = sportsServices.createSport(GUEST_TOKEN, "Football", null)
        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", null, guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `create a sport with blank description is allowed`() {
        val sportID = sportsServices.createSport(GUEST_TOKEN, "Football", "")
        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", null, guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `create multiple sports with same name is allowed`() {
        val sportID1 = sportsServices.createSport(GUEST_TOKEN, "Football", "A game played with feet")
        val sportID2 = sportsServices.createSport(GUEST_TOKEN, "Football", "A game played with feet")
        val sport1 = sportsServices.getSport(sportID1.toString())
        val sport2 = sportsServices.getSport(sportID2.toString())
        val expected1 = SportDTO(sportID1, "Football", "A game played with feet", guestUser.id)
        val expected2 = SportDTO(sportID2, "Football", "A game played with feet", guestUser.id)
        assertEquals(expected1, sport1)
        assertEquals(expected2, sport2)
    }

    @Test
    fun `create 1000 sports checking if they are in getAll`() {
        // val

        val sports = (1..1000).map { i ->
            val sportID = sportsServices.createSport(GUEST_TOKEN, "Football$i", "A game played with feet")
            sportsServices.getSport(sportID.toString())
        }
        val allSports = listOf(testSport.toDTO()) + sports
        assertEquals(1001, sportsServices.getSports(PaginationInfo(10000, 0)).size)
        assertEquals(allSports, sportsServices.getSports(PaginationInfo(1002, 0)))
    }
}
