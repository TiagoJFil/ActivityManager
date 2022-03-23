package pt.isel.ls.repository.memory

import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.User
import pt.isel.ls.entities.UserID
import pt.isel.ls.entities.UserToken


class UserDataMemRepository(guest: User? = null): UserRepository {

    private val tokenTable = mutableMapOf<UserToken,UserID>()


    val usersMap: MutableMap<UserID, User> =
        if(guest != null) mutableMapOf(guest.id to guest) else mutableMapOf()

    //override fun hasUser(id: UserId) = usersMap.contains(id)

    override fun userHasRepeatedEmail(userId: UserID, email : User.Email) : Boolean =
        usersMap.values.any { it.email == email }

    override fun addUser(newUser: User, userId: UserID, userAuthToken: UserToken) {
        usersMap[userId] = newUser
        tokenTable[userAuthToken] = userId
    }

    override fun getUserByID(id: UserID): User? = usersMap[id]

    override fun getUsers(): List<User> = usersMap.values.toList()

}