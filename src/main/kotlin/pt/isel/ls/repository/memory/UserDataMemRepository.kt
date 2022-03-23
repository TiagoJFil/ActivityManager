package pt.isel.ls.repository.memory


import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.User

const val USER_TOKEN = "TOKEN"

internal typealias UserToken = String
internal typealias UserID = String

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

    override fun getUserByID(id: UserID): User? = usersMap[id]

    override fun getUsers(): List<User> = usersMap.values.toList()

    override fun getUserIDByToken(token: UserToken): UserID? = tokenTable[token]

}