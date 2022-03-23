package pt.isel.ls.services

import org.junit.Test
import pt.isel.ls.http.routeRoutes
import pt.isel.ls.repository.memory.RouteDataMemRepository
import kotlin.test.assertEquals

class RouteServicesTest {


    val routeServices = RouteServices(RouteDataMemRepository())


    @Test
    fun `get a route without creating returns an empty list`(){

        assertEquals(emptyList(), routeServices.getRoutes())

    }

}