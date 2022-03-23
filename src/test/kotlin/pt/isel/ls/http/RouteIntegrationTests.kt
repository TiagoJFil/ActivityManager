package pt.isel.ls.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.Test
import pt.isel.ls.http.utils.expectOK
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.services.RouteServices
import kotlin.test.assertEquals

class RouteIntegrationTests {

    private val testDataMem = RouteDataMemRepository()
    private val routeRoutes = RouteRoutes(RouteServices(testDataMem)).handler
    val backend = getApiRoutes(routeRoutes)

    @Test fun `get routes without creating returns empty list`(){

        val baseRequest = Request(Method.GET, "/api/routes")

        val response = backend(baseRequest).expectOK()
        val routesList = Json.decodeFromString<RouteRoutes.RouteList>(response.bodyString())

        assertEquals(emptyList(), routesList.routes)
    }

}