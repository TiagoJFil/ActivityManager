package pt.isel.ls.service

import org.junit.After
import org.junit.Test
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.config.guestUser
import pt.isel.ls.config.testRoute
import pt.isel.ls.service.dto.RouteDTO
import pt.isel.ls.service.entities.Route.Companion.MAX_LOCATION_LENGTH
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.service.toDTO
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RouteServicesTest {

    private var routeServices = TEST_ENV.routeServices

    @After
    fun tearDown() {
        routeServices = TEST_ENV.routeServices
    }

    @Test
    fun `get routes without creating returns a list with the testRoute`() {
        assertEquals(listOf(testRoute.toDTO()), routeServices.getRoutes(PaginationInfo(10, 0)))
    }

    @Test
    fun `get a route that doesnt exist throws an error `() {
        assertFailsWith<ResourceNotFound> { routeServices.getRoute("312") }
    }

    @Test
    fun `get a route with a blank id throws error `() {
        assertFailsWith<InvalidParameter> { routeServices.getRoute(" ") }
    }

    @Test
    fun `get a route without an argument throws error `() {
        assertFailsWith<MissingParameter> { routeServices.getRoute(null) }
    }

    @Test
    fun `create a route`() {
        val routeID: RouteID =
            routeServices.createRoute(token = GUEST_TOKEN, startLocation = "a", endLocation = "b", distance = 10.0)

        val routeCreated = routeServices.getRoute(routeID.toString())
        val routeExpected = RouteDTO(id = routeID, user = guestUser.id, startLocation = "a", endLocation = "b", distance = 10.0)
        assertEquals(routeExpected, routeCreated)
    }

    @Test
    fun `create a route with an invalid start location`() {
        assertFailsWith<InvalidParameter> {
            routeServices.createRoute(token = GUEST_TOKEN, startLocation = " ", endLocation = "b", distance = 10.0)
        }
    }

    @Test
    fun `create a route without end location`() {
        assertFailsWith<MissingParameter> {
            routeServices.createRoute(token = GUEST_TOKEN, startLocation = "a", endLocation = null, distance = 10.0)
        }
    }

    @Test
    fun `create a route without distance`() {
        assertFailsWith<MissingParameter> {
            routeServices.createRoute(token = GUEST_TOKEN, startLocation = "a", endLocation = "b", distance = null)
        }
    }

    @Test
    fun `update a route with more than MAX_LOCATION_LENGTH characters throws InvalidParameter`(){
        val name = "".padEnd(MAX_LOCATION_LENGTH + 1, 'a')

        assertFailsWith<InvalidParameter>{
            routeServices.updateRoute(token = GUEST_TOKEN, routeID = testRoute.id.toString(), startLocation = name, endLocation = "b", distance = 10.0)
        }
    }

    @Test
    fun `update a route with an invalid distance throws InvalidParameter`(){
        assertFailsWith<InvalidParameter>{
            routeServices.updateRoute(token = GUEST_TOKEN, routeID = testRoute.id.toString(), startLocation = "a", endLocation = "b", distance = -10.0)
        }
    }

    @Test
    fun `update a route without any parameters does not update anything`(){

        routeServices.updateRoute(token = GUEST_TOKEN, routeID = testRoute.id.toString(), startLocation = null, endLocation = null, distance = null)

        assertEquals(testRoute.toDTO(), routeServices.getRoute(testRoute.id.toString()))
    }

    @Test
    fun `update a route's distance only`() {
        val newDistance = 20.0

        routeServices.updateRoute(
            token = GUEST_TOKEN,
            routeID = testRoute.id.toString(),
            startLocation = null,
            endLocation = null,
            distance = newDistance
        )
        val updatedRoute = routeServices.getRoute(testRoute.id.toString())
        assertEquals(newDistance, updatedRoute.distance)
        assertEquals(testRoute.startLocation, updatedRoute.startLocation)
        assertEquals(testRoute.endLocation, updatedRoute.endLocation)
    }
}
