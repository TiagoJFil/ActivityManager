package pt.isel.ls.repository.memory

import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.User

class UserDataMemRepository(guest: User? = null): UserRepository {

    private val usersMap: MutableMap<String, User> =
        if(guest != null) mutableMapOf(guest.id to guest) else mutableMapOf()

    override fun getUserByID(id: String): User? = usersMap[id]

    override fun getUsers(): List<User> = usersMap.values.toList()

}