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
            routeServices.createRoute(userId = guestUser.id, startLocation = "a", endLocation = "b", distance = 10.0)

        val routeCreated = routeServices.getRoute(routeID)
        val routeExpected = Route(id = routeID, user = guestUser.id, startLocation = "a", endLocation = "b", distance = 10.0)
        assertEquals(routeExpected,routeCreated)
    }

    @Test
    fun `create a route with an invalid start location`(){
        assertFailsWith<InvalidParameter> {
            routeServices.createRoute(userId = guestUser.id, startLocation = " ", endLocation = "b", distance = 10.0)
        }
    }

    @Test
    fun `create a route without end location`(){
        assertFailsWith<MissingParameter> {
            routeServices.createRoute(userId = guestUser.id, startLocation = "a", endLocation = null, distance = 10.0)
        }
    }

    @Test
    fun `create a route without distance`(){
        assertFailsWith<MissingParameter> {
            routeServices.createRoute(userId = guestUser.id, startLocation = "a", endLocation = "b", distance = null)
        }
    }
}