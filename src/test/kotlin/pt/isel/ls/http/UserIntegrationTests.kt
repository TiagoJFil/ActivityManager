package pt.isel.ls.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.Test
import pt.isel.ls.entities.Email
import pt.isel.ls.repository.memory.UserDataMemRepository
import pt.isel.ls.entities.User
import pt.isel.ls.http.utils.*
import pt.isel.ls.services.UserServices
import pt.isel.ls.utils.guestUser
import kotlin.test.assertEquals

class UserIntegrationTests {
    private val userPath = "/api/users/"
    private val testUser = User(
        name="test",
        email= Email("test@gmail.com"),
        id="1234567"
    )

    private val testDataMem = UserDataMemRepository(testUser)
    private val userRoutes = UserRoutes(UserServices(testDataMem))
    private val backend = getApiRoutes(userRoutes.handler)




    // Get User Details
    @Test fun `get a specific user sucessfully`() {
        val baseRequest = Request(Method.GET, "$userPath${testUser.id}")
        val response = backend(baseRequest).expectOK()
        val userFromBody = Json.decodeFromString<User>(response.bodyString())

        assertEquals(testUser, userFromBody)
    }

    @Test fun `get a user that does not exist gives status 404`(){
        val baseRequest = Request(Method.GET, "${userPath}qwiequiwe")
        backend(baseRequest).expectNotFound().expectMessage(" User does not exist.")
    }


    //USER CREATE
    @Test fun `create a correct user`(){
        val baseRequest = Request(Method.POST, userPath)
        val request = baseRequest.body("""{ "name": "abc", "email": "abc@gmail.com" }""")

        val response = backend(request).expectCreated()
    }
    @Test fun `try to create a user without the name`(){
        val baseRequest = Request(Method.POST, userPath)
        val request = baseRequest.body("""{ "email": "abc@gmail.com" }""")

        backend(request).expectBadRequest().expectMessage(" Missing name.")
    }

    @Test fun `try to create a user without the email`(){
        val baseRequest = Request(Method.POST, userPath)
        val request = baseRequest.body("""{  "name": "abc" }""")

        backend(request).expectBadRequest().expectMessage(" Missing email.")
    }

    @Test fun `create a user with a repeated email`(){
        val baseRequest = Request(Method.POST, userPath)
        val request = baseRequest.body("""{ "name": "test", "email": "test@gmail.com" }""")

        backend(request).expectBadRequest().expectMessage(" Email already registered.")
    }

    @Test fun `create a user with an extra parameter`(){
        val baseRequest = Request(Method.POST, userPath)
        val request = baseRequest.body("""{ "name": "test", "email": "test@gmail.com" , "teste" : "teste" }""")

        backend(request).expectBadRequest()
    }
    @Test fun `create a user with a wrong email`(){
        val baseRequest = Request(Method.POST, userPath)
        val request = baseRequest.body("""{ "name": "test", "email": "test@gm@ail.com"}""")

        val res =backend(request).expectBadRequest().expectMessage(" Email has the wrong format.")
    }

    @Test fun `create a user with a blank name parameter`(){
        val baseRequest = Request(Method.POST, userPath)
        val request = baseRequest.body("""{ "name": "", "email": "test@gmail.com" }""")

        backend(request).expectBadRequest().expectMessage(" Name field has no value")
    }

    @Test fun `create a user with a blank email parameter`(){
        val baseRequest = Request(Method.POST, userPath)
        val request = baseRequest.body("""{ "name": "abdc", "email": "  " }""")

        backend(request).expectBadRequest().expectMessage(" Email field has no value")
    }


    //Get Users
    @Test fun `get all users sucessfully`(){
        val baseRequest = Request(Method.GET, userPath)
        val response = backend(baseRequest).expectOK()
        val userListFromBody = Json.decodeFromString< UserRoutes.UserList>(response.bodyString())

        val userList = listOf(testUser)

        assertEquals(userList, userListFromBody.users)
    }
}

