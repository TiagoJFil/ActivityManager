package pt.isel.ls.services


import org.http4k.core.Status
import pt.isel.ls.entities.User
import pt.isel.ls.repository.UserRepository
import pt.isel.ls.entities.Email
import pt.isel.ls.entities.UserID
import pt.isel.ls.entities.UserToken



class UserServices(val userRepository: UserRepository) {

    /**
     * Verifies the parameters received and creates calls the function [UserRepository] to create a [User]
     * @return a pair of [Pair] with a [UserToken] and a [UserID]
     */
    fun createUser(name : String?, email: String?) : Pair<UserToken,UserID>{
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

    fun getUserByID(id: UserID?): User {
        requireNotNull(id){" Parameter id is required. "}
        val user: User? = userRepository.getUserByID(id)
        checkNotNull(user){" User does not exist."}

        return user
    }

    fun getUsers(): List<User> = userRepository.getUsers()

}



