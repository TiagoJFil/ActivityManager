package pt.isel.ls.repository.db

import pt.isel.ls.entities.User
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

class UserDbRepository : UserRepository {

    override fun getUserByID(id: String): User? {
        TODO("Not yet implemented")
    }

    override fun addUser(newUser: User, userId: UserID, userAuthToken: UserToken) {
        TODO("Not yet implemented")
    }

    override fun getUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override fun userHasRepeatedEmail(userId: UserID, email: User.Email): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUserIDByToken(token: UserToken): UserID? {
        TODO("Not yet implemented")
    }


}