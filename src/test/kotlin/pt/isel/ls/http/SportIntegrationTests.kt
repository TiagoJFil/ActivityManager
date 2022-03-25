package pt.isel.ls.http


import org.http4k.core.Response
import pt.isel.ls.entities.Sport
import pt.isel.ls.http.SportRoutes.*
import pt.isel.ls.http.utils.*
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.SportsServices
import pt.isel.ls.services.UserServices
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
    private val backend = getApiRoutes(Sport(sportServices,userServices))


    @Test
    fun `get sports without creating returns empty list`(){
        val sportList = getRequest<SportList>(backend, sportsPath, Response::expectOK)
        assertEquals(emptyList(), sportList.sports)
    }

    @Test
    fun `get a specific sport sucessfully`() {

        val sportID = postRequest<SportCreationBody, SportsIDResponse>(
            backend,
            sportsPath,
            SportCreationBody("Football", "Game played with feet."),
            Response::expectCreated
        ).sportID

        getRequest<Sport>(backend, "${sportsPath}${sportID}", Response::expectOK)
    }

    @Test fun `get not found error trying to get a sport that does not exist`(){
        val id = 12354
        getRequest<HttpError>(backend, "${sportsPath}${id}", Response::expectNotFound)
    }

    @Test fun `create a sport sucessfully`(){

        postRequest<SportCreationBody, SportsIDResponse>(
            backend,
            sportsPath,
            SportCreationBody("Basketball", "Game played with hands."),
            Response::expectCreated
        )

    }

    @Test fun `create a sport without description is allowed`(){

        postRequest<SportCreationBody, SportsIDResponse>(
            backend,
            sportsPath,
            SportCreationBody("Basketball"),
            Response::expectCreated
        )

    }

    @Test fun `create a sport with a blank description is allowed`(){

        postRequest<SportCreationBody, SportsIDResponse>(
            backend,
            sportsPath,
            SportCreationBody("Basketball", ""),
            Response::expectCreated
        )

    }

    @Test fun `create a sport without name is not allowed giving bad request`(){

        postRequest<SportCreationBody, HttpError>(
            backend,
            sportsPath,
            SportCreationBody(description = ""),
            Response::expectBadRequest
        )

    }

    @Test fun `create a sport with a blank name is not allowed giving bad request`(){

        postRequest<SportCreationBody, HttpError>(
            backend,
            sportsPath,
            SportCreationBody(name="", description = ""),
            Response::expectBadRequest
        )

    }

    @Test fun `create multiple sports ensuring they're in the list of routes`(){

        val name = "Cricket"
        val description = "Played with a cue"

        val creationBodies = List(1000){SportCreationBody(name, description)}
        val sportsIds: List<String> = creationBodies.map {
            postRequest<SportCreationBody, SportsIDResponse>(
                backend,
                sportsPath,
                it,
                Response::expectCreated
            ).sportID
        }

        val expected = sportsIds.map { Sport(id=it, name, description, guestUser.id) }
        val sportList = getRequest<SportList>(backend, sportsPath, Response::expectOK).sports

        expected.forEach { assertContains(sportList, it) }

    }


}