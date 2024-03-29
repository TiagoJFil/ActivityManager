package pt.isel.ls.service

import org.junit.After
import org.junit.Test
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testSport
import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.service.entities.Sport.Companion.MAX_NAME_LENGTH
import pt.isel.ls.service.inputs.SportInputs.SportCreateInput
import pt.isel.ls.service.inputs.SportInputs.SportUpdateInput
import pt.isel.ls.service.inputs.UserInputs.UserCreateInput
import pt.isel.ls.service.transactions.InMemoryTransactionScope
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.service.toDTO
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SportServicesTest {

    private var sportsServices = TEST_ENV.sportsServices
    private var userServices = TEST_ENV.userServices

    @After
    fun tearDown() {
        InMemoryTransactionScope.reset()
    }

    @Test
    fun `get all the sports list`() {
        assertEquals(listOf(testSport.toDTO()), sportsServices.getSports(null, PaginationInfo(10, 0)))
    }

    @Test
    fun `get a sport`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                "A game played with feet"
            )
        )
        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", "A game played with feet", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `create a sport without name throws MissingParam`() {

        assertFailsWith<MissingParameter> {
            sportsServices.createSport(
                GUEST_TOKEN,
                SportCreateInput(
                    null,
                    "A game played with feet"
                )
            )
        }
    }

    @Test
    fun `create a sport with empty name throws InvalidParam`() {

        assertFailsWith<InvalidParameter> {
            sportsServices.createSport(
                GUEST_TOKEN,
                SportCreateInput(
                    "",
                    "A game played with feet"
                )
            )
        }
    }

    @Test
    fun `create a sport without description is allowed`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                null
            )
        )
        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", null, guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `create a sport with blank description is allowed`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                ""
            )
        )
        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", null, guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `create multiple sports with same name is allowed`() {
        val sportID1 = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                "A game played with feet"
            )

        )
        val sportID2 = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                "A game played with feet"
            )
        )
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
            val sportID = sportsServices.createSport(
                GUEST_TOKEN,
                SportCreateInput(
                    "Football$i",
                    "A game played with feet"
                )
            )
            sportsServices.getSport(sportID.toString())
        }
        val allSports = listOf(testSport.toDTO()) + sports
        assertEquals(1001, sportsServices.getSports(null, PaginationInfo(10000, 0)).size)
        assertEquals(allSports, sportsServices.getSports(null, PaginationInfo(1002, 0)))
    }

    @Test
    fun `get sports with search query list`() {
        val sportID1 = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Basketball",
                "Game played with hands"
            )
        )
        val sportID2 = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                "Game played with feet"
            )
        )
        val sportID3 = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Footvolley",
                "Game played with hands"
            )
        )
        val sportList = sportsServices.getSports("foot", PaginationInfo(10, 0))
        val sportList2 = sportsServices.getSports("hands", PaginationInfo(10, 0))
        val sport1 = SportDTO(sportID1, "Basketball", "Game played with hands", guestUser.id)
        val sport2 = SportDTO(sportID2, "Football", "Game played with feet", guestUser.id)
        val sport3 = SportDTO(sportID3, "Footvolley", "Game played with hands", guestUser.id)
        assertEquals(listOf(sport2, sport3), sportList)
        assertEquals(listOf(sport1, sport3), sportList2)
    }

    @Test
    fun `update sport's name and description`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                null
            )
        )
        sportsServices.updateSport(
            GUEST_TOKEN,
            SportUpdateInput(
                sportID.toString(),
                "Basketball",
                "A game played with hands"
            )
        )

        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Basketball", "A game played with hands", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport's description`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                null
            )
        )
        sportsServices.updateSport(
            GUEST_TOKEN,
            SportUpdateInput(
                sportID.toString(),
                "Football",
                "A game played with feet"
            )
        )

        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", "A game played with feet", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport's name without description keeps the previous description`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                "A game played with feet"
            )
        )
        sportsServices.updateSport(
            GUEST_TOKEN,
            SportUpdateInput(
                sportID.toString(),
                "Basketball",
                null
            )
        )

        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Basketball", "A game played with feet", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport's description without name keeps the previous name`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                "A game played with feet"
            )
        )
        sportsServices.updateSport(
            GUEST_TOKEN,
            SportUpdateInput(
                sportID.toString(),
                null,
                "A game played with hands"
            )
        )

        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", "A game played with hands", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `not updating anything keeps the previous sport`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                "A game played with feet"
            )
        )
        sportsServices.updateSport(
            GUEST_TOKEN,
            SportUpdateInput(
                sportID.toString(),
                null,
                null
            )
        )

        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Football", "A game played with feet", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport with user that didn't create but exists it throws AuthorizationError`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                null
            )
        )
        val (token, _) = userServices.createUser(
            UserCreateInput(
                "John",
                "john@email.com",
                "password"
            )
        )

        assertFailsWith<AuthorizationError> {
            sportsServices.updateSport(
                token,
                SportUpdateInput(
                    sportID.toString(),
                    "Basketball",
                    "A game played with hands"
                )
            )
        }
    }

    @Test
    fun `update sport that does not exists`() {
        assertFailsWith<ResourceNotFound> {
            sportsServices.updateSport(
                GUEST_TOKEN,
                SportUpdateInput(
                    "90990",
                    "Basketball",
                    "A game played with hands"
                )
            )
        }
    }

    @Test
    fun `update sport with more than MAX_NAME_LENGTH characters throws InvalidParameter`() {

        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                null
            )
        )
        val name = "".padEnd(MAX_NAME_LENGTH + 1, 'a')

        assertFailsWith<InvalidParameter> {
            sportsServices.updateSport(
                GUEST_TOKEN,
                SportUpdateInput(
                    sportID.toString(),
                    name,
                    null
                )
            )
        }
    }

    @Test
    fun `update sport with a blank description erases it`() {
        val sportID = sportsServices.createSport(
            GUEST_TOKEN,
            SportCreateInput(
                "Football",
                "A game played with feet"
            )
        )
        sportsServices.updateSport(
            GUEST_TOKEN,
            SportUpdateInput(
                sportID.toString(),
                "Basketball",
                ""
            )
        )

        val sport = sportsServices.getSport(sportID.toString())
        val expected = SportDTO(sportID, "Basketball", null, guestUser.id)
        assertEquals(expected, sport)
    }
}
