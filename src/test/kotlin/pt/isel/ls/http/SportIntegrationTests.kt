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
import kotlin.test.assertEquals

class SportIntegrationTests {
    private val sportsPath = "/api/sports/"
    private val testDataMem = SportDataMemRepository()
    private val userTestDataMem = UserDataMemRepository(guestUser)
    private val userServices = UserServices(userTestDataMem)
    private val sportServices = SportsServices(testDataMem)
    private val routeRoutes =  sportsRoutes(sportServices,userServices)
    val backend = getApiRoutes(routeRoutes)


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

    @Test
    fun `get not found error trying to get a sport`(){
        val id = 12354
        getRequest<HttpError>(backend, "${sportsPath}${id}", Response::expectNotFound)
    }


}