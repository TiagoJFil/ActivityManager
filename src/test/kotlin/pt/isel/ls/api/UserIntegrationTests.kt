package pt.isel.ls.api

import kotlinx.serialization.Serializable
import org.http4k.core.Response
import org.junit.After
import org.junit.Before
import pt.isel.ls.api.UserRoutes.*
import pt.isel.ls.api.utils.*
import pt.isel.ls.services.dto.HttpError
import pt.isel.ls.services.dto.UserDTO
import pt.isel.ls.services.dto.toDTO
import pt.isel.ls.services.generateRandomId
import pt.isel.ls.utils.GUEST_TOKEN
import pt.isel.ls.utils.guestUser
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class UserIntegrationTests {
    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After
    fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV))
    }
    @Test
    fun `create multiple Users`() {
        val userCount = 1000
        val randomEmails = (0 until userCount).map { "${generateRandomId()}@gmail.com" }
        val usersCreationBody = List(userCount) { idx -> UserCreationBody("user$idx", randomEmails[idx]) }
        val responses = usersCreationBody.map {
            testClient.createUser(it)
        }

        val usersList = getRequest<UserList>(testClient, USER_PATH, Response::expectOK).users
        val expected = responses.mapIndexed { index, userIDResponse ->
            UserDTO("user$index", randomEmails[index], userIDResponse.id)
        }

        expected.forEach {
            assertContains(usersList, it)
        }
    }


    // Get User Details
    @Test
    fun `get a specific user sucessfully`() {
        val user = getRequest<UserDTO>(testClient, "$USER_PATH${guestUser.id}", Response::expectOK)
        assertEquals(guestUser.toDTO(), user)
    }

    @Test
    fun `get a user that does not exist gives status 404`() {
        getRequest<HttpError>(testClient, "${USER_PATH}qwiequiwe", Response::expectNotFound)
    }


    //USER CREATE
    @Test
    fun `create a correct user gives 201`() {
        testClient.createUser(UserCreationBody("abc", "abc@gmail.com"))
    }

    @Test
    fun `try to create a user without the name gives 400`() {
        val body = UserCreationBody(email = "abc@gmail.com")
        postRequest<UserCreationBody, HttpError>(
            testClient,
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
            testClient,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a repeated email gives 400`() {
        val body = UserCreationBody("Maria", guestUser.email.value)
        postRequest<UserCreationBody, HttpError>(
            testClient,
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
            testClient,
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
            testClient,
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
            testClient,
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
            testClient,
            USER_PATH,
            body,
            authHeader(GUEST_TOKEN),
            Response::expectBadRequest
        )
    }

    @Before
    @Test
    fun `a get a list without creating gives a list with the test user`() {
        val usersList = getRequest<UserList>(testClient, USER_PATH, Response::expectOK)
        assertEquals(listOf(guestUser.toDTO()), usersList.users)
    }

}

