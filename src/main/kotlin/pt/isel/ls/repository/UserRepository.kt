package pt.isel.ls.repository

import pt.isel.ls.services.entities.User
import pt.isel.ls.services.entities.User.Email
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

interface UserRepository {


    /**
     * Returns the user with the given id.
     * @param userID the id of the user to be returned.
     * @return the user with the given id.
     */
    fun getUserByID(userID: String): User?

    /**
     * Adds a new user to the repository.
     * @param userName the user to be added.
     * @param email the email of the user to be added.
     * @param userId the id of the user to be added.
     * @param userAuthToken the authentication token of the user to be added.
     */
    fun addUser(userName: String, email: Email, userId: UserID, userAuthToken: UserToken)

    /**
     * Gets all the users in the repository.
     */
    fun getUsers(): List<User>

    /**
     * Checks if the specified user has a repeated email

     * @param email the user's email
     * @return [Boolean] true if another user already has the given email or false if it doesn't
     */
    fun hasRepeatedEmail(email: Email): Boolean

    /**
     * Gets the user id by the given token
     * @param token the token of the user.
     * @return [UserID] the user id of the user with the given token.
     */
    fun getUserIDByToken(token: UserToken): UserID?

    /**
     * Checks if the user with the given id exists.
     *
     * @param userID the id of the user to be checked.
     * @return [Boolean] true if the user exists or false if it doesn't.
     */
    fun hasUser(userID: UserID): Boolean
}

