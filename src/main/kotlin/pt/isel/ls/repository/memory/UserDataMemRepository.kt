package pt.isel.ls.repository.memory

import pt.isel.ls.entities.Email
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.User


internal typealias UserId = String
internal typealias UserToken = String

class UserDataMemRepository(guest: User? = null): UserRepository {

    private val tokenTable = mutableMapOf<UserToken,UserId>()

    private val usersMap: MutableMap<UserId, User> =
        if(guest != null) mutableMapOf(guest.id to guest) else mutableMapOf()

    //override fun hasUser(id: UserId) = usersMap.contains(id)
    override fun userHasRepeatedEmail(userId: UserId, email : Email) : Boolean{
        return usersMap.values.any { it.email == email }
    }
    override fun addUser(newUser: User, userId: UserId, userAuthToken: UserToken) {
        usersMap[userId] = newUser
        tokenTable[userAuthToken] = userId
    }

    override fun getUserByID(id: UserId): User? = usersMap[id]

    override fun getUsers(): List<User> = usersMap.values.toList()

}