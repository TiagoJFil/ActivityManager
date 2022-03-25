package pt.isel.ls.services

import pt.isel.ls.entities.User
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.utils.UserID
import pt.isel.ls.utils.UserToken

const val NAME_REQUIRED = "Missing name."
const val EMAIL_REQUIRED = "Missing email."
const val NAME_NO_VALUE = "Name field has no value"
const val EMAIL_NO_VALUE = "Email field has no value"
const val EMAIL_ALREADY_EXISTS = "Email already registered."

const val ID_REQUIRED = "Parameter id is required."
const val USER_NOT_FOUND = "User does not exist."
class UserServices(val repository: UserRepository) {

    /**
     * Verifies the parameters received and creates calls the function [UserRepository] to create a [User]
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     * @throws IllegalArgumentException
     */
    fun createUser(name : String?, email: String?) : Pair<UserToken,UserID>{
        requireNotNull(name) { NAME_REQUIRED }
        requireNotNull(email) { EMAIL_REQUIRED }
        require(email.isNotBlank()) { EMAIL_NO_VALUE }
        require(name.isNotBlank()) { NAME_NO_VALUE }
        val userId = generateRandomId()
        val userAuthToken = generateUUId()

        val possibleEmail = User.Email(email)
        val user = User(name, possibleEmail, userId)

        if(repository.userHasRepeatedEmail(userId,possibleEmail)) throw IllegalArgumentException( EMAIL_ALREADY_EXISTS)
        repository.addUser(user,userAuthToken)

        return Pair(userAuthToken, userId)
    }

    /**
     *
     */
    fun getUserByID(id: UserID?): User {
        requireNotNull(id){ ID_REQUIRED }
        val user: User? = repository.getUserByID(id)
        checkNotNull(user){ USER_NOT_FOUND }

        return user
    }

    fun getUsers(): List<User> = repository.getUsers()

    fun getUserByToken(token: UserToken): UserID =
        repository.getUserIDByToken(token) ?: throw IllegalAccessException("Invalid Token")
}
