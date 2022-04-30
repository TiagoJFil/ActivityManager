/*package pt.isel.ls.db

import pt.isel.ls.api.User
import pt.isel.ls.api.getApiRoutes
import kotlinx.serialization.Serializable
import org.http4k.client.ApacheClient
import org.http4k.core.Response
import org.http4k.routing.RoutingHttpHandler
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.junit.runners.Parameterized
import pt.isel.ls.api.UserRoutes.*
import pt.isel.ls.api.getAppRoutes
import pt.isel.ls.api.utils.*
import pt.isel.ls.entities.HttpError
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.entities.User
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.repository.memory.SportDataMemRepository
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.services.generateRandomId
import pt.isel.ls.config.GUEST_TOKEN
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UserIntegrationTests(private val backend: RoutingHttpHandler) {

    companion object{
        private val testUser = User(
            name = "test",
            email = User.Email("test@gmail.com"),
            id = "1234567"
        )

        @JvmStatic
        @Parameterized.Parameters(name= "{index}")
        fun data(): Collection<Array<Any>> {


            return listOf(
                arrayOf(getApiRoutes(getAppRoutes(TEST_ENV))),
                arrayOf(getApiRoutes(getAppRoutes(INTEGRATION_TEST_ENV)))
            )

        }
    }

    @Test
    fun `1 get a list without creating gives a list with the test user`() {
        val usersList = getRequest<UserList>(backend, USER_PATH, Response::expectOK)
        assertEquals(listOf(testUser), usersList.users)
    }

    @Test
    fun `2 get a specific user sucessfully`() {
        val user = getRequest<User>(backend, "$USER_PATH${testUser.id}", Response::expectOK)
        assertEquals(testUser, user)
    }

    @Test
    fun `3 get a user that does not exist gives status 404`() {
        getRequest<HttpError>(backend, "${USER_PATH}qwiequiwe", Response::expectNotFound)
    }

    @Test
    fun `create a correct user gives 201`() {
        backend.createUser(UserCreationBody("abc", "abc@gmail.com"))
    }

    @Test
    fun `create multiple Users`() {
        val userCount = 1000
        val randomEmails = (0 until userCount).map { "${generateRandomId()}@gmail.com" }
        val usersCreationBody = List(userCount) { idx -> UserCreationBody("user$idx", randomEmails[idx]) }
        val responses = usersCreationBody.map {
            backend.createUser(it)
        }

        val usersList = getRequest<UserList>(backend, USER_PATH, Response::expectOK).users
        val expected = responses.mapIndexed { index, userIDResponse ->
            User("user$index", User.Email(randomEmails[index]), userIDResponse.id)
        }

        expected.forEach {
            assertContains(usersList, it)
        }
    }

    @Test
    fun `try to create a user without the name gives 400`() {
        val body = UserCreationBody(email = "abc@gmail.com")
        postRequest<UserCreationBody, HttpError>(
            backend,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test
    fun `try to create a user without the email gives 400`() {
        val body = UserCreationBody(name = "Maria")
        postRequest<UserCreationBody, HttpError>(
            backend,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a repeated email gives 400`() {
        val body = UserCreationBody("Maria", testUser.email.value)
        postRequest<UserCreationBody, HttpError>(
            backend,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with an extra parameter gives 400`() {
        @Serializable
        data class ExtraParam(val name: String?, val email: String?, val extra: String)

        val body = ExtraParam("Manel", "test123@gmail.com", "teste")
        postRequest<ExtraParam, HttpError>(
            backend,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a wrong email gives 400`() {
        val body = UserCreationBody("Maria", "tes@t123@gmail.com")
        postRequest<UserCreationBody, HttpError>(
            backend,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a blank name parameter gives 400`() {
        val body = UserCreationBody("", "test1234@gmail.com")
        postRequest<UserCreationBody, HttpError>(
            backend,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a blank email parameter gives 400`() {
        val body = UserCreationBody("Mario", "")
        postRequest<UserCreationBody, HttpError>(
            backend,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

}

*/
