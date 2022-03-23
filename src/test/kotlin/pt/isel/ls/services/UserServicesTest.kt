package pt.isel.ls.services

import org.junit.Test
import pt.isel.ls.entities.User
import pt.isel.ls.repository.memory.UserDataMemRepository
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserServicesTest {

    private val testUser = User(
        name="test",
        email= User.Email("test@gmail.com"),
        id="1234567"
    )
    private val userServices = UserServices(UserDataMemRepository(testUser))

    /* Get User Details */
    @Test
    fun `get a user by ID successfully`(){
        val sut = userServices.getUserByID(testUser.id)
        assertEquals(testUser, sut)
    }

    @Test
    fun `get an invalid user throws IllegalStateException`(){
        assertFailsWith<IllegalStateException> {
            userServices.getUserByID("INVALID_ID")
        }
    }

    @Test
    fun `get a user id without passing the id throws IllegalArgumentException`(){
        assertFailsWith<IllegalArgumentException> {
            userServices.getUserByID(null)
        }
    }

    @Test
    fun `create a user sucessfully`(){
        val sut = userServices.createUser("abc","abc@gmail.com")

        val user = userServices.getUserByID(sut.second)

        assertEquals("abc",user.name )
        assertEquals(User.Email("abc@gmail.com"),user.email)
    }

    @Test
    fun `cant create a user without a valid email`(){
        assertFailsWith<IllegalArgumentException> {
            userServices.createUser("abc", "abc@gm@a.il.com")
        }
    }

    @Test
    fun `cant create a user without an email`(){
        assertFailsWith<IllegalArgumentException> {
            userServices.createUser("abc", null)
        }
    }

    @Test
    fun `cant create a user with an empty string`(){
        assertFailsWith<IllegalArgumentException> {
            val sut = userServices.createUser("", null)
        }
    }

    @Test
    fun `cant create a user without a name`(){
        assertFailsWith<IllegalArgumentException> {
            userServices.createUser(null, "abc@gm@a.il.com")
        }
    }


    /**/

    @Test
    fun `get all the users list`(){
        val userList = userServices.getUsers()
        val testList = listOf(testUser)
        assertEquals(testList, userList)
    }

}