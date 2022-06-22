package pt.isel.ls.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test
import pt.isel.ls.api.utils.ROUTE_PATH
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.api.utils.expectBadRequest
import pt.isel.ls.service.dto.HttpError
import kotlin.test.assertEquals

class OtherTests {

    private val testCLient = getApiRoutes(getAppRoutes(TEST_ENV))

    companion object {
        private const val SWAGGER_URI = "/api/docs"
    }

    @Test
    fun `swagger ui is being served`() {
        val response = testCLient(Request(Method.GET, SWAGGER_URI))
        assertEquals(Status.FOUND, response.status)
    }

    @Test
    fun `Invalid Json gives status 400`() {
        val response = testCLient(Request(Method.POST, ROUTE_PATH).body("{\"name\":\"test\"}"))
            .expectBadRequest()
        val errorObject = Json.decodeFromString<HttpError>(response.bodyString())
        assertEquals(2090, errorObject.code)
    }
}
