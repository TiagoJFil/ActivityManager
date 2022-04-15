package pt.isel.ls.repository

import pt.isel.ls.service.entities.User
import pt.isel.ls.service.entities.User.Email
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

interface UserRepository {

    /**
     * Adds a new user to the repository.
     * @param userName the user to be added.
     * @param email the email of the user to be added.
     * @param userAuthToken the authentication token of the user to be added.
     * @return the id of the user added.
     */
    fun addUser(userName: String, email: Email, userAuthToken: UserToken): UserID

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
     * Checks if the user with the given id exists.
     *
     * @param userID the id of the user to be checked.
     * @return [Boolean] true if the user exists or false if it doesn't.
     */
    fun hasUser(userID: UserID): Boolean

    /**
     * Returns the user with the given id.
     * @param userID the id of the user to be returned.
     * @return the user with the given id.
     */
    fun getUserBy(userID: UserID): User?

    /**
     * Gets the user id by the given token
     * @param token the token of the user.
     * @return [UserID] the user id of the user with the given token.
     */
    fun getUserIDBy(token: UserToken): UserID?
}
