package pt.isel.ls.services

import org.junit.Test
import pt.isel.ls.entities.User
import pt.isel.ls.repository.memory.UserDataMemRepository
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserServicesTest {

    private val testUser = User(
        name="test",
        email="test@gmail.com",
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

    @Test fun `get a user id without passing the id throws IllegalArgumentException`(){
        assertFailsWith<IllegalArgumentException> {
            userServices.getUserByID(null)
        }
    }
    /**/

}