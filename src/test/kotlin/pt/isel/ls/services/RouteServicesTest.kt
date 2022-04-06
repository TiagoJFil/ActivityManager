package pt.isel.ls.services


import org.junit.After
import org.junit.Test
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.services.dto.RouteDTO


import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.utils.RouteID
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testRoute
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RouteServicesTest {

    private var routeServices = TEST_ENV.routeServices


    @After
    fun tearDown() {
        routeServices = TEST_ENV.routeServices
    }

    @Test
    fun `get routes without creating returns a list with the testRoute`(){
        assertEquals(listOf(testRoute.toDTO()), routeServices.getRoutes())
    }

    @Test
    fun `get a route that doesnt exist throws an error `(){
        assertFailsWith<ResourceNotFound> { routeServices.getRoute("312") }
    }

    @Test
    fun `get a route with a blank id throws error `(){
        assertFailsWith<InvalidParameter> { routeServices.getRoute(" ") }
    }

    @Test
    fun `get a route without an argument throws error `(){
        assertFailsWith<MissingParameter> { routeServices.getRoute(null) }
    }

    @Test
    fun `create a route`(){
        val routeID: RouteID =
            routeServices.createRoute(token = GUEST_TOKEN, startLocation = "a", endLocation = "b", distance = 10.0)

        val routeCreated = routeServices.getRoute(routeID)
        val routeExpected = RouteDTO(id = routeID, user = guestUser.id, startLocation = "a", endLocation = "b", distance = 10.0)
        assertEquals(routeExpected,routeCreated)
    }

    @Test
    fun `create a route with an invalid start location`(){
        assertFailsWith<InvalidParameter> {
            routeServices.createRoute(token = GUEST_TOKEN, startLocation = " ", endLocation = "b", distance = 10.0)
        }
    }

    @Test
    fun `create a route without end location`(){
        assertFailsWith<MissingParameter> {
            routeServices.createRoute(token = GUEST_TOKEN, startLocation = "a", endLocation = null, distance = 10.0)
        }
    }

    @Test
    fun `create a route without distance`(){
        assertFailsWith<MissingParameter> {
            routeServices.createRoute(token = GUEST_TOKEN, startLocation = "a", endLocation = "b", distance = null)
        }
    }
}