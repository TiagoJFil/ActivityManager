package pt.isel.ls.repository.database

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.entities.User
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

class UserDBRepository(private val dataSource: PGSimpleDataSource) : UserRepository {

    /**
     * Returns the user with the given id.
     * @param userID the id of the user to be returned.
     * @return the user with the given id.
     */
    override fun getUserByID(userID: String): User? {
        TODO("Not yet implemented")
    }

    /**
     * Adds a new user to the repository.
     * @param userName the user to be added.
     * @param email the email of the user to be added.
     * @param userId the id of the user to be added.
     * @param userAuthToken the authentication token of the user to be added.
     */
    override fun addUser(userName: String, email: User.Email, userId: UserID, userAuthToken: UserToken) {
        TODO("Not yet implemented")
    }

    /**
     * Gets all the users in the repository.
     */
    override fun getUsers(): List<User> {
        TODO("Not yet implemented")
    }

    /**
     * Checks if the specified user has a repeated email

     * @param email the user's email
     * @return [Boolean] true if another user already has the given email or false if it doesn't
     */
    override fun hasRepeatedEmail(email: User.Email): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Gets the user id by the given token
     * @param token the token of the user.
     * @return [UserID] the user id of the user with the given token.
     */
    override fun getUserIDByToken(token: UserToken): UserID? {
        TODO("Not yet implemented")
    }

    /**
     * Checks if the user with the given id exists.
     *
     * @param userID the id of the user to be checked.
     * @return [Boolean] true if the user exists or false if it doesn't.
     */
    override fun hasUser(userID: UserID): Boolean {
        TODO("Not yet implemented")
    }

}