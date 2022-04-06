package pt.isel.ls.api


import org.http4k.core.Response
import org.junit.After
import pt.isel.ls.services.dto.SportDTO
import pt.isel.ls.api.SportRoutes.*
import pt.isel.ls.api.utils.*
import pt.isel.ls.services.dto.HttpError
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testSport
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class SportApiTests {
    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV))
    }

    @Test fun `get sports without creating returns a list with the testSport list`(){

        val sportList = getRequest<SportList>(testClient, SPORT_PATH, Response::expectOK)
        assertEquals(listOf(testSport.toDTO()), sportList.sports)
    }

    @Test fun `get a specific sport sucessfully`() {
        val sport = testClient.createSport(SportCreationBody("Football", "Game played with feet.")).sportID
        getRequest<SportDTO>(testClient, "${SPORT_PATH}${sport}", Response::expectOK)
    }

    @Test fun `get not found error trying to get a sport that does not exist`(){
        val id = 12354
        getRequest<HttpError>(testClient, "${SPORT_PATH}${id}", Response::expectNotFound)
    }

    @Test fun `create a sport sucessfully`(){

        testClient.createSport(SportCreationBody("Basketball", "Game played with hands."))

    }

    @Test fun `create a sport without description is allowed`(){

        testClient.createSport(SportCreationBody("Basketball"))

    }

    @Test fun `create a sport with a blank description is allowed`(){

        testClient.createSport(SportCreationBody("Basketball", ""))

    }

    @Test fun `create a sport without name is not allowed giving bad request`(){

        postRequest<SportCreationBody, HttpError>(
            testClient,
            SPORT_PATH,
            SportCreationBody(description = ""),
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )

    }

    @Test fun `create a sport with a blank name is not allowed giving bad request`(){

        postRequest<SportCreationBody, HttpError>(
            testClient,
            SPORT_PATH,
            SportCreationBody(name="", description = ""),
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )

    }

    @Test fun `create multiple sports ensuring they're in the list of routes`(){

        val name = "Cricket"
        val description = "Played with a cue"

        val creationBodies = List(1000){SportCreationBody(name, description)}
        val sportsIds: List<String> = creationBodies.map { testClient.createSport(it).sportID }

        val expected = sportsIds.map { SportDTO(id=it, name, description, guestUser.id) }
        val sportList = getRequest<SportList>(testClient, SPORT_PATH, Response::expectOK).sports

        expected.forEach { assertContains(sportList, it) }

    }


}