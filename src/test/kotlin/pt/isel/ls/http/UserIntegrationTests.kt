package pt.isel.ls.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.Test
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.entities.User
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.guestUser
import kotlin.test.assertEquals

class UserIntegrationTests {

    private val testUser = User(
        name="test",
        email="test@gmail.com",
        id="1234567"
    )

    private val testDataMem = UserDataMemRepository(testUser)
    private val userRoutes = UserRoutes(UserServices(testDataMem))
    private val backend = getApiRoutes(userRoutes.handler)

    private fun Response.expectOK(): Response {
        assertEquals(this.status,Status.OK)
        return this
    }

    // Get User Details
    @Test fun `get a specific user sucessfully`() {
        val baseRequest = Request(Method.GET, "/api/users/${testUser.id}")
        val response = backend(baseRequest).expectOK()
        val userFromBody = Json.decodeFromString<User>(response.bodyString())

        assertEquals(testUser, userFromBody)
    }

    @Test fun `get a user that does not exist gives status 404`(){
        val baseRequest = Request(Method.GET, "/api/users/qwiequiwe")
        assertEquals(Status.NOT_FOUND, backend(baseRequest).status)
    }

}

