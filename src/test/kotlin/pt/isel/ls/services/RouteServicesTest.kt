package pt.isel.ls.services


import org.junit.Test
import pt.isel.ls.entities.Route
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.utils.RouteID
import pt.isel.ls.utils.guestUser
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RouteServicesTest {


    val routeServices = RouteServices(RouteDataMemRepository())
    val userMem = UserDataMemRepository(guestUser)


    @Test
    fun `get routes without creating returns an empty list`(){
        assertEquals(emptyList(), routeServices.getRoutes())
    }

    @Test
    fun `get a route that doesnt exist throws an error `(){
        assertFailsWith<IllegalStateException> { routeServices.getRoute("312") }
    }

    @Test
    fun `get a route without an id throws error `(){
        assertFailsWith<IllegalArgumentException> { routeServices.getRoute(" ") }
    }

    @Test
    fun `get a route without an argument throws error `(){
        assertFailsWith<IllegalArgumentException> { routeServices.getRoute(null) }
    }

    @Test
    fun `create a route`(){
        val routeID: RouteID =
            routeServices.createRoute(userId = guestUser.id, startLocation = "a", endLocation = "b", distance = 10.0)

        val routeCreated = routeServices.getRoute(routeID)
        val routeExpected = Route(id = routeID, user = guestUser.id, startLocation = "a", endLocation = "b", distance = 10.0)
        assertEquals(routeExpected,routeCreated)
    }

    @Test
    fun `create a route with an invalid start location`(){
        assertFailsWith<IllegalArgumentException> {
            routeServices.createRoute(userId = guestUser.id, startLocation = " ", endLocation = "b", distance = 10.0)
        }
    }

    @Test
    fun `create a route with an invalid end location`(){
        assertFailsWith<IllegalArgumentException> {
            routeServices.createRoute(userId = guestUser.id, startLocation = "a", endLocation = null, distance = 10.0)
        }
    }

    @Test
    fun `create a route with an invalid distance`(){
        assertFailsWith<IllegalArgumentException> {
            routeServices.createRoute(userId = guestUser.id, startLocation = "a", endLocation = "b", distance = null)
        }
    }
}