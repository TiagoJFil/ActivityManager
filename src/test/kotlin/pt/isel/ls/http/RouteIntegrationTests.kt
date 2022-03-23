package pt.isel.ls.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.Test
import pt.isel.ls.entities.Route
import pt.isel.ls.http.utils.expectBadRequest
import pt.isel.ls.http.utils.expectCreated
import pt.isel.ls.http.utils.expectMessage
import pt.isel.ls.http.utils.expectOK
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.services.*
import pt.isel.ls.utils.guestUser
import kotlin.test.assertEquals

class RouteIntegrationTests {

    private val testDataMem = RouteDataMemRepository()
    private val userTestDataMem = UserDataMemRepository(guestUser)
    private val routeRoutes = RouteRoutes(RouteServices(testDataMem), UserServices(userTestDataMem)).handler
    val backend = getApiRoutes(routeRoutes)

    @Test fun `get routes without creating returns empty list`(){

        val baseRequest = Request(Method.GET, "/api/routes")

        val response = backend(baseRequest).expectOK()
        val routesList = Json.decodeFromString<RouteRoutes.RouteList>(response.bodyString())

        assertEquals(emptyList(), routesList.routes)
    }

    @Test fun `create a route successfully`(){
        val baseRequest = Request(Method.POST, "/api/routes")
        val routeCreation =
            Json.encodeToString(RouteRoutes.RouteCreation(startLocation = "a", endLocation = "b", distance = 10.0))
        backend(baseRequest.body(routeCreation)).expectCreated()
    }


    //TODO(Autenthication)
    @Test fun `create a route without start location`(){
        val baseRequest = Request(Method.POST, "/api/routes")
        val routeCreation =
            Json.encodeToString(RouteRoutes.RouteCreation(endLocation = "b", distance = 10.0))
        backend(baseRequest.body(routeCreation)).expectBadRequest().expectMessage(START_LOCATION_REQUIRED)
    }

    @Test fun `create a route without end location`(){
        val baseRequest = Request(Method.POST, "/api/routes")
        val routeCreation =
            Json.encodeToString(RouteRoutes.RouteCreation(distance = 20.0, startLocation = "a"))
        backend(baseRequest.body(routeCreation)).expectBadRequest().expectMessage(END_LOCATION_REQUIRED)
    }

    @Test fun `create a route without distance`(){
        val baseRequest = Request(Method.POST, "/api/routes")
        val routeCreation =
            Json.encodeToString(RouteRoutes.RouteCreation(endLocation = "b", startLocation = "c"))
        backend(baseRequest.body(routeCreation)).expectBadRequest().expectMessage(DISTANCE_REQUIRED)
    }

    @Test fun `create a route with a blank parameter`(){
        val baseRequest = Request(Method.POST, "/api/routes")
        val routeCreation =
            Json.encodeToString(RouteRoutes.RouteCreation(startLocation = " ", endLocation = "b", distance = 10.0))
        backend(baseRequest.body(routeCreation)).expectBadRequest().expectMessage(START_LOCATION_REQUIRED)
    }


}