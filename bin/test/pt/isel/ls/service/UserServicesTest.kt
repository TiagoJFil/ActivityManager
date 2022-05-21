package pt.isel.ls.service

import org.junit.After
import org.junit.Test
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.config.guestUser
import pt.isel.ls.utils.api.PaginationInfo
import pt.isel.ls.utils.service.toDTO
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserServicesTest {
    private var userServices = TEST_ENV.userServices

    @After
    fun tearDown() {
        userServices = TEST_ENV.userServices
    }

    /* Get User Details */
    @Test
    fun `get a user by ID successfully`() {
        val sut = userServices.getUserByID(guestUser.id.toString())
        assertEquals(guestUser.toDTO(), sut)
    }

    @Test
    fun `get an invalid user throws resource not found`() {
        assertFailsWith<ResourceNotFound> {
            userServices.getUserByID("13241234")
        }
    }

    @Test
    fun `get a user id without passing the id throws missing parameter`() {
        assertFailsWith<MissingParameter> {
            userServices.getUserByID(null)
        }
    }

    @Test
    fun `create a user sucessfully`() {
        val sut = userServices.createUser("abc", "abc@gmail.com")

        val user = userServices.getUserByID(sut.second.toString())

        assertEquals("abc", user.name)
        assertEquals("abc@gmail.com", user.email)
    }

    @Test
    fun `cant create a user without a valid email`() {
        assertFailsWith<InvalidParameter> {
            userServices.createUser("abc", "abc@gm@a.il.com")
        }
    }

    @Test
    fun `cant create a user without an email`() {
        assertFailsWith<MissingParameter> {
            userServices.createUser("abc", null)
        }
    }

    @Test
    fun `cant create a user with an empty string`() {
        assertFailsWith<InvalidParameter> {
            userServices.createUser("", "emailteste@hotmail.com")
        }
    }

    @Test
    fun `cant create a user without a name`() {
        assertFailsWith<MissingParameter> {
            userServices.createUser(null, "emailteste12@hotmail.com")
        }
    }

    @Test
    fun `get all the users list returns empty list`() {
        val userList = userServices.getUsers(PaginationInfo(10, 0))
        val testList = listOf(guestUser.toDTO())
        assertEquals(testList, userList)
    }

    @Test
    fun `get all users list returns a list with one user`() {
        val newUserId = userServices.createUser("abc", "abc@gmail.com").second
        val newUser = userServices.getUserByID(newUserId.toString())

        val userListReceived = userServices.getUsers(PaginationInfo(10, 0))
        val testListExpected = listOf(guestUser.toDTO(), newUser)
        assertEquals(testListExpected, userListReceived)
    }

    @Test
    fun `Cant create a user with a name longer than 20 letters`() {
        assertFailsWith<InvalidParameter> {
            userServices.createUser("abcdefghijklmnopqrstuvwxyz", "asd@gma.com")
        }
    }

    @Test
    fun `create a user and login and get it's token for auth`() {
        val (expectedToken, userId) = userServices.createUser("abc", "randomEmail@email.com")
        val actualToken = userServices.getTokenByEmail("randomEmail@email.com")
        val user = userServices.getUserByID(userId.toString())

        assertEquals(user.email, "randomEmail@email.com")

        assertEquals(expectedToken, actualToken)
    }

    @Test
    fun `get a token for a email that does not exist gives an invalid parameter`() {
        assertFailsWith<InvalidParameter> {
            userServices.getTokenByEmail("random@email")
        }
    }

    @Test
    fun `get a token for an invalid  email  gives an invalid parameter`() {
        assertFailsWith<InvalidParameter> {
            userServices.getTokenByEmail("invalidEmail")
        }
    }
}
