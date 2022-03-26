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
    private val sportsPath = "/api/sports/"
    private val testDataMem = SportDataMemRepository()
    private val userTestDataMem = UserDataMemRepository(guestUser)
    private val userServices = UserServices(userTestDataMem)
    private val sportServices = SportsServices(testDataMem)
    private val activityServices = ActivityServices(ActivityDataMemRepository())
    private val backend = getApiRoutes(Sport(sportServices,userServices, activityServices))


    @Test
    fun `get sports without creating returns empty list`(){
        val sportList = getRequest<SportList>(backend, sportsPath, Response::expectOK)
        assertEquals(emptyList(), sportList.sports)
    }

    @Test
    fun `get a specific sport sucessfully`() {
        val sportID = createSport(SportCreationBody("Football", "Game played with feet."))
        getRequest<Sport>(backend, "${sportsPath}${sportID}", Response::expectOK)
    }

    @Test fun `get not found error trying to get a sport that does not exist`(){
        val id = 12354
        getRequest<HttpError>(backend, "${sportsPath}${id}", Response::expectNotFound)
    }

    @Test fun `create a sport sucessfully`(){

        createSport(SportCreationBody("Basketball", "Game played with hands."))

    }

    @Test fun `create a sport without description is allowed`(){

        createSport(SportCreationBody("Basketball"))

    }

    @Test fun `create a sport with a blank description is allowed`(){

        createSport(SportCreationBody("Basketball", ""))

    }

    @Test fun `create a sport without name is not allowed giving bad request`(){

        postRequest<SportCreationBody, HttpError>(
            backend,
            sportsPath,
            SportCreationBody(description = ""),
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )

    }

    @Test fun `create a sport with a blank name is not allowed giving bad request`(){

        postRequest<SportCreationBody, HttpError>(
            backend,
            sportsPath,
            SportCreationBody(name="", description = ""),
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )

    }

    @Test fun `create multiple sports ensuring they're in the list of routes`(){

        val name = "Cricket"
        val description = "Played with a cue"

        val creationBodies = List(1000){SportCreationBody(name, description)}
        val sportsIds: List<String> = creationBodies.map { createSport(it).sportID }

        val expected = sportsIds.map { Sport(id=it, name, description, guestUser.id) }
        val sportList = getRequest<SportList>(backend, sportsPath, Response::expectOK).sports

        expected.forEach { assertContains(sportList, it) }

    }


    /**
     * Helper function to create a sport, ensures it is created and returns the respective [SportIDResponse]
     * @param sportCreationBody the body of the sport to be created. Must be valid.
     */
    private fun createSport(sportCreationBody: SportCreationBody): SportIDResponse
        = postRequest(
            backend,
            sportsPath,
            sportCreationBody,
            headers = authHeader(GUEST_TOKEN),
            Response::expectCreated
        )




}