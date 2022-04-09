package pt.isel.ls.repository.memory

import pt.isel.ls.services.dto.UserDTO
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.services.entities.User
import pt.isel.ls.services.entities.User.Email
import pt.isel.ls.config.GUEST_TOKEN
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken


class UserDataMemRepository(guest: User) : UserRepository {

    /**
     * Mapping between a [UserToken] and a [UserID]
     */
    private val tokenTable: MutableMap<UserToken, UserID> = mutableMapOf(GUEST_TOKEN to guest.id)

    /**
     * Mapping between an email and a [UserID]
     */
    private val emailsMap: MutableMap<String, UserID> = mutableMapOf(guest.email.value to guest.id)

    /**
     * Mapping between a [UserID] and it's identified [User]
     */
    private val usersMap: MutableMap<UserID, User> = mutableMapOf(guest.id to guest)


    /**
    * Checks if the specified user has a repeated email
    *
    *  to check
    * @param email the user's email
    * @return [Boolean] true if another user already has the given email or false if it doesn't
    */
    override fun hasRepeatedEmail(email: Email): Boolean =
            emailsMap[email.value] != null


    /**
     * Adds a new user to the repository.
     * @param userName the user to be added.
     * @param email the email of the user to be added.
     * @param userAuthToken the authentication token of the user to be added.
     * @return the user's id.
     */
    override fun addUser(userName: String, email: Email, userAuthToken: UserToken): UserID {
        val userId = generateRandomId()
        val user = User(userName, email, userId)
        usersMap[userId] = user
        emailsMap[email.value] = userId
        tokenTable[userAuthToken] = userId
        return userId
    }

    /**
     * @param id user's unique identifier
     * @return A [UserDTO] object or null if there is no user identified by [id]
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

