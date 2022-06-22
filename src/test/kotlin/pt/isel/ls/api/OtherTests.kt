package pt.isel.ls.api

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test
import pt.isel.ls.api.utils.TEST_ENV
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
}
