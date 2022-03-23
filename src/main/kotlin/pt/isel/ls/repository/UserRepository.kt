package pt.isel.ls.repository


import pt.isel.ls.entities.User
import pt.isel.ls.repository.db.UserID
import pt.isel.ls.repository.db.UserToken


interface UserRepository{

    //fun hasUser(id: UserId): Boolean

    fun getUserByID(id: String): User?
    fun addUser(newUser: User, userId: UserID, userAuthToken: UserToken)

    fun getUsers(): List<User>

    fun userHasRepeatedEmail(userId: UserID, email: User.Email): Boolean

    fun getUserIDByToken(token: UserToken): UserID?
}

