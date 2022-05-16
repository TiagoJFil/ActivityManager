package pt.isel.ls.api

import kotlinx.serialization.Serializable
import org.http4k.core.Response
import org.junit.After
import pt.isel.ls.api.UserRoutes.UserInput
import pt.isel.ls.api.UserRoutes.UserListOutput
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.api.utils.USER_PATH
import pt.isel.ls.api.utils.createUser
import pt.isel.ls.api.utils.expectBadRequest
import pt.isel.ls.api.utils.expectNotFound
import pt.isel.ls.api.utils.expectOK
import pt.isel.ls.api.utils.getRequest
import pt.isel.ls.api.utils.postRequest
import pt.isel.ls.config.guestUser
import pt.isel.ls.service.dto.HttpError
import pt.isel.ls.service.dto.UserDTO
import pt.isel.ls.utils.service.generateUUId
import pt.isel.ls.utils.service.toDTO
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class UserApiTests {
    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After
    fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV))
    }
    @Test
    fun `create multiple Users`() {
        val userCount = 1000
        val randomEmails = (0 until userCount).map { "${generateUUId()}@gmail.com" }
        val usersCreationBody = List(userCount) { idx -> UserInput("user$idx", randomEmails[idx]) }
        val responses = usersCreationBody.map {
            testClient.createUser(it)
        }

        val usersList = getRequest<UserListOutput>(testClient, "$USER_PATH?limit=1005", Response::expectOK).users
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
        getRequest<HttpError>(testClient, "${USER_PATH}90000", Response::expectNotFound)
    }

    @Test
    fun `get a user whose id is not an integer`() {
        getRequest<HttpError>(testClient, "${USER_PATH}qwiequiwe", Response::expectBadRequest)
    }

    // USER CREATE
    @Test
    fun `create a correct user gives 201`() {
        testClient.createUser(UserInput("abc", "abc@gmail.com"))
    }

    @Test
    fun `try to create a user without the name gives 400`() {
        val body = UserInput(email = "abc@gmail.com")
        postRequest<UserInput, HttpError>(
            testClient,
            USER_PATH,
            body,
            expectedStatus = Response::expectBadRequest
        )
    }

    @Test
    fun `try to create a user without the email gives 400`() {
        val body = UserInput(name = "Maria")
        postRequest<UserInput, HttpError>(
            testClient,
            USER_PATH,
            body,
            expectedStatus = Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a repeated email gives 400`() {
        val body = UserInput("Maria", guestUser.email.value)
        postRequest<UserInput, HttpError>(
            testClient,
            USER_PATH,
            body,
            expectedStatus = Response::expectBadRequest
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
            expectedStatus = Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a wrong email gives 400`() {
        val body = UserInput("Maria", "tes@t123@gmail.com")
        postRequest<UserInput, HttpError>(
            testClient,
            USER_PATH,
            body,
            expectedStatus = Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a blank name parameter gives 400`() {
        val body = UserInput("", "test1234@gmail.com")
        postRequest<UserInput, HttpError>(
            testClient,
            USER_PATH,
            body,
            expectedStatus = Response::expectBadRequest
        )
    }

    @Test
    fun `create a user with a blank email parameter gives 400`() {
        val body = UserInput("Mario", "")
        postRequest<UserInput, HttpError>(
            testClient,
            USER_PATH,
            body,
            expectedStatus = Response::expectBadRequest
        )
    }

    @Test
    fun `a get a list without creating gives a list with the test user`() {
        val usersList = getRequest<UserListOutput>(testClient, USER_PATH, Response::expectOK)
        assertEquals(listOf(guestUser.toDTO()), usersList.users)
    }
}
