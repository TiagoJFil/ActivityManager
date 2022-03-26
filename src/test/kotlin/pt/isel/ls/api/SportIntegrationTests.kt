package pt.isel.ls.api


import org.http4k.core.Response
import pt.isel.ls.entities.Sport
import pt.isel.ls.api.SportRoutes.*
import pt.isel.ls.api.utils.*
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.GUEST_TOKEN
import pt.isel.ls.utils.guestUser
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class SportIntegrationTests {
    private val testDataMem = SportDataMemRepository()
    private val userTestDataMem = UserDataMemRepository(guestUser)
    private val userServices = UserServices(userTestDataMem)
    private val sportServices = SportsServices(testDataMem)
    private val activityServices = ActivityServices(ActivityDataMemRepository(), UserDataMemRepository(guestUser))
    private val backend = getApiRoutes(Sport(sportServices,userServices, activityServices))


    @Test
    fun `get sports without creating returns empty list`(){
        val sportList = getRequest<SportList>(backend, SPORT_PATH, Response::expectOK)
        assertEquals(emptyList(), sportList.sports)
    }

    @Test
    fun `get a specific sport sucessfully`() {
        val sport = backend.createSport(SportCreationBody("Football", "Game played with feet.")).sportID
        getRequest<Sport>(backend, "${SPORT_PATH}${sport}", Response::expectOK)
    }

    @Test fun `get not found error trying to get a sport that does not exist`(){
        val id = 12354
        getRequest<HttpError>(backend, "${SPORT_PATH}${id}", Response::expectNotFound)
    }

    @Test fun `create a sport sucessfully`(){

        backend.createSport(SportCreationBody("Basketball", "Game played with hands."))

    }

    @Test fun `create a sport without description is allowed`(){

        backend.createSport(SportCreationBody("Basketball"))

    }

    @Test fun `create a sport with a blank description is allowed`(){

        backend.createSport(SportCreationBody("Basketball", ""))

    }

    @Test fun `create a sport without name is not allowed giving bad request`(){

        postRequest<SportCreationBody, HttpError>(
            backend,
            SPORT_PATH,
            SportCreationBody(description = ""),
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )

    }

    @Test fun `create a sport with a blank name is not allowed giving bad request`(){

        postRequest<SportCreationBody, HttpError>(
            backend,
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
        val sportsIds: List<String> = creationBodies.map { backend.createSport(it).sportID }

        val expected = sportsIds.map { Sport(id=it, name, description, guestUser.id) }
        val sportList = getRequest<SportList>(backend, SPORT_PATH, Response::expectOK).sports

        expected.forEach { assertContains(sportList, it) }

    }







}