package pt.isel.ls.http

import kotlinx.serialization.Serializable
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.Before
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.entities.User
import pt.isel.ls.http.SportRoutes.SportCreationBody
import pt.isel.ls.http.UserRoutes.*
import pt.isel.ls.http.utils.*
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import pt.isel.ls.services.ActivityServices
import pt.isel.ls.services.UserServices
import pt.isel.ls.services.generateRandomId
import pt.isel.ls.utils.GUEST_TOKEN
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class UserIntegrationTests {

    private val userPath = "/api/users/"
    private val testUser = User(
        name="test",
        email= User.Email("test@gmail.com"),
        id="1234567"
    )

    private val testDataMem = UserDataMemRepository(testUser)
    private val userServices = UserServices(testDataMem)
    private val activityServices = ActivityServices(ActivityDataMemRepository())
    private val backend = getApiRoutes(User(userServices, activityServices))

    @Test fun `create multiple Users`(){
        val userCount = 1000
        val randomEmails = (0 until userCount).map { "${generateRandomId()}@gmail.com" }
        val usersCreationBody = List(userCount){idx -> UserCreationBody("user$idx", randomEmails[idx])}
        val responses = usersCreationBody.map{
            createUser(it)
        }

        val usersList = getRequest<UserList>(backend, userPath, Response::expectOK).users
        val expected = responses.mapIndexed { index, userIDResponse ->
            User("user$index", User.Email(randomEmails[index]), userIDResponse.id)
        }

        expected.forEach{
            assertContains(usersList, it)
        }
    }


    // Get User Details
    @Test fun `get a specific user sucessfully`() {
        val user = getRequest<User>(backend, "$userPath${testUser.id}", Response::expectOK)
        assertEquals(testUser, user)
    }

    @Test fun `get a user that does not exist gives status 404`(){
        getRequest<HttpError>(backend, "${userPath}qwiequiwe", Response::expectNotFound)
    }


    //USER CREATE
    @Test fun `create a correct user gives 201`(){
        createUser(UserCreationBody("abc","abc@gmail.com"))
    }

    @Test fun `try to create a user without the name gives 400`(){
        val body = UserCreationBody(email = "abc@gmail.com")
        postRequest<UserCreationBody, HttpError>(backend,userPath, body, authHeader(GUEST_TOKEN), Response::expectBadRequest)
    }

    @Test fun `try to create a user without the email gives 400`(){
        val body = UserCreationBody(name= "Maria")
        postRequest<UserCreationBody, HttpError>(backend,userPath, body, authHeader(GUEST_TOKEN), Response::expectBadRequest)
    }

    @Test fun `create a user with a repeated email gives 400`(){
        val body = UserCreationBody("Maria", testUser.email.value)
        postRequest<UserCreationBody, HttpError>(backend,userPath, body, authHeader(GUEST_TOKEN), Response::expectBadRequest)
    }

    @Test fun `create a user with an extra parameter gives 400`(){
        @Serializable
        data class ExtraParam(val name:String?, val email:String?, val extra: String)
        val body = ExtraParam("Manel", "test123@gmail.com", "teste")
        postRequest<ExtraParam, HttpError>(backend,userPath, body, authHeader(GUEST_TOKEN), Response::expectBadRequest)
    }

    @Test fun `create a user with a wrong email gives 400`(){
        val body = UserCreationBody("Maria", "tes@t123@gmail.com")
        postRequest<UserCreationBody, HttpError>(backend,userPath, body, authHeader(GUEST_TOKEN), Response::expectBadRequest)
    }

    @Test fun `create a user with a blank name parameter gives 400`(){
        val body = UserCreationBody("", "test1234@gmail.com")
        postRequest<UserCreationBody, HttpError>(backend,userPath, body, authHeader(GUEST_TOKEN), Response::expectBadRequest)
    }

    @Test fun `create a user with a blank email parameter gives 400`(){
        val body = UserCreationBody("Mario", "")
        postRequest<UserCreationBody, HttpError>(backend,userPath, body, authHeader(GUEST_TOKEN), Response::expectBadRequest)
    }

    @Before
    @Test fun `a get a list without creating gives a list with the test user`(){
        val usersList = getRequest<UserList>(backend, userPath, Response::expectOK)
        assertEquals(listOf(testUser), usersList.users)
    }

    private fun createUser(userCreationBody: UserCreationBody): UserIDResponse =
            postRequest(
                    backend,
                    userPath,
                    userCreationBody,
                    authHeader(GUEST_TOKEN),
                    Response::expectCreated
            )
    }

}

