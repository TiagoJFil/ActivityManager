package pt.isel.ls.repository

import pt.isel.ls.entities.Email
import pt.isel.ls.entities.User
import pt.isel.ls.repository.memory.UserId
import pt.isel.ls.repository.memory.UserToken

interface UserRepository{

    //fun hasUser(id: UserId): Boolean

    fun getUserByID(id: String): User?
    fun addUser(newUser: User, userId: UserId, userAuthToken: UserToken)

    fun getUsers(): List<User>

    fun userHasRepeatedEmail(userId: UserId, email: Email): Boolean
}
