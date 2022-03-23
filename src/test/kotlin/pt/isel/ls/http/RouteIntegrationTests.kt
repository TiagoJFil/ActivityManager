package pt.isel.ls.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.Test
import pt.isel.ls.http.utils.expectNotFound
import pt.isel.ls.http.utils.expectOK
import pt.isel.ls.repository.memory.RouteDataMemRepository
import pt.isel.ls.services.RouteServices
import kotlin.test.assertEquals

class RouteIntegrationTests {
    private val userPath = "/api/routes/"
    private val testDataMem = RouteDataMemRepository()
    private val routeRoutes = RouteRoutes(RouteServices(testDataMem)).handler
    val backend = getApiRoutes(routeRoutes)

    @Test fun `get routes without creating returns empty list`(){

        val baseRequest = Request(Method.GET, userPath)

        val response = backend(baseRequest).expectOK()
        val routesList = Json.decodeFromString<RouteRoutes.RouteList>(response.bodyString())

        assertEquals(emptyList(), routesList.routes)
    }

    @Test fun `try to get a route that doesnt exist`(){
        val id = 123

        val baseRequest = Request(Method.GET, "${userPath}/${id}")

        val response = backend(baseRequest).expectNotFound()
    }
}