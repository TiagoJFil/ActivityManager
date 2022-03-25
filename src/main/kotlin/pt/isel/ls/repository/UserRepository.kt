package pt.isel.ls.repository

import pt.isel.ls.entities.User
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

interface UserRepository {

    // fun hasUser(id: UserId): Boolean

    fun getUserByID(id: String): User?

    fun addUser(newUser: User, userAuthToken: UserToken)

    fun getUsers(): List<User>

    fun userHasRepeatedEmail(userId: UserID, email: User.Email): Boolean

    fun getUserIDByToken(token: UserToken): UserID?
}

