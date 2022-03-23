package pt.isel.ls.services

import org.junit.Test
import pt.isel.ls.http.routeRoutes
import pt.isel.ls.repository.memory.RouteDataMemRepository
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RouteServicesTest {


    val routeServices = RouteServices(RouteDataMemRepository())


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

}