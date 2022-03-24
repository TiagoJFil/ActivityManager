package pt.isel.ls.repository.memory

import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.User

typealias UserID = String
typealias UserToken = String
const val USER_TOKEN = "TOKEN"

class UserDataMemRepository(guest: User): UserRepository {

    private val tokenTable = mutableMapOf<UserToken,UserID>( USER_TOKEN to guest.id)

    private val usersMap: MutableMap<UserID, User> = mutableMapOf(guest.id to guest)

    /**
     * Checks if the specified user has a repeated email
     *
     * @param userId the unique id that identifies an user
     * @param email the user's email
     * @return [Boolean] true if another user already has the given email or false if it doesn't
     */
    //override fun hasUser(id: UserId) = usersMap.contains(id)
    override fun userHasRepeatedEmail(userId: UserID, email : User.Email) : Boolean{
        return usersMap.values.any { it.email == email }
    }

    /**
     * Adds a new user.
     *
     * @param newUser the new [User] to add
     * @param userId the [UserID] of the user to add
     * @param userAuthToken the [UserToken] of the user to add
     */
    override fun addUser(newUser: User, userId: UserID, userAuthToken: UserToken) {
        usersMap[userId] = newUser
        tokenTable[userAuthToken] = userId
    }

    /**
     * @param id user's unique identifier
     * @param return An [User] object or null if there is no user identified by [id]
     */
    override fun getUserByID(id: UserID): User? = usersMap[id]

    /**
     * Gets all the users stored
     */
    override fun getUsers(): List<User> = usersMap.values.toList()

    /**
     * @param token user's unique token
     * @return the [UserID] identified by the [UserToken]
     */
    override fun getUserIDByToken(token: UserToken): UserID? = tokenTable[token]

}