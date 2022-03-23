package pt.isel.ls.repository.memory

import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.User

typealias UserID = String
typealias UserToken = String

const val USER_TOKEN = "TOKEN"

class UserDataMemRepository(guest: User): UserRepository {

    private val tokenTable = mutableMapOf<UserToken,UserID>( USER_TOKEN to guest.id)

    private val usersMap: MutableMap<UserID, User> = mutableMapOf(guest.id to guest)

    //override fun hasUser(id: UserId) = usersMap.contains(id)
    override fun userHasRepeatedEmail(userId: UserID, email : User.Email) : Boolean{
        return usersMap.values.any { it.email == email }
    }

    override fun addUser(newUser: User, userId: UserID, userAuthToken: UserToken) {
        usersMap[userId] = newUser
        tokenTable[userAuthToken] = userId
    }

    /**
     * @id user's unique identifier
     * @return An [User] object or null if there is no user identified by [id]
     */
    override fun getUserByID(id: UserID): User? = usersMap[id]

    /**
     * Gets all the users stored
     */
    override fun getUsers(): List<User> = usersMap.values.toList()

    override fun getUserIDByToken(token: UserToken): UserID? = tokenTable[token]

}