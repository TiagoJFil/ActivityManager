package pt.isel.ls.repository.memory

import pt.isel.ls.services.dto.UserDTO
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.entities.User
import pt.isel.ls.services.entities.User.Email
import pt.isel.ls.utils.GUEST_TOKEN
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken


class UserDataMemRepository(guest: User) : UserRepository {

    /**
     * Mapping between a token and a user id
     */
    private val tokenTable = mutableMapOf<UserToken, UserID>(GUEST_TOKEN to guest.id)

    /**
     * Mapping between a user id and it's identified user
     */
    private val usersMap: MutableMap<UserID, User> = mutableMapOf(guest.id to guest)

    /**
     * Checks if the specified user has a repeated email
     *
     * @param userId the unique id that identifies an user
     * @param email the user's email
     * @return [Boolean] true if another user already has the given email or false if it doesn't
     */
    override fun userHasRepeatedEmail(userId: UserID, email: User.Email): Boolean {
        return usersMap.values.any { it.email == email }
    }

    /**
     * Adds a new user to the repository.
     * @param userName the user to be added.
     * @param email the email of the user to be added.
     * @param userId the id of the user to be added.
     * @param userAuthToken the authentication token of the user to be added.
     */
    override fun addUser(userName: String, email: Email, userId: UserID, userAuthToken: UserToken) {
        val user = User(userName, email, userId)
        usersMap[userId] = user
        emailsMap[email.value] = userId
        tokenTable[userAuthToken] = userId
    }

    /**
     * @param id user's unique identifier
     * @return A [User] object or null if there is no user identified by [id]
     */
    override fun getUserByID(userID: UserID): User? = usersMap[userID]

    /**
     * Gets all the users stored
     */
    override fun getUsers(): List<User> = usersMap.values.toList()

    /**
     * @param token user's unique token
     * @return the [UserID] identified by the [UserToken]
     */
    override fun getUserIDByToken(token: UserToken): UserID? = tokenTable[token]

    /**
     * Checks if the user with the given id exists
     */
    override fun hasUser(userID: UserID): Boolean = usersMap.containsKey(userID)
}