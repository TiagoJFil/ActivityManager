package pt.isel.ls.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import pt.isel.ls.entities.User
import pt.isel.ls.http.utils.expectOK
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
        val baseRequest = Request(Method.GET, sportsPath)

        val response = backend(baseRequest).expectOK()
        val sportList = Json.decodeFromString<SportRoutes.SportList>(response.bodyString())

        assertEquals(emptyList(), sportList.routes)
    }
/*
    @Test
    fun `get a specific sport sucessfully`() {
        val sportID = sportServices.createSport()
        val baseRequest = Request(Method.GET, "$sportsPath${sportID}")
        val response = backend(baseRequest).expectOK()
        val sportFromBody = Json.decodeFromString<User>(response.bodyString())

        assertEquals(testUser, userFromBody)
    }
*/
}