package pt.isel.ls.services


import kotlinx.serialization.Serializable
import pt.isel.ls.entities.User
import pt.isel.ls.repository.UserRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import pt.isel.ls.entities.Email
import pt.isel.ls.repository.memory.UserId
import pt.isel.ls.repository.memory.UserToken


class UserServices(val userRepository: UserRepository) {

    /**
     * Verifies the parameters received and creates calls the function [UserRepository] to create a [User]
     * @return a pair of [Pair] with a [UserToken] and a [UserId]
     */
    fun createUser(name : String?, email: String?) : Pair<UserToken,UserId>{
        requireNotNull(name) {" Missing name."}
        requireNotNull(email) {" Missing email."}
        val userId = generateRandomId()
        val userAuthToken = generateUUId()

        val possibleEmail = Email(email)
        val user =  User(name,possibleEmail,userId)

        if(userRepository.userHasRepeatedEmail(userId,possibleEmail)) throw IllegalArgumentException(" Email already registered.")
        userRepository.addUser(user,userId,userAuthToken)

        return Pair(userAuthToken,userId)
    }

    fun getUserByID(id: UserId?): User {
        requireNotNull(id){" Parameter id is required. "}
        val user: User? = userRepository.getUserByID(id)
        checkNotNull(user){" User does not exist."}

        return user
    }

    fun getUsers(): List<User> = userRepository.getUsers()

}


