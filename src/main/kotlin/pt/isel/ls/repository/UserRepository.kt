package pt.isel.ls.repository

import pt.isel.ls.entities.Email
import pt.isel.ls.entities.User
import pt.isel.ls.entities.UserID
import pt.isel.ls.entities.UserToken


interface UserRepository{

    //fun hasUser(id: UserId): Boolean

    fun getUserByID(id: String): User?
    fun addUser(newUser: User, userId: UserID, userAuthToken: UserToken)

    fun getUsers(): List<User>

    fun userHasRepeatedEmail(userId: UserID, email: Email): Boolean
}
