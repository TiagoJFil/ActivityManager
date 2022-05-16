package pt.isel.ls.api

import org.http4k.core.Response
import org.junit.After
import pt.isel.ls.api.SportRoutes.SportInput
import pt.isel.ls.api.SportRoutes.SportListOutput
import pt.isel.ls.api.UserRoutes.UserInput
import pt.isel.ls.api.utils.SPORT_PATH
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.api.utils.authHeader
import pt.isel.ls.api.utils.createSport
import pt.isel.ls.api.utils.createUser
import pt.isel.ls.api.utils.expectBadRequest
import pt.isel.ls.api.utils.expectForbidden
import pt.isel.ls.api.utils.expectNotFound
import pt.isel.ls.api.utils.expectOK
import pt.isel.ls.api.utils.expectUnauthorized
import pt.isel.ls.api.utils.getRequest
import pt.isel.ls.api.utils.postRequest
import pt.isel.ls.api.utils.putRequest
import pt.isel.ls.api.utils.updateResource
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testSport
import pt.isel.ls.service.dto.HttpError
import pt.isel.ls.service.dto.SportDTO
import pt.isel.ls.utils.SportID
import pt.isel.ls.utils.service.toDTO
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class SportApiTests {
    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV))
    }

    @Test fun `get sports without creating returns a list with the testSport list`() {

        val sportList = getRequest<SportListOutput>(testClient, SPORT_PATH, Response::expectOK)
        assertEquals(listOf(testSport.toDTO()), sportList.sports)
    }

    @Test fun `get a specific sport sucessfully`() {
        val sport = testClient.createSport(SportInput("Football", "Game played with feet.")).sportID
        getRequest<SportDTO>(testClient, "${SPORT_PATH}$sport", Response::expectOK)
    }

    @Test fun `get not found error trying to get a sport that does not exist`() {
        val id = 12354
        getRequest<HttpError>(testClient, "${SPORT_PATH}$id", Response::expectNotFound)
    }

    @Test fun `create a sport sucessfully`() {

        testClient.createSport(SportInput("Basketball", "Game played with hands."))
    }

    @Test fun `create a sport without description is allowed`() {

        testClient.createSport(SportInput("Basketball"))
    }

    @Test fun `create a sport with a blank description is allowed`() {

        testClient.createSport(SportInput("Basketball", ""))
    }

    @Test fun `create a sport without name is not allowed giving bad request`() {

        postRequest<SportInput, HttpError>(
            testClient,
            SPORT_PATH,
            SportInput(description = ""),
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create a sport with a blank name is not allowed giving bad request`() {

        postRequest<SportInput, HttpError>(
            testClient,
            SPORT_PATH,
            SportInput(name = "", description = ""),
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test fun `create multiple sports ensuring they're in the list of routes`() {

        val name = "Cricket"
        val description = "Played with a cue"

        val creationBodies = List(1000) { SportInput(name, description) }
        val sportsIds: List<SportID> = creationBodies.map { testClient.createSport(it).sportID }

        val expected = sportsIds.map { SportDTO(id = it, name, description, guestUser.id) }
        val sportList = getRequest<SportListOutput>(testClient, "$SPORT_PATH?limit=1005", Response::expectOK).sports

        expected.forEach { assertContains(sportList, it) }
    }

    @Test
    fun `Cant create a sport with a name that has more than 20 letters`() {
        val name = "1234567890123456789012345678901"
        val description = "Played with a cue"

        postRequest<SportInput, HttpError>(
            testClient,
            SPORT_PATH,
            SportInput(name, description),
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test
    fun `update sport's name and description`() {
        val sportInput = SportInput("Football", "Game played with feet.")
        val sportID = testClient.createSport(sportInput).sportID

        testClient.updateResource<SportInput,SportID>(SportInput("Basketball", "Game played with hands."),sportID)

        val sport = getRequest<SportDTO>(testClient, "$SPORT_PATH$sportID", Response::expectOK)
        val expected = SportDTO(sportID, "Basketball", "Game played with hands.", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport's description`() {
        val sportInput = SportInput("Football", null)
        val sportID = testClient.createSport(sportInput).sportID

        testClient.updateResource<SportInput,SportID>(SportInput(name = "Football", description = "Game played with hands."),sportID)

        val sport = getRequest<SportDTO>(testClient, "$SPORT_PATH$sportID", Response::expectOK)
        val expected = SportDTO(sportID, "Football", "Game played with hands.", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport's name without description keeps the previous description`() {
        val sportInput = SportInput("Football", "Game played with hands.")
        val sportID = testClient.createSport(sportInput).sportID

        testClient.updateResource<SportInput,SportID>(SportInput(name = "Basketball"),sportID)

        val sport = getRequest<SportDTO>(testClient, "$SPORT_PATH$sportID", Response::expectOK)
        val expected = SportDTO(sportID, "Basketball", "Game played with hands.", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport's description without name keeps the previous name`() {
        val sportInput = SportInput("Football", "Game played with hands.")
        val sportID = testClient.createSport(sportInput).sportID

        testClient.updateResource<SportInput,SportID>(SportInput(description = "A game played with feet."),sportID)

        val sport = getRequest<SportDTO>(testClient, "$SPORT_PATH$sportID", Response::expectOK)
        val expected = SportDTO(sportID, "Football", "A game played with feet.", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `not updating anything keeps the previous sport`() {
        val sportInput = SportInput("Football", "Game played with hands.")
        val sportID = testClient.createSport(sportInput).sportID

        testClient.updateResource<SportInput,SportID>(sportInput,sportID)

        val sport = getRequest<SportDTO>(testClient, "$SPORT_PATH$sportID", Response::expectOK)
        val expected = SportDTO(sportID, "Football", "Game played with hands.", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport with a user that didn't create it throws AuthorizationError`() {
        val sportInput = SportInput("Football", "Game played with hands.")
        val sportID = testClient.createSport(sportInput).sportID
        val userToken = testClient.createUser(UserInput(name = "user", email = "random@okay.com")).authToken

        putRequest<SportInput>(
            testClient,
            "$SPORT_PATH$sportID",
            SportInput(),
            authHeader(userToken),
            expectedStatus = Response::expectForbidden
        )

        val sport = getRequest<SportDTO>(testClient, "$SPORT_PATH$sportID", Response::expectOK)
        val expected = SportDTO(sportID, "Football", "Game played with hands.", guestUser.id)
        assertEquals(expected, sport)
    }

    @Test
    fun `update sport that does not exists`() {
        putRequest<SportInput>(
            testClient,
            "${SPORT_PATH}9679076",
            SportInput(),
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectNotFound
        )
    }

    @Test
    fun `update sport with more than MAX_NAME_LENGTH characters throws InvalidParameter`() {

        val sportInput = SportInput("Football", "Game played with hands.")
        val sportID = testClient.createSport(sportInput).sportID
        val name = "".padEnd(pt.isel.ls.service.entities.Sport.MAX_NAME_LENGTH + 1, 'a')

        putRequest<SportInput>(
            testClient,
            "$SPORT_PATH$sportID",
            SportInput(name = name),
            authHeader(GUEST_TOKEN),
            expectedStatus = Response::expectBadRequest
        )
    }
}
